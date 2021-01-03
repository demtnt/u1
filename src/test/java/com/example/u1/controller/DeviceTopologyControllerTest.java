package com.example.u1.controller;

import com.example.u1.networkdeployment.DeviceNotFoundException;
import com.example.u1.networkdeployment.DeviceTopologyService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceTopologyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceTopologyService deviceTopologyService;

    @Test
    void wrongMACShouldReturnNotFound() throws Exception {
        doThrow(DeviceNotFoundException.class).when(deviceTopologyService).printDeviceSubtree(anyString(), anyInt());

        mockMvc.perform(get("/devicetopology/{mac}", "WRONG_MAC"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void printTopologyByMAC() throws Exception {
        final var expectedResult = "" + System.currentTimeMillis();
        when(deviceTopologyService.printDeviceSubtree(anyString(), anyInt())).thenReturn(expectedResult);

        mockMvc.perform(get("/devicetopology/{mac}", "MAC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(equalTo(expectedResult)))
                ;
    }
}