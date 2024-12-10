package org.koreait.dl.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.dl.entities.TrainItem;
import org.koreait.dl.services.PredictService;
import org.koreait.dl.services.TrainService;
import org.koreait.global.rests.JSONData;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * 머신 러닝 RestController
 *
 * EX) 사용자가 접속한 url을 해시화해 학습
 *     사용자가 조회 & 찜한 포켓몬을 학습해 다음 관심 포켓몬을 예측
 *     사용자 연령층에 따라서 많이 선택하는 상품 추천 등
 *
 */
@Profile("dl")
@RestController
@RequestMapping("/api/dl")
@RequiredArgsConstructor
public class ApiDlController {

    private final TrainService trainService;

    private final PredictService predictService;

    // 임시 랜덤 숫자 1000개 학습
    // 규칙성이 없어서 정답률 20% 정도 예상
    @GetMapping("/data")
    public List<TrainItem> sendData() {

        Random random = new Random();

        List<TrainItem> items = IntStream.range(0, 1000)
                .mapToObj(i -> TrainItem.builder()
                        .item1(random.nextInt())
                        .item2(random.nextInt())
                        .item3(random.nextInt())
                        .item4(random.nextInt())
                        .item5(random.nextInt())
                        .item6(random.nextInt())
                        .item7(random.nextInt())
                        .item8(random.nextInt())
                        .item9(random.nextInt())
                        .item10(random.nextInt())
                        .result(random.nextInt(4)) // 0 ~ 4 반복
                        .build()
                ).toList();
        return items;
    }

    // 예측
    @PostMapping("/predict")
    public JSONData predict(@RequestParam("items") List<int[]> items) {

        int[] predictions = predictService.predict(items);

        return new JSONData(predictions);
    }
}