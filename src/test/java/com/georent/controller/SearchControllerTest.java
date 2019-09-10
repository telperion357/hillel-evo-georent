package com.georent.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SearchControllerTest {
    private SearchController searchControllerTest = mock(SearchController.class);

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchControllerTest).build();
    }

    @Test
    public void findLotsAdrName_mapping_get_FiltersAddressAndLotName_Return_Status_ok() throws Exception {
        mockMvc.perform(get("/search/filters/?address=Kuiv&lotname=lotName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getPage_mapping_get_PageFiltersAddressAndLotName_Return_Status_ok() throws Exception {
        mockMvc.perform(get("/search/page/1/3/next/?address=Kuiv&lotname=lotName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }


}
