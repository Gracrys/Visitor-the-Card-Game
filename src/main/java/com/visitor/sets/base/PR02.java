package com.visitor.sets.base;

import com.visitor.card.types.Ritual;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import static com.visitor.game.Game.Zone.Discard_Pile;
import static com.visitor.protocol.Types.Knowledge.PURPLE;

public class PR02 extends Ritual {

	public PR02 (Game game, String owner) {
		super(game, "UR01", 1,
				new CounterMap<>(PURPLE, 1),
				"Restore and return target unit card from your discard pile to your hand.",
				owner);

		playable
				.setTargetSingleUnit(Discard_Pile, cardId -> game.restore(cardId).returnToHand());
	}
}
