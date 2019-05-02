
package com.visitor.game;

import com.visitor.card.properties.Activatable;
import com.visitor.card.properties.Triggering;
import com.visitor.card.types.Card;
import com.visitor.card.types.Junk;
import com.visitor.helpers.UUIDHelper;
import com.visitor.protocol.ServerGameMessages.*;
import com.visitor.protocol.Types;
import com.visitor.protocol.Types.GameState;
import com.visitor.protocol.Types.Phase;
import com.visitor.server.GameEndpoint;
import static com.visitor.protocol.Types.Phase.*;
import com.visitor.protocol.Types.SelectFromType;
import static com.visitor.protocol.Types.SelectFromType.*;
import com.visitor.helpers.Hashmap;
import java.io.IOException;
import static java.lang.Math.random;
import com.visitor.helpers.Arraylist;
import com.visitor.server.GeneralEndpoint;
import java.util.List;
import java.util.UUID;
import static java.util.UUID.randomUUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.websocket.EncodeException;

/**
 *
 * @author pseudo
 */
public class Game {
    
    Hashmap<String, Player> players;
    Hashmap<String, GameEndpoint> connections;
    Hashmap<String, ServerGameMessage> lastMessages;
    String turnPlayer;
    public String activePlayer;
    Arraylist<Card> stack;
    Phase phase;
    int turnCount;
    int passCount;
    UUID id;
    ArrayBlockingQueue<Object> response;
    Hashmap<String, Arraylist<Triggering>> triggeringCards;
    Arraylist<Event> eventQueue;
    boolean endProcessed;

    public Game (String p1, String p2) {
        id = randomUUID();
        players = new Hashmap<>();
        connections = new Hashmap<>();
        stack = new Arraylist<>();
        lastMessages = new Hashmap<>();
        response = new ArrayBlockingQueue<>(1);
        triggeringCards = new Hashmap<>();
        triggeringCards.put(p1, new Arraylist<>());
        triggeringCards.put(p2, new Arraylist<>());
        eventQueue = new Arraylist();
        
        players.putIn(p1, new Player(p1, TestDecks.randomDeck(p1)));
        players.putIn(p2, new Player(p2, TestDecks.randomDeck(p2)));
        
        System.out.println("Example Class Names");
        System.out.println("\tName: " + players.get(p1).deck.get(0).getClass().getName());
        System.out.println("\tCanonical Name: " + players.get(p1).deck.get(0).getClass().getCanonicalName());
        System.out.println("\tSimple Name: " + players.get(p1).deck.get(0).getClass().getSimpleName());
        System.out.println("\tType Name: " + players.get(p1).deck.get(0).getClass().getTypeName());
        
        players.get(p1).deck.shuffle();
        players.get(p2).deck.shuffle();
        
        phase = MULLIGAN;
        turnPlayer = (random() < 0.5)?p1:p2;
        activePlayer = turnPlayer;
        turnCount = 0;
        passCount = 0;
        players.get(p1).draw(5);
        players.get(p2).draw(5);
        updatePlayers();
    }

    public void addConnection(String username, GameEndpoint connection) {
        connections.putIn(username, connection);
    }

    public void removeConnection(String username) {
        connections.removeFrom(username);
    }

    public void setLastMessage(String username, ServerGameMessage lastMessage) {
        lastMessages.put(username, lastMessage);
    }

    public ServerGameMessage getLastMessage(String username) {
        return lastMessages.get(username);
    }

    public Card extractCard(UUID targetID) {
        for (Player player : players.values()) {
            Card c = player.extractCard(targetID);
            if (c != null){
                return c;
            }
        }
        for (Card c : stack){
            if (c.id.equals(targetID)){
                stack.remove(c);
                return c;
            }
        }
        return null;
    }
    
    public Card getCard(UUID targetID) {
        for (Player player : players.values()) {
            Card c = player.getCard(targetID);
            if (c != null){
                return c;
            }
        }
        for (Card c : stack){
            if (c.id.equals(targetID)){
                return c;
            }
        }
        return null;
    }
    
