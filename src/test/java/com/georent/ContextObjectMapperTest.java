package com.georent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.dto.RentOrderRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContextObjectMapperTest {


    @Autowired
    ObjectMapper objectMapper;

    @Test
    void objectMapperNotNullTest() {
        assertThat(objectMapper != null);
    };

    @Test
    void contextObjectMapperLocalDateTimeSerializationTest() throws IOException {

        Path path = Paths.get("src/test/resources/rent_order.json");

        LocalDateTime now = LocalDateTime.of(2019, 06, 29, 13, 30,39);
        LocalDateTime then = LocalDateTime.of(2019, 07, 01, 13, 30 ,39);

        RentOrderRequestDTO dto = new RentOrderRequestDTO();
        dto.setLotId(13L);
        dto.setStartTime(now);
        dto.setEndTime(then);

        String jsonOrderRequestDTO = objectMapper.writeValueAsString(dto);

        log.info("DTO: {}", jsonOrderRequestDTO);
        log.info(now.toString());
        log.info(then.toString());

        String expected = Files.readAllLines(path)
                .stream()
                .map(string -> string.replaceAll("\\s",""))
                .collect(Collectors.joining());

        assertEquals(expected, jsonOrderRequestDTO);


    };

    @Test
    void contextObjectMapperLocalDateTimeDeserializationTest() throws IOException {

        Path path = Paths.get("src/test/resources/rent_order.json");

        RentOrderRequestDTO dto = objectMapper.readValue(path.toFile(), RentOrderRequestDTO.class);

        Long expectedId = 13L;
        LocalDateTime expectedStartTime =  LocalDateTime.of(2019, 06, 29, 13, 30,39);
        LocalDateTime expectedEndTime =  LocalDateTime.of(2019, 07, 01, 13, 30 ,39);

        assertEquals(expectedId, dto.getLotId());
        assertEquals(expectedStartTime, dto.getStartTime());
        assertEquals(expectedEndTime, dto.getEndTime());

        log.info(dto.toString());
    }
}
