/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.sets.set1;

import com.visitor.card.types.helpers.Ability;
import com.visitor.card.types.Asset;
import com.visitor.game.Game;
import static com.visitor.game.Game.Zone.PLAY;
import com.visitor.helpers.Hashmap;
import static com.visitor.protocol.Types.Knowledge.BLACK;
import java.util.UUID;

/**
 *
 * @author pseudo
 */
public class MindSlavesThrash extends Asset {
    
    public MindSlavesThrash (String owner){
        super("Mind Slave's Thrash", 1, new Hashmap(BLACK, 1), 
                "Condition - Control a card you don't own in play. Activate: Deal 2 damage", owner);
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return   game.controlsUnownedCard(controller, PLAY);
    }

    @Override
    public void activate(Game game) {
        UUID target = game.selectDamageTargets(controller, 1, false).get(0);
        game.deplete(id);
        game.addToStack(new Ability(this,
            "deal 2 damage.",
            (x) -> {
                game.dealDamage(id, target, 2);
            }));
    }
}
