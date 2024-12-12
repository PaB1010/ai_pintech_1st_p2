package org.koreait.dl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * 예측 Service
 *
 */
@Lazy
@Service
@Profile("dl")
public class PredictService {

    @Value("${python.run.path}")
    private String runPath;

    @Value("${python.script.path}")
    private String scriptPath;

    @Value("${python.data.url}")
    private String dataUrl;

    @Autowired
    private ObjectMapper om;

    // List 여러개인 형태 -> JSON
    public int[] predict(List<int[]> items) {

        try {
            
            String data = om.writeValueAsString(items);

            ProcessBuilder builder = new ProcessBuilder(runPath, scriptPath + "predict.py", dataUrl + "?mode=ALL", data);

            Process process = builder.start();

            InputStream in = process.getInputStream();

            // 실행결과를 InputStream 으로 받음
            // 문자열을 int 배열로 변경하기

            //System.out.println(Arrays.toString(in.readAllBytes()));

            return om.readValue(in.readAllBytes(), int[].class);

        } catch (Exception e) {

            e.printStackTrace();
        }
        
        // 없을때에는 null 대체
        return null;
    }
}