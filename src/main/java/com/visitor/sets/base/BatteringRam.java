/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visitor.sets.base;

import com.visitor.card.types.Unit;
import com.visitor.card.types.helpers.AbilityCard;
import com.visitor.game.Card;
import com.visitor.game.Game;
import com.visitor.helpers.CounterMap;

import java.util.UUID;

import static com.visitor.card.properties.Combat.CombatAbility.Trample;
import static com.visitor.helpers.Predicates.isUnit;
import static com.visitor.protocol.Types.Knowledge.RED;

/**
 * @author pseudo
 */
public class BatteringRam extends Unit {

	public BatteringRam (Game game, UUID owner) {
		super(game, "Battering Ram",
				4, new CounterMap(RED, 2),
				"When {~} enters play, discard a random card. If you discard a unit card this way, {~} deals damage equal to that card’s attack to any target.",
				4, 3,
				owner, Trample);

				addEnterPlayEffect(null, "When {~} enters play, discard a random card. If you discard a unit card this way, {~} deals damage equal to that card’s attack to any target.",
				()-> {
					Card discardedCard = game.discardAtRandom(controller);
					if (discardedCard != null && isUnit(discardedCard)){
						int attack = discardedCard.getAttack();
						UUID damageTarget = game.selectDamageTargets(controller,1, false, "").get(0);
						game.addToStack(new AbilityCard(game, this, "Deal " + attack + " damage.",
								()-> game.dealDamage(id, damageTarget, attack), damageTarget));
					}
				});
	}
}
