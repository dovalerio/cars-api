package br.com.dovalerio.cars_api.vehicle.controller;

import br.com.dovalerio.cars_api.common.exception.BusinessException;
import br.com.dovalerio.cars_api.vehicle.Vehicle;
import br.com.dovalerio.cars_api.vehicle.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void shouldReturn401WhenGetWithoutToken() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication required or invalid token"))
                .andExpect(jsonPath("$.path").value("/veiculos"));

        verifyNoInteractions(vehicleService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenPostAsUser() throws Exception {
        String body = validCreatePayloadJson("ABC1234");

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.path").value("/veiculos"));

        verifyNoInteractions(vehicleService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn201WhenPostAsAdmin() throws Exception {
        String body = validCreatePayloadJson("XYZ9876");
        Vehicle vehicle = Vehicle.create(
                "Toyota",
                "Corolla",
                2022,
                "Black",
                "XYZ9876",
                new BigDecimal("20000.00")
        );

        when(vehicleService.create(any())).thenReturn(vehicle);

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.plate").value("XYZ9876"));

        verify(vehicleService).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409WithStandardErrorPayloadWhenConflict() throws Exception {
        String body = validCreatePayloadJson("KLM1234");

        when(vehicleService.create(any())).thenThrow(new BusinessException("Plate already exists"));

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Plate already exists"))
                .andExpect(jsonPath("$.path").value("/veiculos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenPayloadIsInvalid() throws Exception {
        String invalidPayload = """
                {
                  \"brand\": \"\",
                  \"model\": \"\",
                  \"year\": 1800,
                  \"color\": \"\",
                  \"plate\": \"\",
                  \"priceBrl\": -1
                }
                """;

        mockMvc.perform(post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString(":")))
                .andExpect(jsonPath("$.path").value("/veiculos"));

        verifyNoInteractions(vehicleService);
    }

    private String validCreatePayloadJson(String plate) {
        return """
                {
                  \"brand\": \"Toyota\",
                  \"model\": \"Corolla\",
                  \"year\": 2022,
                  \"color\": \"Black\",
                  \"plate\": \"%s\",
                  \"priceBrl\": 100000
                }
                """.formatted(plate);
    }
}