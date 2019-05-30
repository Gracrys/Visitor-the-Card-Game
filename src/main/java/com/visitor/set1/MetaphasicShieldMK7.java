/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.set1;

import com.visitor.card.types.Activation;
import com.visitor.card.types.Item;
import com.visitor.game.Game;
import static com.visitor.protocol.Types.Counter.CHARGE;
import static com.visitor.protocol.Types.Knowledge.YELLOW;
import com.visitor.helpers.Hashmap;


/**
 *
 * @author pseudo
 */
public class MetaphasicShieldMK7 extends Item {
    
    public MetaphasicShieldMK7 (String owner){
        super("Metaphasic Shield MK 7", 1, new Hashmap(YELLOW, 2), 
                "Charge 2.\n" +
                "3, Sacrifice ~: \n" +
                "  Gain reflect X.\n" +
                "  X = # of charge counters\"", owner);
        subtypes.add("Barrier");
    }
    
    @Override
    public void resolve(Game game) {
        super.resolve(game);
        addCounters(CHARGE, 2);
    }

    @Override
    public boolean canActivate(Game game) {
        return game.hasEnergy(controller, 3);
    }
    
    @Override
    public void activate(Game game) {
        game.spendEnergy(controller, 3);
        int x = counters.get(CHARGE);
        counters.removeFrom(CHARGE);
        game.destroy(id);
        
        game.addToStack(new Activation(this, controller+" gains reflect " + x,
            (y) -> { 
                game.addReflect(controller, x);
        }));
    }
}
