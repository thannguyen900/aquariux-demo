package com.aquariux.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "trading")
public class AppProperties {

    private long demoUserId = 1L;
    private List<String> supportedPairs = new ArrayList<>();
    private Scheduler scheduler = new Scheduler();
    private External external = new External();

    @Getter
    @Setter
    public static class Scheduler {
        private long fixedRateMs = 10000L;
    }

    @Getter
    @Setter
    public static class External {
        private String binanceUrl;
        private String huobiUrl;
    }
}
