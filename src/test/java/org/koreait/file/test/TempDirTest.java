package org.koreait.file.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

@DisplayName("임시 폴더 & 파일 객체 생성 @TempDir")
public class TempDirTest {

    @TempDir
    private static File tempDir;

    @Test
    void test1() {

        // 절대 경로
        String path = tempDir.getAbsolutePath();

        System.out.println(path);
    }

    @AfterAll
    static void destroy() {

        tempDir.delete();

        System.out.println(tempDir.exists());
    }
}