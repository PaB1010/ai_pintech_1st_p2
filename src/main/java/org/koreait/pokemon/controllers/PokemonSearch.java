package org.koreait.pokemon.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;

/**
 * Pokemon 검색
 *
 */
@Data
public class PokemonSearch extends CommonSearch {

    private String type;
}