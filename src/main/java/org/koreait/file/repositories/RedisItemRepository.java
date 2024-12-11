package org.koreait.file.repositories;


import org.koreait.file.entities.RedisItem;
import org.springframework.data.repository.CrudRepository;

/**
 * Redis Repository
 *
 */
public interface RedisItemRepository extends CrudRepository<RedisItem, String> {
}