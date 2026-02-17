package com.coanalysis.server.infrastructure.config;

import com.coanalysis.server.infrastructure.util.TimeZoneUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson ObjectMapper 설정
 * - LocalDateTime 직렬화 시 UTC -> KST 변환
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Java 8 Date/Time 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());

        // LocalDateTime을 KST로 직렬화하는 커스텀 모듈
        SimpleModule kstModule = new SimpleModule();
        kstModule.addSerializer(LocalDateTime.class, new LocalDateTimeKstSerializer());
        objectMapper.registerModule(kstModule);

        return objectMapper;
    }

    /**
     * LocalDateTime을 KST (UTC+9)로 변환하여 직렬화
     * DB에는 UTC로 저장되어 있으므로, 응답 시 KST로 변환하여 반환
     */
    private static class LocalDateTimeKstSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                LocalDateTime kstTime = TimeZoneUtil.toKst(value);
                gen.writeString(kstTime.format(FORMATTER));
            }
        }
    }
}
