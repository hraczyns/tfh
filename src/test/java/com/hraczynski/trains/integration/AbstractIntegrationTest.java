package com.hraczynski.trains.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractIntegrationTest {

    private static final String GET = "get";
    private static final String GET_CLASS = "getClass";

    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected Environment env;

    protected void verifyWithJson(String contentAsString, String jsonName) throws URISyntaxException, IOException {
        Objects.requireNonNull(contentAsString);
        Objects.requireNonNull(jsonName);
        String reference = getString(jsonName);
        assertThat(contentAsString).isEqualTo(reference);
    }

    protected <T> T instantiate(String jsonEntity, Class<T> type) throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonEntity, type);
    }

    protected void simpleVerifyGet(String apiPath, String jsonPath) throws Exception {
        String contentAsString = mvc.perform(get(apiPath)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyWithJson(contentAsString, jsonPath);
    }

    protected void simpleVerifyPost(String apiPath, String jsonPath, Object arg) throws Exception {
        String contentAsString = mvc.perform(post(apiPath)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(arg))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyWithJson(contentAsString, jsonPath);
    }

    protected void simpleVerifyDelete(String apiPath, String jsonPath) throws Exception {
        String contentAsString = mvc.perform(delete(apiPath)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verifyWithJson(contentAsString, jsonPath);
    }

    protected <T, U> void simpleVerifyPut(String apiPath, T requestObject, Long id, Class<U> clazzForInst) throws Exception {
        mvc.perform(put(apiPath)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(requestObject)))
                .andExpect(status().isNoContent());
        U instantiated = instantiate(findByIdJson(apiPath + "/" + id), clazzForInst);

        verifyPut(requestObject, instantiated);
    }

    protected String findByIdJson(String apiPath) throws Exception {
        return mvc.perform(get(apiPath)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private <T, U> void verifyPut(T requestObject, U instantiated) {
        verifyPatch(requestObject, null, instantiated);
    }

    protected <T, U> void verifyPatch(U requestObject, T beforeChanges, T afterChanges) {
        Map<Method, Object> map = getNotEmptyAndNotNullValues(requestObject);
        map.forEach((method, value) -> Arrays.stream(afterChanges.getClass().getMethods())
                .filter(s -> {
                    if (!s.getName().equals(method.getName())) {
                        return false;
                    }
                    try {
                        Object invoked = method.invoke(requestObject);
                        if (invoked instanceof String) {
                            return !((String) invoked).isEmpty();
                        }
                        return invoked != null;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Fatal error during obtaining value from input object!");
                    }
                })

                .findFirst()
                .ifPresentOrElse(s -> {
                    try {
                        Object ref = s.invoke(afterChanges);
                        assertThat(value).isEqualTo(ref);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Fatal error during obtaining value from input object!");
                    }
                }, () -> {
                    Method methodNotTouched = Arrays.stream(afterChanges.getClass().getMethods())
                            .filter(s -> s.getName().equals(method.getName()))
                            .findFirst()
                            .orElseThrow();
                    try {
                        Object invoke1 = methodNotTouched.invoke(afterChanges);
                        if (beforeChanges != null) {
                            Object invoke = methodNotTouched.invoke(beforeChanges);
                            assertThat(invoke1).isEqualTo(invoke);
                        } else {
                            assertThat(invoke1).isNull();
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }));
    }

    private String getString(String jsonName) throws URISyntaxException, IOException {
        return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource("/static/" + jsonName)).toURI())).replaceAll("\\s+", "");
    }

    private <U> Map<Method, Object> getNotEmptyAndNotNullValues(U requestObject) {
        return Arrays.stream(requestObject.getClass().getMethods())
                .filter(method -> method.getName().startsWith(GET) && !GET_CLASS.equals(method.getName()))
                .collect(HashMap::new,
                        (map, method) -> {
                            try {
                                map.put(method, method.invoke(requestObject));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                throw new RuntimeException("Fatal error during obtaining value from input object!");
                            }
                        },
                        Map::putAll
                );
    }

}
