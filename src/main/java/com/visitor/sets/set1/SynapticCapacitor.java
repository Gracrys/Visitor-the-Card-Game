/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.visitor.sets.set1;

import com.visitor.card.types.helpers.Ability;
import com.visitor.card.types.Asset;
import com.visitor.game.Game;
import static com.visitor.game.Game.Zone.HAND;
import static com.visitor.game.Game.Zone.SCRAPYARD;
import com.visitor.helpers.Hashmap;
import com.visitor.helpers.Predicates;
import static com.visitor.protocol.Types.Knowledge.BLUE;
import java.util.UUID;


/**
 *
 * @author pseudo
 */
public class SynapticCapacitor extends Asset{
    
    public SynapticCapacitor (String owner){
        super("Synaptic Capacitor", 2, new Hashmap(BLUE, 2), 
                "Discard 1, Purge 1: Gain 1 Energy", owner);
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return game.hasIn(controller, HAND, Predicates::any, 1)
                && !depleted;
    }
    
    @Override
    public void activate(Game game) {
        game.discard(controller, 1);
        UUID target = game.selectFromZone(game.getPlayer(controller).username, SCRAPYARD, Predicates::any, 1, false).get(0);
        if(game.hasIn(controller, SCRAPYARD, Predicates::any, 1)) {
            game.purge(controller, target);
        }
        game.addToStack(new Ability(this, controller + " gains 1 energy",
                (x) -> { game.addEnergy(controller, 1); }));
    }
}
