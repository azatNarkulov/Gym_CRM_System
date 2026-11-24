package com.epam.integration.workload;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(TrainerWorkloadProperties.class)
public class TrainerWorkloadClientConfiguration {

    @Bean
    @Profile("!docker")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder trainerWorkloadRestClientHttpClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Profile("docker")
    @LoadBalanced
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder trainerWorkloadLoadBalancedRestClientHttpClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Profile("!docker")
    public RestClient trainerWorkloadHttpClient(
            RestClient.Builder builder,
            TrainerWorkloadProperties properties,
            TrainerWorkloadRequestInterceptor interceptor) {
        return buildRestClient(builder, properties, interceptor);
    }

    @Bean
    @Profile("docker")
    public RestClient trainerWorkloadLoadBalancedHttpClient(
            @LoadBalanced RestClient.Builder builder,
            TrainerWorkloadProperties properties,
            TrainerWorkloadRequestInterceptor interceptor) {
        return buildRestClient(builder, properties, interceptor);
    }

    private RestClient buildRestClient(
            RestClient.Builder builder,
            TrainerWorkloadProperties properties,
            TrainerWorkloadRequestInterceptor interceptor) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    public CircuitBreaker trainerWorkloadCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(4)
                .minimumNumberOfCalls(2)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();
        return CircuitBreakerRegistry.of(config).circuitBreaker("trainerWorkload");
    }
}
