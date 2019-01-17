/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import card.Card;
import static card.Card.toUUIDList;
import client.gui.DeckBuilder;
import client.gui.GameArea;
import client.gui.Lobby;
import client.gui.Login;
import client.gui.MainFrame;
import client.gui.components.CardOrderPopup;
import client.gui.components.TextPopup;
import enums.Knowledge;
import game.ClientGame;
import game.Deck;
import game.Table;
import helpers.Hashmap;
import java.io.File;
import java.io.Serializable;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.UUID;
import network.Connection;
import network.Message;
import static network.Message.chatMessage;
import static network.Message.pass;
import static network.Message.registerGameConnection;
import static network.Message.registerInteractionConnection;
import static network.Message.updateLobby;

/**
 *
 * @author pseudo
 */
public class Client {
    String hostname;
    int hostport;
    int hostGamePort;

    /**
     *
     */
    public Connection connection;

    /**
     *
     */
    public Connection gameConnection;

    /**
     *
     */
    public Connection interactionConnection;

    /**
     *
     */
    public String username;

    /**
     *
     */
    public ArrayList<String> players;

    /**
     *
     */
    public ArrayList<String> chatLog;

    /**
     *
     */
    public Hashmap<UUID, Table> tables;

    /**
     *
     */
    public ClientGame game;
    
    //UI
    public MainFrame main;
    Login login;
    Lobby lobby;
    DeckBuilder deckBuilder;

    /**
     *
     */
    public GameArea gameArea;
    
    /**
     *
     */
    public Client(){
        //Initialize UI
        main = new MainFrame(this);
        login = new Login(this);
        lobby = new Lobby(this);
        deckBuilder = new DeckBuilder(this);

        main.add("Login", login);
        login.setVisible(true);

        //hostname = "ccgtest.ddns.net";
        hostname = "localhost";
        hostport = 8_080;
        hostGamePort = 8_081;
        out.println("Hostname: " + hostname);
        connection = new Connection();
        connection.openConnection(hostname, hostport);
    }
    
    //Game Related Functions

    /**
     *
     * @param game
     */
    public void newGame(ClientGame game){
        this.game = game;
        gameConnection = new Connection();
        gameConnection.openConnection(hostname, hostGamePort);
        new Thread(new ClientGameReceiver(gameConnection, this)).start();
        gameConnection.send(registerGameConnection(game.id, username));
        
        interactionConnection = new Connection();
        interactionConnection.openConnection(hostname, hostGamePort);
        new Thread(new ClientGameReceiver(interactionConnection, this)).start();
        interactionConnection.send(registerInteractionConnection(game.id, username));
        
        gameArea = new GameArea(this);
        main.add("Game", gameArea);
        gameArea.setVisible(true);
        gameArea.revalidate();
        gameArea.repaint();
        main.setSelectedComponent(gameArea);
        main.setSelectedComponent(deckBuilder);
        main.setSelectedComponent(gameArea);
    }
    
    /**
     *
     * @param game
     */
    public void updateGame(ClientGame game){
        this.game = game;
        gameArea.update();
    }
    
    /**
     *
     */
    public void mulligan(){
        gameConnection.send(Message.mulligan(game.id, username));
    }
    
    /**
     *
     */
    public void keep(){
        gameConnection.send(Message.keep(game.id, username));
    }
    
    /**
     *
     * @param card
     * @param knowledge
     */
    public void study(Card card, Hashmap<Knowledge, Integer> knowledge){
        gameConnection.send(Message.study(game.id, username, card.id, knowledge));
    }
    
    /**
     *
     */
    public void skipInitiative(){
        gameConnection.send(pass(game.id));
    }
    
    /**
     *
     * @param count
     */
    public void handSelection(int count){
        if (!game.player.hand.isEmpty()) {
            gameArea.discardCards(count);
        } else {
            interactionConnection.send(Message.selectFromHandReturn(new ArrayList<>()));
        }
    }
    
    /**
     *
     * @param cards
     */
    public void handSelectionReturn(ArrayList<Serializable> cards){
        interactionConnection.send(Message.selectFromHandReturn(cards));
    }
    
    /**
     *
     */
    public void concede(){
        gameConnection.send(Message.concede(game.id, username));
    }
    
    /**
     *
     */
    public void lose(){
        new TextPopup("You lost");
        finishGame();
    }
    
    /**
     *
     */
    public void win(){
        new TextPopup("You win");
        finishGame();
    }
    
    /**
     *
     */
    public void finishGame(){
        gameConnection.closeConnection();
        interactionConnection.closeConnection();
        main.setSelectedComponent(lobby);
        main.remove(gameArea);
        game = null;
        gameArea = null;
    }
    
    //Chat Related Functions

    /**
     *
     * @param message
     */
    public void sendMessage(String message){
        connection.send(chatMessage(username+": "+message));
    }
    
    /**
     *
     */
    public void updateChat(){
        lobby.updateChat();
    }
    
    /**
     *
     */
    public void updatePlayers(){
        lobby.updatePlayers();
    }
    
    //Table Related Functions

    /**
     *
     * @param deckFile
     */
    public void createTable(File deckFile){
        Deck deck = new Deck(deckFile, username);
        if(deck.valid()){
            connection.send(Message.createTable(username, deck));
        } else {
            new TextPopup("Invalid deck.");
        }
    }
    
    /**
     *
     * @param deckFile
     * @param uuid
     */
    public void joinTable(File deckFile, UUID uuid){
        Deck deck = new Deck(deckFile, username);
        if(deck.valid()){
            connection.send(Message.joinTable(username, deck, uuid));
        } else {
            new TextPopup("Invalid deck.");
        }
    }
    
    /**
     *
     */
    public void updateTables(){
        lobby.updateTables();
    }
    
    
    // Login related functions

    /**
     *
     * @param username
     * @return
     */
    public String register(String username){
        connection.send(Message.register(username));
        Message message = connection.receive();
        switch (message.label) {
            case SUCCESS:
                return username + " successfuly registered.";
            case FAIL:
                return "Registration failed. Error: " + message.object;
            default:
                return "Unexpected server response.";
        }
    }
    
    /**
     *
     */
    public void signalLogin(){
        main.remove(login);
        login.setVisible(false);
        main.add("Lobby", lobby);
        main.add("Deck Editor", deckBuilder);
        lobby.setVisible(true);
        deckBuilder.setVisible(true);
        main.setSelectedComponent(lobby);
        connection.send(updateLobby());
    }
    
    /**
     *
     * @param username
     */
    public void login(String username){
        connection.send(Message.login(username));
        Message message = connection.receive();
        switch (message.label) {
            case SUCCESS:
                this.username = username;
                new Thread(new ClientReceiver(connection, this)).start();
                signalLogin();
                break;
            default:
                new TextPopup((String)message.object);
        }
        
    }

    /**
     *
     */
    public void logout(){
        connection.send(Message.logout(username));      
    }

    /**
     *
     * @param cards
     */
    public void order(ArrayList<Card> cards) {
        new CardOrderPopup(cards, this::orderReturn);
    }
    
    /**
     *
     * @param cards
     */
    public void orderReturn(ArrayList<Card> cards){
        interactionConnection.send(Message.orderReturn(toUUIDList(cards)));
    }
    
}

