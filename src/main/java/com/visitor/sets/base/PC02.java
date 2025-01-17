package com.visitor.sets.base;

import com.visitor.card.properties.Combat;
import com.visitor.card.types.Cantrip;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import java.util.UUID;

import static com.visitor.protocol.Types.Knowledge.PURPLE;

public class PC02 extends Cantrip {
	public PC02 (Game game, UUID owner) {
		super(game, "PC02", 2,
				new CounterMap<>(PURPLE, 1),
				"Target unit gains deathtouch until end of turn. Draw 1 card.",
				owner);

		playable
				.setTargetSingleUnit(null, null, cardId -> game.getCard(cardId).addTurnlyCombatAbility(Combat.CombatAbility.Deathtouch),
						() -> game.draw(controller, 1));
	}
}
