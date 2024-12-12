package org.koreait.wishlist.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.entitis.QWish;
import org.koreait.wishlist.entitis.Wish;
import org.koreait.wishlist.entitis.WishId;
import org.koreait.wishlist.repositories.WishRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 찜하기 추가 & 삭제 기능
 * 
 */
@Lazy
@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository repository;

    private final MemberUtil memberUtil;

    private final JPAQueryFactory queryFactory;

    // mode 값(add / remove)에 따른 처리
    public void process(String mode, Long seq, WishType type) {

        if (!memberUtil.isLogin()) {

            return;
        }

        mode = StringUtils.hasText(mode) ? mode : "add";

        Member member = memberUtil.getMember();

        try {

            if (mode.equals("remove")) {// 찜 해제

                WishId wishId = new WishId(seq, type, member);
                repository.deleteById(wishId);

            } else { // 찜 추가

                Wish wish = new Wish();

                wish.setSeq(seq);
                wish.setType(type);
                wish.setMember(member);

                repository.save(wish);
            }
            repository.flush();
            
        } catch (Exception e) {}
    }

    /**
     * 회원별 찜 목록 조회
     *
     * @return
     */
    public List<Long> getMyWish(WishType type) {

        if (!memberUtil.isLogin()) {

            return null;
        }

        QWish wish = QWish.wish;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(wish.member.eq(memberUtil.getMember()))
                .and(wish.type.eq(type));

        List<Long> items = queryFactory.select(wish.seq)
                .from(wish)
                .where(builder)
                .fetch();

        return items;
    }
}