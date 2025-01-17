package com.visitor.sets.base;

import com.visitor.card.types.Cantrip;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import java.util.UUID;

import static com.visitor.protocol.Types.Knowledge.GREEN;

public class GC05 extends Cantrip {
	public GC05 (Game game, UUID owner) {
		super(game, "GC05", 2,
				new CounterMap<>(GREEN, 2),
				"Target unit gets +3/+3.",
				owner);

		playable
				.setTargetSingleUnit(null, null, cardId -> {
					game.getCard(cardId).addAttack(3);
					game.getCard(cardId).addHealth(3);
				}, null);
	}
}
