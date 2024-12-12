package org.koreait.wishlist.repositories;


import org.koreait.wishlist.entitis.Wish;
import org.koreait.wishlist.entitis.WishId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface WishRepository extends JpaRepository<Wish, WishId>, QuerydslPredicateExecutor<Wish> {
}