/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccg.ancientaliens.set1.blue;

import com.ccg.ancientaliens.card.types.Action;
import com.ccg.ancientaliens.card.types.Card;
import com.ccg.ancientaliens.game.Game;
import com.ccg.ancientaliens.game.Player;
import com.ccg.ancientaliens.helpers.UUIDHelper;
import static com.ccg.ancientaliens.protocol.Types.Knowledge.BLUE;
import com.ccg.ancientaliens.helpers.Hashmap;
import com.ccg.ancientaliens.helpers.Arraylist;
import java.util.UUID;

/**
 *
 * @author pseudo
 */
public class UA03 extends Action {
    
    /**
     *
     * @param owner
     */
    public UA03(String owner) {
        super("UA03", 1, new Hashmap(BLUE, 1), 
        "Look at the top 4 cards of your deck. Put a kit among them into play. Put rest to the bottom.", owner);
    }
    
    @Override
    public void resolve (Game game){
        Player p = game.getPlayer(controller);
        Arraylist<Card> topCards = p.deck.extractFromTop(4);
        Arraylist<UUID> s = game.selectFromList(controller, topCards, c->{return c.subtypes.contains("Kit");}, 1, true);
        Arraylist<Card> selected = UUIDHelper.getInList(topCards, s);
        Arraylist<Card> notSelected = UUIDHelper.getNotInList(topCards, s);
        selected.forEach(c ->{c.resolve(game);});
        p.deck.putToBottom(notSelected);
        game.putTo(controller, this, "scrapyard");
    }
    
}