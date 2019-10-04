/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.sets.set1;

import com.visitor.card.types.helpers.Ability;
import com.visitor.card.types.Asset;
import com.visitor.game.Game;
import static com.visitor.game.Game.Zone.BOTH_PLAY;
import com.visitor.helpers.Arraylist;
import com.visitor.helpers.Hashmap;
import com.visitor.helpers.Predicates;
import static com.visitor.protocol.Types.Knowledge.RED;
import java.util.UUID;

/**
 *
 * @author pseudo
 */
public class HeatWave extends Asset {
    
    UUID target;
    
    public HeatWave (String owner){
        super("Heat Wave", 1, new Hashmap(RED, 2), 
            "Activate: \n" +
            "  Return ~ and target asset to controller's hand.", owner);
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return   
                game.hasIn(controller, BOTH_PLAY, c->{return (c instanceof Asset && !c.id.equals(id));}, 1);
    }

    @Override
    public void activate(Game game) {
        game.deplete(id);
        target = game.selectFromZone(controller, BOTH_PLAY, c->{return (c instanceof Asset && !c.id.equals(id));}, 1, false).get(0);
        game.addToStack(new Ability(this,
            "Return ~ and target asset to controller's hand.",
            (x) -> {
                if(game.isIn(controller, target, BOTH_PLAY)){
                    game.getCard(target).returnToHand(game);
                }
                if(game.isIn(controller, id, BOTH_PLAY)){
                    returnToHand(game);
                }
            }, new Arraylist<>(target))
        );
    }
}
