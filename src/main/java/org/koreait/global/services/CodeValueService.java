package org.koreait.global.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.CodeValue;
import org.koreait.global.repositories.CodeValueRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * JSON(Code & Value) CRUD Service
 *
 */
@Service
@Lazy
@RequiredArgsConstructor
public class CodeValueService {

    private final CodeValueRepository repository;

    private final ObjectMapper om;

    /**
     * JSON 문자열로 변환 후 저장
     *
     * @param code
     * @param value - 변환되는 Data, Save 시에는 Object 해도 상관 X
     */
    public void save(String code, Object value) {

        CodeValue item = repository.findById(code).orElseGet(CodeValue::new);

        try {
            // Object -> String 변환
            String json = om.writeValueAsString(value);

            item.setCode(code);
            item.setValue(json);

            repository.saveAndFlush(item);

        } catch (JsonProcessingException e) {}


    }

    /**
     * Json 조회 후 다시 원래 자료형으로 변환
     *
     * @param code
     * @param cls
     * @return
     * @param <R>
     */
    public <R> R get(String code, Class<R> cls) {

        CodeValue item = repository.findById(code).orElse(null);

        if (item != null) {

            String json = item.getValue();

            try {
                return om.readValue(json, cls);

            } catch (JsonProcessingException e) {}
        }
        return null;
    }

    /**
     * 삭제
     *
     * @param code
     */
    public void remove(String code) {

        repository.deleteById(code);

        repository.flush();
    }
}