package org.koreait.pokemon.api.services;

import lombok.RequiredArgsConstructor;
import org.koreait.pokemon.api.entities.ApiPokemon;
import org.koreait.pokemon.api.entities.ApiResponse;
import org.koreait.pokemon.api.entities.FlavorText;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.api.entities.UrlItem;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *
 */
@Service
@RequiredArgsConstructor
public class ApiUpdateService {

    // global.configs.BeansConfig 에서 싱글톤 객체 의존 주입
    private final RestTemplate tpl;

    private final PokemonRepository repository;

    /**
     * 1page 당 100개씩 DB update 반영
     *
     * @param page
     */
    public void update(int page, String version) {

        /* 전체 목록 처리 S */

        int limit = 100;
        
        // 시작 레코드 번호, 0, 100, 200, ...
        int offset = (page - 1) * limit;

        String url = String.format("https://pokeapi.co/api/v2/pokemon?offset=%d&limit=%d",offset,limit);

        ApiResponse response = tpl.getForObject(URI.create(url), ApiResponse.class);

        List<UrlItem> items = response.getResults();

        // 조회된 결과가 없는 경우 처리 X
        if (items == null || items.isEmpty()) {
            return;
        }
        /* 전체 목록 처리 E */

        /* 상세 정보 처리 S */
        List<Pokemon> pokemons = new ArrayList<>();

        for (UrlItem item : items) {

            Pokemon pokemon = new Pokemon();

            // 기초 data
            ApiPokemon data1 = tpl.getForObject(URI.create(item.getUrl()), ApiPokemon.class);
            pokemon.setSeq(Long.valueOf(data1.getId()));
            // 영문 이름
            pokemon.setNameEn(data1.getName());
            pokemon.setHeight(data1.getHeight());
            pokemon.setWeight(data1.getWeight());
            pokemon.setBaseExperience(data1.getBaseExperience());
            pokemon.setFrontImage(data1.getSprites().getOther().getOfficialArtwork().get("front_default"));

            /* Type 처리 S */
            String types = data1.getTypes().stream().map(d -> d.getType().getName())
                    .collect(Collectors.joining("||"));
            // Type1 || Type2 || Type3
            /* Type 처리 E */

            /* 특성 처리 S */
            String abilities = data1.getAbilities().stream().map(d -> d.getAbility().getName())
                    .collect(Collectors.joining("||"));
            // 특성1 || 특성2 || 특성3
            /* 특성 처리 E */

            pokemon.setTypes(types);
            pokemon.setAbilities(abilities);

            // 포켓몬 한글 이름, 한글 설명
            String url2 = String.format("https://pokeapi.co/api/v2/pokemon-species/%d", data1.getId());

            ApiPokemon data2 = tpl.getForObject(URI.create(url2), ApiPokemon.class);

            // 한글 이름
            String nameKr = data2.getNames().stream().filter(d -> d.getLanguage().getName().equals("ko")).map(d -> d.getName()).collect(Collectors.joining());

            // collect.joining
            String flavorTextOrigin = data2.getFlavorTextEntries().stream().filter(d -> d.getVersion().getName().equals("x")).map(d -> d.getFlavorText()).collect(Collectors.joining());

            // 버전에 맞는 포켓몬 한글 설명
            String flavorText = data2.getFlavorTextEntries().stream().filter(d -> d.getLanguage().getName().equals("ko")).filter(d -> d.getVersion().getName().equals(version)).map((FlavorText::getFlavorText)).collect(Collectors.joining());

            pokemon.setFlavorText(flavorText);

            pokemon.setName(nameKr);

            // 포켓몬 분류
            String genus = data2.getGenera().stream().filter(d -> d.getLanguage().getName().equals("ko")).map(d -> d.getGenus()).collect(Collectors.joining());

            pokemon.setGenus(genus);

            pokemons.add(pokemon);
        }
        /* 상세 정보 처리 E */

        // DB 영구 저장 처리
        repository.saveAllAndFlush(pokemons);
    }
}