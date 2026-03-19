package com.aquariux.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
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
    private Precision precision = new Precision();
    private Fee fee = new Fee();

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

    @Getter
    @Setter
    public static class Precision {
        private int quantityScale = 8;
        private int priceScale = 8;
        private int quoteScale = 8;
        private int feeScale = 8;
        private String roundingMode = "HALF_UP";
    }

    @Getter
    @Setter
    public static class Fee {
        private BigDecimal rate = new BigDecimal("0.0010");
        private String asset = "USDT";
    }
}
