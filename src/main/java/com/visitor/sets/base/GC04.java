package com.visitor.sets.base;

import com.visitor.card.types.Cantrip;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import java.util.UUID;

import static com.visitor.protocol.Types.Knowledge.GREEN;

public class GC04 extends Cantrip {
	public GC04 (Game game, UUID owner) {
		super(game, "GC04", 2,
				new CounterMap<>(GREEN, 1),
				"Target unit gets +2/+4 until end of turn.",
				owner);

		playable
				.setTargetSingleUnit(null, null, cardId -> {
					game.getCard(cardId).addTurnlyAttack(2);
					game.getCard(cardId).addTurnlyHealth(4);
				}, null);
	}
}
