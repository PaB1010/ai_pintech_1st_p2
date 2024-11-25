package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Pokemon API 에서
 * Sprites (Image)
 * Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sprites {

    private SpritesOther other;
}