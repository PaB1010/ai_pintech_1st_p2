package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Pokemon API 정보 Data Class
 *
 */
@Data
// @JsonIgnoreProperties = (true)일부 항목이 알 수 없는 경우여도 무시하고 실행
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiPokemon {

    private int id;

    private String name;

    private Sprites sprites;

    private int weight;

    private int height;

    private List<Types> types;

    private List<Ability> abilities;

    @JsonAlias("base_experience")
    private int baseExperience;

    // https://pokeapi.co/api/v2/pokemon-species/1
    private List<Names> names;

    @JsonAlias("flavor_text_entries")
    private List<FlavorText> flavorTextEntries;

    private List<Genus> genera;
}