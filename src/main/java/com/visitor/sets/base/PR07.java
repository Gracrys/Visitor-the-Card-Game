package com.visitor.sets.base;

import com.visitor.card.types.Ritual;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;
import com.visitor.helpers.Predicates;

import java.util.UUID;

import static com.visitor.game.Game.Zone.Play;
import static com.visitor.protocol.Types.Knowledge.PURPLE;

public class PR07 extends Ritual {

	public PR07 (Game game, UUID owner) {
		super(game, "PR07", 4,
				new CounterMap<>(PURPLE, 2),
				"As an additional cost to cast this spell, sacrifice a unit.\n" +
						"Draw 3 cards.",
				owner);

		playable
				.addCanPlayAdditional(() ->
						game.hasIn(controller, Play, Predicates::isUnit, 1)
				)
				.addBeforePlay(() -> {
					//Sacrificed Unit
					UUID sacrificedUnit = game.selectFromZone(controller, Play, Predicates::isUnit, 1, false, "").get(0);
					game.sacrifice(sacrificedUnit);
				})
				.setResolveEffect(() ->
						game.draw(controller, 3)
				);
	}
}
