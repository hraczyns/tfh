package com.hraczynski.trains.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public final class JsonMapper {

    public Map<String, Object> parse(String json) throws IOException {
        return new ObjectMapper().readValue(getResource(json), new TypeReference<>() {
        });
    }

    private File getResource(String json) {
        return new File(Objects.requireNonNull(getClass().getResource(json)).getFile());
    }

}
