package com.georent.service;

import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import com.georent.dto.MethodPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DescriptionSearchServiceTest {
    private final String searchTerm = "Kiev";
    private final String lotName = "lotName2";

    @Autowired
    DescriptionSearchService descriptionSearchService;

    @Test
    void descriptionSearchServiceSuccessfullyStarted() {
        assertThat(descriptionSearchService != null);
    }

    @Test
    void whenFuzzyLotNameAndAddressSearchSuccessfully_Return_SetLotPageDTO() {
        Set<LotPageDTO> actualPageDTO = descriptionSearchService.fuzzyLotNameAndAddressSearch(searchTerm, lotName);
        Assert.assertNotNull(actualPageDTO);
    }

    @Test
    void whenFuzzyLotPageNameMethodPageNumberVarMethodPageVar_Return_Return_LotPageableTotalPagesZeroPageNumberOne () {
        for (int i=-2; i <= 2; i++) {
            for (MethodPage type: MethodPage.values()) {
                String msg = "Method: " + type.getTypeValue() + " pageNumber: " + i;
                System.out.println(msg);
                log.info(msg);
                LotPageable actualLotPageable = descriptionSearchService.fuzzyLotPageNameAndAddressSearch(
                        i,
                        3,
                        type.getTypeValue(),
                        searchTerm,
                        lotName,
                        false);
                Assert.assertNotNull(actualLotPageable);
                Assert.assertEquals(0, actualLotPageable.getTotalPages());
                Assert.assertEquals(1, actualLotPageable.getPageNumber());
                msg = "Ok: " + "Method: " + type.getTypeValue() + " pageNumber: " + i;
                System.out.println(msg);
                log.info(msg);
            }
        }
    }
}



