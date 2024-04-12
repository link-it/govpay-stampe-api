package it.govpay.stampe.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource("classpath:avvisi.properties")
public class LabelAvvisiConfiguration {

    @Data
    public static class LabelAvvisiProperties {
        private Map<String, String> it;
        private Map<String, String> sl;
        private Map<String, String> en;
        private Map<String, String> fr;
        private Map<String, String> de;
    }

    @Bean(name = "labelAvvisiProperties")
    @ConfigurationProperties(prefix = "")
    public LabelAvvisiProperties getLabelAvvisiProperties() {
        return new LabelAvvisiProperties();
    }
}