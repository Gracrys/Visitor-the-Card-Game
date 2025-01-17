package com.visitor.sets.base;

import com.visitor.card.types.Ritual;
import com.visitor.game.Card;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;
import com.visitor.helpers.Predicates;
import com.visitor.helpers.containers.Damage;

import java.util.UUID;

import static com.visitor.game.Game.Zone.Opponent_Play;
import static com.visitor.game.Game.Zone.Play;
import static com.visitor.protocol.Types.Knowledge.GREEN;

public class GR01 extends Ritual {

	public GR01 (Game game, UUID owner) {
		super(game, "GR01", 2,
				new CounterMap<>(GREEN, 1),
				"Target unit you control strikes target unit you don't control.",
				owner);

		playable.addCanPlayAdditional(() ->
				game.hasIn(controller, Play, Predicates::isUnit, 1) &&
				game.hasIn(controller, Opponent_Play, Predicates::isUnit, 1)
		);
		playable.addBeforePlay(() -> {
			targets.add(game.selectFromZone(playable.card.controller, Play, Predicates::isUnit, 1, false, "Select a unit you control.").get(0));
			targets.add(game.selectFromZone(playable.card.controller, Opponent_Play, Predicates::isUnit, 1, false, "Select a unit you don't control").get(0));
		});
		playable.setResolveEffect(() -> {
			UUID strikerId = targets.get(0);
			UUID receiverId = targets.get(1);
			if (game.isIn(controller, Play, strikerId) &&
			    game.isIn(controller, Opponent_Play, receiverId)) {
				Card striker = game.getCard(strikerId);
				game.dealDamage(strikerId, receiverId, new Damage(striker.getAttack()));
			}
		});
	}
}
