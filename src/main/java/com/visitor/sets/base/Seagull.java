/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visitor.sets.base;

import com.visitor.card.types.Unit;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;
import com.visitor.helpers.Predicates;
import com.visitor.helpers.containers.ActivatedAbility;

import java.util.UUID;

import static com.visitor.card.properties.Combat.CombatAbility.Flying;
import static com.visitor.helpers.Predicates.and;
import static com.visitor.protocol.Types.Knowledge.BLUE;

/**
 * @author pseudo
 */
public class Seagull extends Unit {

	public Seagull (Game game, UUID owner) {
		super(game, "Seagull",
				2, new CounterMap(BLUE, 1),
				"{2}, {Use}: Another target unit gains flying until end of turn.",
				2, 1,
				owner, Flying);
		activatable
				.addActivatedAbility(new ActivatedAbility(game, this, 2, "Another target unit gains flying until end of turn.")
						//TODO: another unit doesn't work because lambda binds this to activated ability card not the original one
						.setTargeting(Game.Zone.Both_Play, Predicates.anotherUnit(id), 1, false,
						targetId -> game.runIfInZone(controller, Game.Zone.Both_Play, targetId, ()->game.addTurnlyCombatAbility(targetId, Flying)))
						.setDepleting());

	}
}
