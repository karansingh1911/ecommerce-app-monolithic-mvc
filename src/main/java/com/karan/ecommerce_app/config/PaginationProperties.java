package com.karan.ecommerce_app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "pagination") // to bind values defined in the configuration to the fields
@Component
@Getter
@Setter
public class PaginationProperties {
    private int defaultSize;
    private int defaultPage;
    private int maxPageSize;
}