    public void playCard(String username, UUID cardID) {
        extractCard(cardID).play(this);
        activePlayer = getOpponentName(username); 
    }
    
    public void activateCard(String username, UUID cardID) {
        ((Activatable)getCard(cardID)).activate(this);
        activePlayer = getOpponentName(username); 
    }
    
    public void addToStack(Card c) {
        passCount = 0;
        stack.add(0, c);
    }

    public void studyCard(String username, UUID cardID) {
        extractCard(cardID).study(this);
    }
    
    public void processEvents(){
        while(!eventQueue.isEmpty()){
            Event e = eventQueue.remove(0);
            triggeringCards.get(turnPlayer).forEachInOrder(c ->{ c.checkEvent(this, e);});
            triggeringCards.get(getOpponentName(turnPlayer)).forEachInOrder(c ->{ c.checkEvent(this, e);});
        }
    }
    
    public void changePhase(){
        switch(phase) {
            case MULLIGAN:
                newTurn();
                break;
            case BEGIN:
                
                passCount = 0;
                activePlayer = turnPlayer;
                phase = MAIN;
                
                break;
            case MAIN:
                passCount = 0;
                activePlayer = " ";
                endTurn();
                newTurn();
                break;
            case END:
                break;
        }
    }
    
    private void endTurn() {
        if (!endProcessed){
            endProcessed = true;
            processEndEvents();
            resolveStack(); //TODO: figure out logic here
        }
        players.values().forEach(p->{ p.shield = 0; p.reflect = 0;});
        if(players.get(turnPlayer).hand.size() > 7){
            discard(turnPlayer, players.get(turnPlayer).hand.size()-7);
            
        }
    }
    
    private void newTurn(){
        phase = MAIN;
        if(turnCount > 0){
            turnPlayer = getOpponentName(turnPlayer);
            players.get(turnPlayer).draw(1);
        }
        activePlayer = turnPlayer;
        passCount = 0;
        players.get(turnPlayer).draw(1);
        players.get(turnPlayer).newTurn();
        turnCount++;
        processBeginEvents();
    }
    

    public String getOpponentName(String playerName){
        for(String name : players.keySet()){
            if(!name.equals(playerName)){
                return name;
            }
        }
        return null;
    }

    public void pass(String username) {
        passCount++;
        if (passCount == 2) {
            if (!stack.isEmpty()){
                resolveStack();
            } else {
                changePhase();
            }
        } else {
            activePlayer = getOpponentName(username);
        }
    }

    
    //This is resolve until something new is added version
    private void resolveStack() {
        activePlayer = " ";
        updatePlayers();    
        while (!stack.isEmpty() && passCount == 2) {
            Card c = stack.remove(0);
            c.resolve(this);
            int prevSize = stack.size();
            processEvents();
            if(stack.isEmpty() || prevSize != stack.size()){
                passCount = 0;
                activePlayer = turnPlayer;
            } else {
                updatePlayers();
            }
        }
    }
    
    /*
    // This is stop after each resolution version.
    private void resolveStack() {
        if (passCount == 2) {
            activePlayer = " ";
            updatePlayers();
            Card c = stack.remove(0);
            c.resolve(this);
            passCount = 0;
            activePlayer = turnPlayer;
        }
    }
    */

    public void mulligan(String username) {
        players.get(username).mulligan();
    }

    public void keep(String username) {
        passCount++;
        if (passCount == 2) {
            changePhase();
        } else {
            activePlayer = getOpponentName(username);
        }
    }
    
    //Eventually make this private.
    public Arraylist<Card> getZone(String username, String zone){
        switch(zone){
            case "deck":
                return players.get(username).deck;
            case "hand":
                return players.get(username).hand;
            case "play":
                return players.get(username).playArea;
            case "scrapyard":
                return players.get(username).scrapyard;
            case "void":
                return players.get(username).voidPile;
            case "stack":
                return stack;
            case "both play":
                Arraylist<Card> total = new Arraylist<>();
                total.addAll(players.get(username).playArea);
                total.addAll(players.get(getOpponentName(username)).playArea);
                return total;
            default:
                return null;
        }
    }
    
