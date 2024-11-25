package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Pokemon Api
 *
 * Name Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Names {

    private UrlItem language;

    private String name;
}