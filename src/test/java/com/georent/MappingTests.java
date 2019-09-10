package com.georent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.georent.dto.RentOrderRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MappingTests {

    @Test
    void mappingTest() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> map = new HashMap<>();

        map.put("Hello", "World");
        map.put("Bye", "World");

        String s = mapper.writeValueAsString(map);

        log.info("Map: {}", s);

        String json = "{\"Hello\":\"World\",\"Bye\":\"World\"}";

        Map map1 = mapper.readValue(json, Map.class);

        log.info(map1.toString());
    }

    @Test
    void localDateTimeTest() throws JsonProcessingException {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime then = now.plusDays(2);

        RentOrderRequestDTO dto = new RentOrderRequestDTO();
        dto.setLotId(13L);
        dto.setStartTime(now);
        dto.setEndTime(then);

        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());

        String string = mapper.writeValueAsString(dto);

        log.info("DTO: {}", string);

        log.info(now.toString());
        log.info(then.toString());

    }

    @Test
    void localDateTimeDeserializationTest() throws IOException {

        Path path = Paths.get("src/test/resources/rent_order.json");

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        RentOrderRequestDTO dto = mapper.readValue(path.toFile(), RentOrderRequestDTO.class);

        log.info(dto.toString());

    }
}
