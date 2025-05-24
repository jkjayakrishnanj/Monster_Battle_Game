package edu.kit.kastel.game.model.effect;

/**
 * Represents a general effect.
 *
 * @author uupyx
 */
public sealed interface Effect permits RepeatEffect, HittableEffect, StatusEffect {

}
