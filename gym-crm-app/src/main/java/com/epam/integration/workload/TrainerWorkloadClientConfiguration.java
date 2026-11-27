package com.epam.integration.workload;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.net.URI;
import java.util.function.Function;

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
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder trainerWorkloadLoadBalancedRestClientHttpClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @Profile("!docker")
    public RestClient trainerWorkloadHttpClient(
            RestClient.Builder builder,
            TrainerWorkloadProperties properties) {
        return buildRestClient(builder, properties);
    }

    @Bean
    @Profile("docker")
    public RestClient trainerWorkloadLoadBalancedHttpClient(
            RestClient.Builder builder,
            TrainerWorkloadProperties properties) {
        return buildRestClient(builder, properties);
    }

    private RestClient buildRestClient(
            RestClient.Builder builder,
            TrainerWorkloadProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    @Profile("!docker")
    public Function<URI, URI> trainerWorkloadAddressResolver() {
        return uri -> uri;
    }

    @Bean
    @Profile("docker")
    public Function<URI, URI> trainerWorkloadDiscoveryAddressResolver(LoadBalancerClient loadBalancer) {
        return uri -> {
            ServiceInstance instance = loadBalancer.choose(uri.getHost());
            if (instance == null) {
                throw new TrainerWorkloadUnavailableException(
                        new IllegalStateException("No instances available for " + uri.getHost()));
            }
            return loadBalancer.reconstructURI(instance, uri);
        };
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
