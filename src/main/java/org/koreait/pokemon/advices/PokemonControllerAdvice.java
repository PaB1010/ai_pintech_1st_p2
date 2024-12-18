package org.koreait.pokemon.advices;

import lombok.RequiredArgsConstructor;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Pokemon 도메인 Package 공통
 *
 */
@RequiredArgsConstructor
@ControllerAdvice("org.koreait.pokemon")
public class PokemonControllerAdvice {

    private final WishService wishService;

    // 나의 찜한 포켓몬 목록
    @ModelAttribute("myPokemons")
    public List<Long> myPokemons() {

        return wishService.getMyWish(WishType.POKEMON);
    }
}