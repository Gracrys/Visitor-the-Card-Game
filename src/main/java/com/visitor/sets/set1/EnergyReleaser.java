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
import com.visitor.helpers.Arraylist;
import com.visitor.helpers.Hashmap;
import com.visitor.helpers.Predicates;
import static com.visitor.protocol.Types.Knowledge.RED;
import java.util.UUID;


/**
 *
 * @author pseudo
 */
public class EnergyReleaser extends Asset {
    
    public EnergyReleaser(String owner){
        super("Energy Releaser", 1, new Hashmap(RED, 2),
                "Sacrifice an Asset, Activate: \n" +
                "  Deal X damage. \n" +
                "  X = cost of sacrificed asset.", owner);
    }
    
    @Override
    public boolean canActivateAdditional(Game game) {
        return   game.hasIn(controller, PLAY, Predicates::isAsset, 1);
    }
    
    @Override
    public void activate(Game game) {
        UUID selection = game.selectFromZone(controller, PLAY, Predicates::isAsset, 1, false).get(0);
        int x = game.getCard(selection).cost;
        game.sacrifice(selection);
        game.deplete(id);
        UUID target = game.selectDamageTargets(controller, 1, false).get(0);
        game.addToStack(new Ability(this,
            "Deal "+x+" damage",
            (y) -> {
                game.dealDamage(id, target, x);
            }, new Arraylist<>(selection))
        );
    }
}
