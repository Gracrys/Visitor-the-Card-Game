package com.visitor.sets.base;

import com.visitor.card.types.Cantrip;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import static com.visitor.protocol.Types.Knowledge.PURPLE;

public class PC04 extends Cantrip {
	public PC04 (Game game, String owner) {
		super(game, "UR01", 3,
				new CounterMap<>(PURPLE, 2),
				"Destroy target unit.",
				owner);

		playable
				.setTargetSingleUnit((cardId) -> game.destroy(id, cardId)
				);
	}
}