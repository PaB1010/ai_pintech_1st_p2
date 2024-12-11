package org.koreait.pokemon.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.pokemon.controllers.PokemonSearch;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.QPokemon;
import org.koreait.pokemon.exceptions.PokemonNotFoundException;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

/**
 * Pokemon 조회 Service
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class PokemonInfoService {

    private final PokemonRepository pokemonRepository;

    private final HttpServletRequest request;

    private final Utils utils;

    /**
     * Pokemon 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<Pokemon> getList(PokemonSearch search) {

        // 페이지 번호
        // 최소 1으로 default 값 설정
        int page = Math.max(search.getPage(), 1);

        // 한 페이지당 출력 개수
        int limit = search.getLimit();

        // 1보다 작을 경우 20으로 default 값 설정
        limit = limit < 1 ? 20 : limit;

        QPokemon pokemon = QPokemon.pokemon;

        /* 검색 처리 S */

        // String sopt = search.getSopt();

        BooleanBuilder andBuilder = new BooleanBuilder();

        // 검색 조건
        String skey = search.getSkey();

        // 추후 특성, 타입 등 추가 예정
        if (StringUtils.hasText(skey)) { // 키워드 검색

            andBuilder.and(pokemon.name
                    .concat(pokemon.nameEn)
                    .concat(pokemon.flavorText).contains(skey));
        }

        /* 검색 처리 E */

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("seq")));

        Page<Pokemon> data = pokemonRepository.findAll(andBuilder, pageable);

        // 조회된 목록 (limit 개수 만큼)
        List<Pokemon> items = data.getContent();

        // 추가 정보 처리 (2차 가공)
        items.forEach(this::addInfo);


        // public Pagination(int page, int total, int ranges, int limit, HttpServletRequest request)

        int ranges = utils.isMobile() ? 5 : 10;

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * Pokemon 단일 조회
     *
     * @param seq
     * @return
     */
    public Pokemon get(Long seq) {

        Pokemon item = pokemonRepository.findById(seq).orElseThrow(PokemonNotFoundException::new);

        // 추가 정보 처리 (2차 가공)
        addInfo(item);

        return item;
    }

    /**
     * 추가 정보 처리 (2차 가공)
     *
     * @param item
     */
    private void addInfo(Pokemon item) {

        // abilities 분리 가공
        String abilities = item.getAbilities();

        if (StringUtils.hasText(abilities)) {

            item.set_abilities(Arrays.stream(abilities.split("\\|\\|")).toList());
        }

        // types 분리 가공
        String types = item.getTypes();

        if (StringUtils.hasText(types)) {

            item.set_types(Arrays.stream(types.split("\\|\\|")).toList());
        }
    }
}