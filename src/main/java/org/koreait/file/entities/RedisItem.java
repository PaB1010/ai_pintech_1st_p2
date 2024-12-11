package org.koreait.file.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * Redis 연습용
 *
 */
@Data
@RedisHash(value = "test_hash", timeToLive = 300)
public class RedisItem implements Serializable {

    // @Id 값은 필시 유지
    @Id
    private String key;

    // 그아래는 set 값 변경해도 상관 무

    private int price;

    private String productNm;
}