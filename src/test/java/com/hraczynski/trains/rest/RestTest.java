package com.hraczynski.trains.rest;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Builder
public class RestTest<T> {

    private static final String API_URL = "http://localhost:8084/api/";
    private static final String GET = "get";

    private final JsonMapper mapper = new JsonMapper();
    private final MockMvc mockMvc;
    private final HttpMethod httpMethod;
    private final String uri;
    private final String json;
    private final T objectToCompare;

    public void assertRest() {
        try {
            Map<String, Object> json = mapper.parse("/static/" + this.json);

            ResultActions resultActions = mockMvc.perform(getMockMvcRequestBuilders());

            resultActions.andExpect(status().is(getStatus()));
            resultActions.andReturn().getResponse().getContentAsString();
            createAndExpectSections(json, objectToCompare);

        } catch (Exception e) {
            fail("Test failed. Cause: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createAndExpectSections(Map<String, Object> json, Object actualObject) throws Exception {
        for (Map.Entry<String, Object> stringObjectEntry : json.entrySet()) {

            if (isMap(stringObjectEntry.getValue())) {
                createAndExpectSections((Map<String, Object>) stringObjectEntry.getValue(), actualObject);
            }
            if (isIterable(stringObjectEntry.getValue())) {
                for (Object obj : ((List<Object>) stringObjectEntry.getValue())) {
                    if (isMap(obj)) {
                        createAndExpectSections((Map<String, Object>) obj, actualObject);
                    }
                }
            }

            Method foundMethod = Arrays.stream(actualObject.getClass().getMethods())
                    .filter(method -> method.getName().startsWith(GET))
                    .filter(method -> stringObjectEntry.getKey().equalsIgnoreCase(method.getName().substring(GET.length())))
                    .findFirst()
                    .orElse(null);
            if (foundMethod == null) {
                continue;
            }
            Object result = foundMethod.invoke(objectToCompare);

            result = cast(result);

            assertThat(stringObjectEntry.getValue())
                    .isEqualTo(result);

        }
    }

    private boolean isIterable(Object value) {
        return value instanceof ArrayList;
    }

    private boolean isMap(Object value) {
        return value instanceof LinkedHashMap;
    }

    private Object cast(Object result) {
        if (result instanceof Long) {
            return ((Long) result).intValue();
        }
        return result;
    }

    private MockHttpServletRequestBuilder getMockMvcRequestBuilders() {
        String apiUrl = API_URL + uri;
        return switch (httpMethod) {
            case GET -> MockMvcRequestBuilders.get(apiUrl);
            case POST -> MockMvcRequestBuilders.post(apiUrl);
            case PUT -> MockMvcRequestBuilders.put(apiUrl);
            case DELETE -> MockMvcRequestBuilders.delete(apiUrl);
        };
    }

    private int getStatus() {
        return switch (httpMethod) {
            case GET -> HttpStatus.OK.value();
            case POST -> HttpStatus.CREATED.value();
            case PUT, DELETE -> HttpStatus.NO_CONTENT.value();
        };
    }


    public enum HttpMethod {
        GET, POST, DELETE, PUT
    }

}
