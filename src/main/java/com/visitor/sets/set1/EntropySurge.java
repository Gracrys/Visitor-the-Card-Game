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
import com.visitor.helpers.Predicates;
import static com.visitor.protocol.Types.Knowledge.BLACK;
import java.util.UUID;

/**
 *
 * @author pseudo
 */
public class EntropySurge extends Asset {
    
    UUID target;
    
    public EntropySurge (String owner){
        super("Entropy Surge", 4, new Hashmap(BLACK, 2), 
        "Sacrifice an Asset: Gain 1 Energy. If that asset is owned by the opponent gain 1 additional energy.", owner);
    }

    @Override
    public boolean canActivateAdditional(Game game) {
        return game.hasIn(controller, PLAY, Predicates::isAsset, 1);
    }

    @Override
    public void activate(Game game) {
        target = game.selectFromZone(controller, PLAY, Predicates::isAsset, 1, false).get(0);
        game.sacrifice(target);
        game.addToStack(new Ability(this, 
            controller + " gains " + (game.ownedByOpponent(target)?2:1) + " energy",
            (x) -> { game.addEnergy(controller, (game.ownedByOpponent(target)?2:1)); }));
    }
}