    public boolean hasValidTargetsIn(String username, Predicate<Card> validTarget, int count, String zone){
        return getZone(username, zone).parallelStream().filter(validTarget).count() >= count;
    }
    
     public boolean hasCardsIn(String username, String zone, int count) {
        return getZone(username, zone).size() >= count;
    }
    
    public void putTo(String username, Card c, String zone) {
        getZone(username, zone).add(c);
    }
    
    public void putTo(String username, Card c, String zone, int index) {
        getZone(username, zone).add(index, c);
    }
    
    public void addEnergy(String username, int i) {
        players.get(username).energy+=i;
    }
    
    public void spendEnergy(String username, int i) {
        players.get(username).energy-=i;
    }
    
    public void draw(String username, int count){
        Player player = players.get(username);
        player.draw(count);
        if (player.deck.isEmpty()){
            lose(username);
        }
    }
    
    public void drawByID(String username, UUID cardID) {
        players.get(username).hand.add(extractCard(id));
    }
    
    public void purgeByID(String username, UUID cardID) {
        players.get(username).voidPile.add(extractCard(id));
    }

    public void destroy(UUID id){
        Card item = extractCard(id);
        item.destroy(this);
    }
    
    public void loot(String username, int x) {
        draw(username, x);
        discard(username, x);
    }
    
    public void discard(String username, int count){
        players.get(username).discard(selectFromZone(username, "hand", c -> {return true;}, count, false));
    }
    
    public void discard(String username, UUID cardID){
        Arraylist<UUID> temp = new Arraylist<>();
        temp.add(cardID);
        players.get(username).discard(temp);
    }
    
    public void deplete(UUID id){
        getCard(id).depleted = true;
    }
    
    public void ready(UUID id){
        getCard(id).ready();
    }
    
    public boolean ownedByOpponent(UUID targetID) {
        Card c = getCard(targetID);
        return c.owner.equals(getOpponentName(c.controller));
    }
    
    public void purge(String username, int count){
        String current = username;
        Player player; 
        int ret = count;
        
        do {
            player = players.get(current);
            ret = player.dealDamage(this, ret);
            if (player.deck.isEmpty()){
                lose(current);
            }
            current = getOpponentName(current);
        } while(ret > 0);
    }
    
    public void purgeSelf(String username, int count){
        Player player = players.get(username);
        player.purgeSelf(count);
        if (player.deck.isEmpty()){
            lose(username);
        }
    }

    public void possessTo(String newController, UUID cardID, String zone) {
        Card c = extractCard(cardID);
        c.controller = newController;
        c.knowledge = new Hashmap<>();
        getZone(newController, zone).add(c);
    }

    public boolean controlsUnownedCard(String username, String zone) {
        return getZone(username, zone).parallelStream().anyMatch(c->{return ownedByOpponent(c.id);});
    }

    public boolean isIn(String username, UUID cardID, String zone) {
        return getZone(username, zone).parallelStream().anyMatch(getCard(cardID)::equals);
    }
    
    public boolean hasInstancesIn(String username, Class c, String zone, int count) {
        return getZone(username, zone).parallelStream().filter(c::isInstance).count() >= count;
    }

    public void replaceWith(Card oldCard, Card newCard) {
        players.values().forEach(p->{p.replaceWith(oldCard, newCard);});
        for (int i = 0; i < stack.size(); i++){
            if(stack.get(i).equals(oldCard)){
                stack.remove(i);
                stack.add(i, newCard);
            }
        }
    }

