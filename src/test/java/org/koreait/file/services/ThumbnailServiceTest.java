package org.koreait.file.services;

import org.junit.jupiter.api.Test;
import org.koreait.file.controllers.RequestThumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ThumbnailServiceTest {

    @Autowired
    private ThumbnailService service;

    @Test
    void thumbPathTest() {

        RequestThumb form = new RequestThumb();

        form.setSeq(1052L);
        form.setWidth(100);
        form.setHeight(100);

        String path = service.getThumbPath(1052L, null, 100, 100);

        path = service.create(form);

        System.out.println(path);

        form.setSeq(null);
        form.setUrl("https://ssl.pstatic.net/melona/libs/1518/1518361/93d04f2de8962d16a596_20241129102344231.png");

        String path2 = service.getThumbPath(0L, "https://ssl.pstatic.net/melona/libs/1518/1518361/93d04f2de8962d16a596_20241129102344231.png", 100, 100);

        path2 = service.create(form);

        System.out.println(path2);
    }
}