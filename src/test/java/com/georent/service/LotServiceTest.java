package com.georent.service;

import com.georent.domain.Lot;
import com.georent.dto.LotDTO;
import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import com.georent.dto.LotShortDTO;
import com.georent.repository.LotRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.georent.service.ServiceTestUtils.createTestLot;
import static com.georent.service.ServiceTestUtils.createTestLotDTO;
import static com.georent.service.ServiceTestUtils.createTestShortLotDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class LotServiceTest {
    private static final Lot    TEST_LOT = createTestLot();
    private static final LotDTO TEST_LOT_DTO = createTestLotDTO();
    private static final LotDTO TEST_SHORT_LOT_DTO = createTestShortLotDTO();

    private LotService lotService;
    private LotRepository mockRepository = mock(LotRepository.class);
    private AWSS3Service mockService = mock(AWSS3Service.class);

    @Before
    public void setup() {
        lotService = new LotService(mockRepository, mockService);
    }

    @Test
    public void When_Service_GetAllLots_Return_AllLotsDto() {
        // given
        List<Lot> testLots = Arrays.asList(TEST_LOT);
        when(mockRepository.findAll()).thenReturn(testLots);
        List<LotDTO> expectedLotDTOS = Arrays.asList(TEST_SHORT_LOT_DTO);
        // when
        List<LotShortDTO> actualLotDTOS = lotService.getLotsDto();
        verify(mockRepository, times(1)).findAll();
        // then
        Assert.assertEquals(expectedLotDTOS, actualLotDTOS);
    }

    @Test
    public void When_Service_getLotDto_Id_One_Return_LotDto_Id_One() {
        // given
        when(mockRepository.findById(any(Long.class))).thenReturn(Optional.of(TEST_LOT));
        // when
        LotDTO actualLotDTO = lotService.getLotDto(TEST_LOT.getId());
        verify(mockRepository, times(1)).findById(any(Long.class));
        // then
        Assert.assertEquals(TEST_LOT_DTO, actualLotDTO);
    }


    @Test    public void When_Service_GetPage_Return_LotsPageDTO() {
        List<Lot> lots = new ArrayList<>();
        Page<Lot> pagedLots = new PageImpl(lots);
        Pageable pageRequest = PageRequest.of(0, 4);
        when(this.mockRepository.findAll(pageRequest)).thenReturn(pagedLots);
        LotPageable lotPageable = lotService.getPage(0, 4, "", null);
        verify(mockRepository, times(1)).findAll(pageRequest);
        Assert.assertEquals(lotPageable.getPageNumber(), 0);
        Assert.assertEquals(lotPageable.getLots().size(), 4);
    }


}
