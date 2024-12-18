package org.koreait.pokemon.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Order.asc;

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

    private final JPAQueryFactory queryFactory;

    private final WishService wishService;

    /**
     * Pokemon 목록 조회
     *
     * @param search
     * @return new ListData<>(items, pagination)
     */
    public ListData<Pokemon> getList(PokemonSearch search) {

        // 페이지 번호
        // 최소 1으로 default 값 설정
        int page = Math.max(search.getPage(), 1);

        // 한 페이지당 출력 개수
        int limit = search.getLimit();

        // 1보다 작을 경우 18으로 default 값 설정
        limit = limit < 1 ? 18 : limit;

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

        List<Long> seq = search.getSeq();

        if (seq != null && !seq.isEmpty()) {
            
            // 찜한 포켓몬 목록만 조회
            andBuilder.and(pokemon.seq.in(seq));
        }

        /* 검색 처리 E */

        // ★ Pageable 이면 무조건 반환 값은 Page ★
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(asc("seq")));

        Page<Pokemon> data = pokemonRepository.findAll(andBuilder, pageable);

        // 조회된 목록 (limit 개수 만큼)
        List<Pokemon> items = data.getContent();

        // 추가 정보 처리 (2차 가공)
        items.forEach(this::addInfo);


        // public Pagination(int page, int total, int ranges, int limit, HttpServletRequest request)

        int ranges = utils.isMobile() ? 5 : 10;

        // ★ Page 완성 ★
        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 내가 찜한 포켓몬 목록
     *
     * @param search
     * @return
     */
    public ListData<Pokemon> getMyPokemons(PokemonSearch search) {

        List<Long> seq = wishService.getMyWish(WishType.POKEMON);

        if (seq == null || seq.isEmpty()) {

            // NPE 방지용으로 빈 ListData 반환
            return new ListData<>();
        }

        search.setSeq(seq);

        return getList(search);
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
        addInfo(item, true);

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

    /**
     * 상세페이지 조회(get(), view.html)일 경우
     * 이전/다음 번호 포켓몬 추가 가공 여부
     *
     * @param item
     * @param isView
     */
    private void addInfo(Pokemon item, boolean isView) {

        addInfo(item);

        if (!isView) return;

        long seq = item.getSeq();

        long lastSeq = getLastSeq();

        // 이전 포켓몬 정보 - prevItem
        long prevSeq = seq - 1L;

        // 이전 포켓몬 번호가 음수일 경우 마지막 번호(SEQ)로 대체
        prevSeq = prevSeq < 1L ? lastSeq : prevSeq;

        // 다음 포켓몬 정보 - nextItem
        long nextSeq = seq + 1L;

        nextSeq = nextSeq > lastSeq ? 1L : nextSeq;

        QPokemon pokemon = QPokemon.pokemon;

        // 처리
        List<Pokemon> items = (List<Pokemon>)pokemonRepository.findAll(pokemon.seq.in(prevSeq, nextSeq));

        Map<String, Object> prevItem = new HashMap<>();
        Map<String, Object> nextItem = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {

            Pokemon _item = items.get(i);

            Map<String, Object> data = _item.getSeq().longValue() == prevSeq ? prevItem : nextItem;

            data.put("seq", _item.getSeq());
            data.put("name", _item.getName());
            data.put("nameEn", _item.getNameEn());
        }

        item.setPrevItem(prevItem);
        item.setNextItem(nextItem);
    }

    /**
     * 1번 포켓몬에서 prevItem 할 경우
     * 마지막 번호(seq) 포켓몬 조회
     *
     * @return
     */
    private Long getLastSeq() {

        QPokemon pokemon = QPokemon.pokemon;

        // 항목별 나열 가능, seq 최대값
        // Tuple 자료형 - Python의 Tuple 아님
        // 영속성 상태로는 아니고 Data 값만 get 해옴
        return queryFactory.select(pokemon.seq.max())
                .from(pokemon)
                .fetchFirst();
    }
}