package com.visitor.sets.base;

import com.visitor.card.types.Ritual;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import java.util.UUID;

import static com.visitor.card.properties.Combat.CombatAbility.Trample;
import static com.visitor.game.Game.Zone.Play;
import static com.visitor.helpers.Predicates.isUnit;
import static com.visitor.protocol.Types.Knowledge.GREEN;

public class GR03 extends Ritual {

	public GR03 (Game game, UUID owner) {
		super(game, "GR03", 5,
				new CounterMap<>(GREEN, 3),
				"Units you control gains +3/+3 and trample until end of turn.",
				owner);

		playable
				.setResolveEffect(() ->
						game.forEachInZone(controller, Play, card -> {
							if (isUnit(card)) {
								card.addTurnlyAttack(3);
								card.addTurnlyHealth(3);
								card.addTurnlyCombatAbility(Trample);
							}
						})
				);
	}
}