    public Arraylist<Card> extractAllCopiesFrom(String username, String cardName, String zone) {
        Arraylist<Card> cards = new Arraylist<>(getZone(username, zone).parallelStream()
                .filter(c -> { return c.name.equals(cardName);}).collect(Collectors.toList()));
        getZone(username, zone).removeAll(cards);
        return cards;
    }

    public void putTo(String username, Arraylist<Card> cards, String zone) {
        getZone(username, zone).addAll(cards);
    }
    
    public void transformToJunk(UUID cardID){
        Card c = getCard(cardID);
        Junk j = new Junk(c.controller);
        j.copyPropertiesFrom(c);
        replaceWith(c, j);
    }

    public Arraylist<Card> extractAll(List<UUID> list) {
        return new Arraylist<>(list.stream().map(i -> {return extractCard(i);}).collect(Collectors.toList()));
    }
    
    public void shuffleIntoDeck(String username, Arraylist<Card> cards) {
        players.get(username).deck.shuffleInto(cards);
    }

    
    
    
    private SelectFromType getZoneLabel(String zone){
        switch(zone){
            case "hand":
                return HAND;
            case "both play":
            case "play":
                return PLAY;
            case "scrapyard":
                return SCRAPYARD;
            case "void":
                return VOID;
            case "stack":
                return STACK;
            default:
                return NOTYPE;
        }
    }
    
