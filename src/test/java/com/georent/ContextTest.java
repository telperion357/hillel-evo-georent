package com.georent;

import com.georent.service.AWSS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContextTest {

    @Autowired
    AWSS3Service awss3Service;

    @Test
    void contextSuccessfullyStarted() {
        assertThat(awss3Service!=null);
    }
}
