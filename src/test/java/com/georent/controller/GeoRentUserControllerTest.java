package com.georent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.dto.GeoRentUserUpdateDto;
import com.georent.dto.RegistrationLotDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GeoRentUserControllerTest {

    private GeoRentUserController controllerToTest = mock(GeoRentUserController.class);

    ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerToTest).build();
    }

    @Test
    public void getUserInfo_mapping_get_user_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateUser_mapping_patch_user_Return_Status_ok () throws Exception {
        GeoRentUserUpdateDto reqest = new GeoRentUserUpdateDto();
        reqest.setFirstName("firstName");
        reqest.setLastName("lastName");
        reqest.setPhoneNumber("123456789012");

        mockMvc.perform(patch("/user").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deletetUser_mapping_delete_user_Return_Status_ok () throws Exception {
        mockMvc.perform(delete("/user/user2@gmail.com.ua")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserLots_mapping_get_user_lots_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserLotId_mapping_get_user_lot_id_Return_Status_ok () throws Exception {
        mockMvc.perform(get("/user/lot/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void setUserLot_mapping_post_user_lot_Return_Status_ok() throws Exception {
        RegistrationLotDto reqest = new RegistrationLotDto();
        reqest.setPrice(200L);
        reqest.setLatitude(801.800f);
        reqest.setLongitude(901.900f);
        reqest.setAddress("100 Киев 14");
        reqest.setLotName("New Lot Name");
        reqest.setLotDescription("lotDescription2 lotDescription lotDescription");

        mockMvc.perform(post("/user/lot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deletetLotId_mapping_delete_user_lot_id_Return_Status_ok() throws Exception {
        mockMvc.perform(delete("/user/lot/8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deletetLots_mapping_delete_user_lots_Return_Status_ok() throws Exception {
        mockMvc.perform(delete("/user/lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
