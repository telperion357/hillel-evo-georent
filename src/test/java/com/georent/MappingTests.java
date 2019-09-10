package com.georent;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

}
