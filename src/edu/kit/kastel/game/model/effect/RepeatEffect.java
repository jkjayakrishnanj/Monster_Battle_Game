package edu.kit.kastel.game.model.effect;

import java.util.List;

import edu.kit.kastel.game.model.count.Count;

/**
 * Represents an effect that is repeated multiple times.
 *
 * @author uupyx
 * @param count The number of times the effect is repeated.
 * @param effects The list of effects to be repeated.
 */
public record RepeatEffect(Count count, List<Effect> effects) implements Effect {

}
