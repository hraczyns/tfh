package com.hraczynski.trains.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
@Getter
@Setter
public class EmailProperties {
    private String username;
    private String password;
}