    public int selectX(String username, int maxX) {
        if (maxX == 0){
            return maxX;
        }
        SelectXValue.Builder b = SelectXValue.newBuilder()
                .setMaxXValue(maxX)
                .setGame(toGameState(username));
        try {
            send(username, ServerGameMessage.newBuilder().setSelectXValue(b));
            
            int l = (int)response.take();
            return l;
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    private Arraylist<UUID> selectFrom(String username, SelectFromType type, Arraylist<Card> candidates, Arraylist<UUID> canSelect, int count, boolean upTo){
        /*
        if (canSelect.size() == count || (canSelect.size() < count && upTo)){
            return canSelect;
        }
        */
        SelectFrom.Builder b = SelectFrom.newBuilder()
                .addAllCanSelected(canSelect.parallelStream().map(u->{return u.toString();}).collect(Collectors.toList()))
                .addAllCandidates(candidates.parallelStream().map(c->{return c.toCardMessage().build();}).collect(Collectors.toList()))
                .setMessageType(type)
                .setSelectionCount(count)
                .setUpTo(upTo)
                .setGame(toGameState(username));
        try {
            send(username, ServerGameMessage.newBuilder().setSelectFrom(b));
            System.out.println("Waiting targets!");
            String[] l = (String[])response.take();
            System.out.println("Done waiting!");
            return UUIDHelper.toUUIDList(l);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Arraylist<UUID> selectFromZone(String username, String zone, Predicate<Card> validTarget, int count, boolean upTo) {        
        Arraylist<UUID> canSelect = new Arraylist<>(getZone(username, zone).parallelStream()
                .filter(validTarget).map(c->{return c.id;}).collect(Collectors.toList()));
        return selectFrom(username, getZoneLabel(zone), getZone(username, zone), canSelect, count, upTo);
    }

    public Arraylist<UUID> selectFromList(String username, Arraylist<Card> candidates, Predicate<Card> validTarget, int count, boolean upTo) {
        Arraylist<UUID> canSelect = new Arraylist<>(candidates.parallelStream()
                .filter(validTarget).map(c->{return c.id;}).collect(Collectors.toList()));
        return selectFrom(username, LIST, candidates, canSelect, count, upTo);
    }
    
    public String selectPlayer(String username) {
        SelectPlayer.Builder b = SelectPlayer.newBuilder()
                .setGame(toGameState(username));
        try {
            send(username, ServerGameMessage.newBuilder().setSelectPlayer(b));
            String selection = (String)response.take();
            return selection;
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    
    
    
    public void win(String player) {
        send(player, ServerGameMessage.newBuilder().setWin(Win.newBuilder().setGameID(id.toString())));
        send(getOpponentName(player), ServerGameMessage.newBuilder().setLoss(Loss.newBuilder().setGameID(id.toString())));
        connections.forEach((s, c) -> {c.close();});
        connections = new Hashmap<>();
        GeneralEndpoint.gameServer.removeGame(id);
    }
    
    
    public void lose(String player) {
        send(player, ServerGameMessage.newBuilder().setLoss(Loss.newBuilder().setGameID(id.toString())));
        send(getOpponentName(player), ServerGameMessage.newBuilder().setWin(Win.newBuilder().setGameID(id.toString())));
        connections.forEach((s, c) -> {c.close();});
        connections = new Hashmap<>();
        GeneralEndpoint.gameServer.removeGame(id);
    }
    
    public GameState.Builder toGameState(String username){
        GameState.Builder b = 
                GameState.newBuilder()
                .setId(id.toString())
                .setPlayer(players.get(username).toPlayerMessage())
                .setOpponent(players.get(getOpponentName(username)).toOpponentMessage())
                .setTurnPlayer(turnPlayer)
                .setActivePlayer(activePlayer)
                .setPhase(phase);
        for(int i = 0; i < stack.size(); i++){
            b.addStack(stack.get(i).toCardMessage());
        }
        players.forEach((s, p) -> {
            if(isActive(s)){
                p.hand.forEach(c -> {
                    if(c.canPlay(this)){
                        b.addCanPlay(c.id.toString());
                    }
                    if(c.canStudy(this)){
                        b.addCanStudy(c.id.toString());
                    }
                });
                p.playArea.forEach(c -> {
                    if(c instanceof Activatable && ((Activatable)c).canActivate(this)){
                        b.addCanActivate(c.id.toString());
                    }
                });
            }
        });
        return b;
    }

    public void send(String username, ServerGameMessage.Builder builder) {
        try {
            setLastMessage(username, builder.build());
            GameEndpoint e = connections.get(username);
            if (e != null) {
                e.send(builder);
            }
        } catch (IOException | EncodeException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void updatePlayers(){
        players.forEach((name, player) -> {
            send(name, ServerGameMessage.newBuilder()
                    .setUpdateGameState(UpdateGameState.newBuilder()
                            .setGame(toGameState(name))));
        });
    }

    public void addReflect(String username, int i) {
        players.get(username).reflect += i;
    }
    
    public void addShield(String username, int i) {
        players.get(username).shield += i;
    }

    public boolean hasEnergy(String username, int i) {
        return players.get(username).energy >= i;
    }

    public boolean hasKnowledge(String username, Hashmap<Types.Knowledge, Integer> knowledge) {
         return players.get(username).hasKnowledge(knowledge);
    }

    public boolean canPlaySlow(String username) {
         return turnPlayer.equals(username)
                && activePlayer.equals(username)
                && stack.isEmpty()
                && phase == MAIN;
    }

    public UUID getId() {
        return id;
    }

    public boolean canStudy(String username) {
        return canPlaySlow(username)
            && players.get(username).numOfStudiesLeft > 0;
    }

    //Eventually get rid of this
    public Player getPlayer(String username) {
        return players.get(username);
    }

    public int getEnergy(String controller) {
        return players.get(controller).energy;
    }

    public boolean isActive(String username) {
        return activePlayer.equals(username);
    }

    public boolean isAPlayer(String username) {
        return players.getOrDefault(username, null) != null;
    }

    public void addToResponseQueue(Object o) {
        try {
            response.put(o);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processBeginEvents() {
        eventQueue.add(Event.turnStart(turnPlayer));
        processEvents();
    }

    private void processEndEvents() {
        eventQueue.add(Event.turnEnd(turnPlayer));
        processEvents();
    }

    public void registerTriggeringCard(String username, Triggering t) {
        triggeringCards.get(username).add(t);
    }

    public void removeTriggeringCard(Triggering card) {
        triggeringCards.values().forEach(l->{l.remove(card);});
    }
}