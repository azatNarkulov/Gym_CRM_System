package com.epam.workload.security;

import com.epam.workload.filter.WorkloadTransactionIdFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "JWT_SECRET=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
@AutoConfigureMockMvc
public class WorkloadSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRejectWorkloadRequestWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/trainers/unknown/workload")
                        .param("year", "2026")
                        .param("month", "11"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldAllowWorkloadRequestWithValidBearerToken() throws Exception {
        mockMvc.perform(get("/api/trainers/unknown/workload")
                        .header("Authorization", "Bearer " + createToken())
                        .param("year", "2026")
                        .param("month", "11"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAllowActuatorHealthWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnIncomingTransactionIdOnHealthResponse() throws Exception {
        String transactionId = "2c28f849-ddb1-42b8-b470-aa5f49339ff9";
        mockMvc.perform(get("/actuator/health")
                        .header(WorkloadTransactionIdFilter.HEADER, transactionId))
                .andExpect(status().isOk())
                .andExpect(header().string(WorkloadTransactionIdFilter.HEADER, transactionId));
    }

    private String createToken() {
        byte[] keyBytes = Base64.getDecoder()
                .decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("trainer.username")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60)))
                .signWith(key)
                .compact();
    }
}
