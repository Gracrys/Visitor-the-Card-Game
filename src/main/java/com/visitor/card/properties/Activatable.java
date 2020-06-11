/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visitor.card.properties;

import com.visitor.game.Card;
import com.visitor.game.Game;
import com.visitor.helpers.Arraylist;
import com.visitor.helpers.containers.ActivatedAbility;

/**
 * Interface for cards that has an activating ability.
 *
 * @author pseudo
 */
public class Activatable {

	private final Card card;
	private final Game game;

	private Arraylist<ActivatedAbility> abilityList;

	// Constructors
	public Activatable (Game game, Card card) {
		this.game = game;
		this.card = card;
		abilityList = new Arraylist<>();
	}

	public Activatable (Game game, Card card, ActivatedAbility ability) {
		this(game, card);
		addActivatedAbility(ability);
	}

	public final boolean canActivate () {
		for (ActivatedAbility activatedAbility : abilityList) {
			if (activatedAbility.canActivate.get())
				return true;
		}
		return false;
	}

	public final Arraylist getActivatableAbilities () {
		Arraylist<ActivatedAbility> abilities = new Arraylist<>();
		for (ActivatedAbility activatedAbility : abilityList) {
			if (activatedAbility.canActivate.get())
				abilities.add(activatedAbility);
		}
		return abilities;
	}


	public final void activate () {
		Arraylist<ActivatedAbility> abilities = getActivatableAbilities();
		if (abilities.size() == 1) {
			abilities.get(0).activate.run();
		} else if (abilities.size() > 1) {
			// TODO: figure this out
		}
	}

	// Adders
	public Activatable addActivatedAbility (ActivatedAbility ability) {
		abilityList.add(ability);
		return this;
	}
	// Resetters

	public final void resetAbilityList () {
		abilityList.clear();
	}
}
