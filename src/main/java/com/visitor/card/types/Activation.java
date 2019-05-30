/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.visitor.card.types;

import com.visitor.game.Game;
import com.visitor.helpers.Arraylist;
import com.visitor.helpers.Hashmap;
import com.visitor.protocol.Types;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *
 * @author pseudo
 */
public class Activation extends Card {
    
    Consumer<Card> effect;
    Card creator;
    
    public Activation (Card creator, String text, Consumer<Card> effect){
        super("Activation", 0, new Hashmap<>(), text, creator.controller);
        this.effect = effect;
        this.creator = creator;
        this.targets = new Arraylist<UUID>(creator.id);
    }
    
    public Activation (Card creator, String text, Consumer<Card> effect, Arraylist<UUID> targets){
        super("Activation", 0, new Hashmap<>(), text, creator.controller);
        this.effect = effect;
        this.creator = creator;
        this.targets = new Arraylist<UUID>(creator.id).putAllIn(targets);
    }
    
    @Override
    public boolean canPlay(Game game) { return false; }

    @Override
    public void resolve(Game game) { effect.accept(this); }
    
    @Override
    public Types.Card.Builder toCardMessage() {
        return super.toCardMessage()
                .setType("Activation")
                .setCost("");
    }
    
}
