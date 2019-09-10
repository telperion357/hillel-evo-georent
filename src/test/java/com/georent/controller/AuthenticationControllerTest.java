package com.georent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.georent.domain.GeoRentUser;
import com.georent.dto.LoginRequestDTO;
import com.georent.dto.RegistrationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationControllerTest {

    private AuthenticationController controllerToTest = mock(AuthenticationController.class);

    ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controllerToTest).build();
    }

    @Test
    public void authenticateUser_mapping_post_login_Return_Status_ok () throws Exception {
        LoginRequestDTO reqest = new LoginRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");
        reqest.setPassword( "pass5678910");
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void registerUser_mapping_post_register_Return_Status_ok () throws Exception {
        RegistrationRequestDTO reqest = new RegistrationRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");
        reqest.setPassword( "pass5678910");

        GeoRentUser user = new GeoRentUser();
        user.setEmail(reqest.getEmail());
        user.setFirstName(reqest.getFirstName());
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void registerUser_mapping_post_register_Return_Status_isBadRequest () throws Exception {
        RegistrationRequestDTO reqest = new RegistrationRequestDTO();
        reqest.setEmail("mkyong@gmail23.com.aa");

        GeoRentUser user = new GeoRentUser();
        user.setEmail(reqest.getEmail());
        user.setFirstName(reqest.getFirstName());
        mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reqest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
