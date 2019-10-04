/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.sets.set1;

import com.visitor.card.types.helpers.Ability;
import com.visitor.card.types.Ally;
import com.visitor.card.types.Asset;
import com.visitor.game.Game;
import static com.visitor.game.Game.Zone.BOTH_PLAY;
import com.visitor.helpers.Arraylist;
import com.visitor.helpers.Hashmap;
import com.visitor.helpers.Predicates;
import static com.visitor.protocol.Types.Knowledge.GREEN;
import java.util.UUID;


/**
 *
 * @author pseudo
 */
public class MysticFruit extends Asset {
    
    UUID target;
    
    public MysticFruit (String owner){
        super("Mystic Fruit", 1, new Hashmap(GREEN, 1), 
                "When ~ enters play: \n" +
                "    Add 1 Loyalty up to one target ally. \n" +
                "Activate, Sacrifice ~: \n" +
                "    Target Ally gains 2 health.", 
                1,
                owner);
    }
    
    @Override
    protected void afterResolve(Game game) {
        Arraylist<UUID> maybeTarget = game.selectFromZone(controller, BOTH_PLAY, Predicates::isAlly, 1, true);
        if(!maybeTarget.isEmpty()){
            target = maybeTarget.get(0);
            game.addToStack(new Ability(this, "Add 1 Loyalty", 
            a -> {
                if(game.isIn(controller, target, BOTH_PLAY)){
                    ((Ally)game.getCard(target)).loyalty++;
                }
            }
            ,target));
        }
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return   
                game.hasIn(controller, BOTH_PLAY, Predicates::isAlly, 1);
    }
    
    @Override
    public void activate(Game game) {
        target = game.selectFromZone(controller, BOTH_PLAY, Predicates::isAlly, 1, false).get(0);
        game.deplete(id);
        game.sacrifice(id);
        game.addToStack(new Ability(this, 
                "Gains 2 health.",
            a -> {
                if(game.isIn(controller, target, BOTH_PLAY)){
                    ((Ally)game.getCard(target)).health+=2;
                }
            }
            ,target));
    }
}
