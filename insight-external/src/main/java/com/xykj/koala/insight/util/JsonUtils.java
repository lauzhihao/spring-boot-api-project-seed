package com.xykj.koala.insight.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author liuzhihao
 * @date 2018/4/10
 */
@Slf4j
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String EMPTY_STRING = "";

    static {
        OBJECT_MAPPER
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String toJson(Object obj) {
        try {
            return Objects.isNull(obj) ? EMPTY_STRING : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.toString());
        }

        return EMPTY_STRING;
    }

    public static <T> Optional<T> fromJson(String json, Class<T> clazz) {

        try {
            return isNullOrEmpty(json) ? Optional.empty() : Optional.of(OBJECT_MAPPER.readValue(json, clazz));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
        return Optional.empty();
    }
}
