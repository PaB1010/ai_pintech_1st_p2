package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Pokemon Api
 * Type Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Types {

    private int slot;

    private UrlItem type;
}
