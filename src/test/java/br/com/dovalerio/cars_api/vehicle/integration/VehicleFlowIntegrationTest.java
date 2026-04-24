package br.com.dovalerio.cars_api.vehicle.integration;

import br.com.dovalerio.cars_api.exchange.proxy.ExchangeRateProxy;
import br.com.dovalerio.cars_api.vehicle.repository.VehicleRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

        @MockitoBean
    private ExchangeRateProxy exchangeRateProxy;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        when(exchangeRateProxy.getUsdToBrlRate()).thenReturn(new BigDecimal("5.0"));
    }

    @Test
    void shouldExecuteFullVehicleFlow() throws Exception {
        String adminToken = loginAndGetToken("admin", "admin123");
        String userToken = loginAndGetToken("user", "user123");

        String plate = "TST" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String createPayload = """
                {
                  \"brand\": \"Toyota\",
                  \"model\": \"Corolla\",
                  \"year\": 2022,
                  \"color\": \"Black\",
                  \"plate\": \"%s\",
                  \"priceBrl\": 100000
                }
                """.formatted(plate);

        MvcResult createResult = mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plate").value(plate))
                .andReturn();

        String vehicleId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");
        assertThat(vehicleId).isNotBlank();

        mockMvc.perform(get("/veiculos")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        mockMvc.perform(get("/veiculos")
                        .param("marca", "Toyota")
                        .param("ano", "2022")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand").value("Toyota"));

        mockMvc.perform(get("/veiculos/{id}", vehicleId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId))
                .andExpect(jsonPath("$.plate").value(plate));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String payload = """
                {
                  \"username\": \"%s\",
                  \"password\": \"%s\"
                }
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }
}