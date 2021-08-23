package com.hraczynski.trains;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountryTemp {
    @JsonProperty("name")
    private String name;
    @JsonProperty("code")
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CountryTemp{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
