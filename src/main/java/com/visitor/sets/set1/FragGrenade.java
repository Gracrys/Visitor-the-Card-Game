/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.sets.set1;

import com.visitor.card.types.helpers.Ability;
import com.visitor.card.types.Asset;
import com.visitor.game.Game;
import com.visitor.helpers.Hashmap;
import static com.visitor.protocol.Types.Knowledge.RED;
import java.util.UUID;

/**
 *
 * @author pseudo
 */
public class FragGrenade extends Asset {
    
    public FragGrenade (String owner){
        super("Frag Grenade", 2, new Hashmap(RED, 1), 
                "1, Activate: \n" +
                "  Deal 2 damage", owner);
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return   game.hasEnergy(controller, 1);
    }

    @Override
    public void activate(Game game) {
        game.deplete(id);
        game.spendEnergy(controller, 1);
        UUID target = game.selectDamageTargets(controller, 1, false).get(0);
        game.addToStack(new Ability(this,
            "Deal 2 damage",
            (x) -> { game.dealDamage(id, target, 2); }));
    }
}
