package com.hraczynski.trains.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hraczynski.trains.exceptions.responses.ApiSubError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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

    protected void verifyWithJson(String contentAsString, String jsonName, HttpStatus status) throws URISyntaxException, IOException {
        Objects.requireNonNull(contentAsString);
        Objects.requireNonNull(jsonName);
        String reference = getString(jsonName);
        if (status.isError()) {
//            !there is some issue with jackson library and I can't delete node using this.
//            JsonNode jsonNode = mapper.readValue(contentAsString, JsonNode.class);
//            jsonNode.forEach(node -> ((ObjectNode) node).remove("timestamp"));
//            contentAsString = mapper.writeValueAsString(jsonNode);
            Gson gson = new Gson();
            JsonElement jsonObj = gson.fromJson(contentAsString, JsonElement.class);
            JsonObject asJsonObject = jsonObj.getAsJsonObject();
            asJsonObject.remove("timestamp");
            Pair<String, String> result = verifySubErrorsIfExist(asJsonObject, reference);
            contentAsString = result.getFirst();
            reference = result.getSecond();
        }
        contentAsString = contentAsString.replaceAll("\\s+", "");
        reference = reference.replaceAll("\\s+", "");
        assertThat(contentAsString).isEqualTo(reference);
    }

    @SuppressWarnings("unchecked")
    private Pair<String, String> verifySubErrorsIfExist(JsonObject jsonObj, String reference) {
        Gson gson = new Gson();
        JsonElement jsonElementRef = gson.fromJson(reference, JsonElement.class);
        JsonElement ref = jsonElementRef.getAsJsonObject().get("subErrors");
        if (ref != null) {
            Set<ApiSubError> fromJson = gson.fromJson(ref, Set.class);
            Set<ApiSubError> fromJsonRef = gson.fromJson(jsonObj.get("subErrors"), Set.class);
            assertThat(fromJsonRef).isEqualTo(fromJson);
            jsonElementRef.getAsJsonObject().remove("subErrors");
            jsonObj.getAsJsonObject().remove("subErrors");
        }
        return Pair.of(gson.toJson(jsonObj), gson.toJson(jsonElementRef));
    }

    protected <T> T instantiate(String jsonEntity, Class<T> type) throws JsonProcessingException {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonEntity, type);
    }

    protected void simpleVerifyGet(String apiPath, String jsonPath) throws Exception {
        simpleVerifyGet(apiPath, jsonPath, HttpStatus.OK);
    }

    private void genericVerify(String apiPath, String jsonPath, HttpMethod method, HttpStatus status, Object arg) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = getMockHttpServletRequestBuilder(method, apiPath, arg);
        MvcResult mvcResult = mvc.perform(mockHttpServletRequestBuilder).andExpect(status().is(status.value())).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        verifyWithJson(contentAsString, jsonPath, status);
    }

    protected void simpleVerifyGet(String apiPath, String jsonPath, HttpStatus status) throws Exception {
        genericVerify(apiPath, jsonPath, HttpMethod.GET, status, null);
    }

    protected void simpleVerifyPost(String apiPath, String jsonPath, Object arg) throws Exception {
        simpleVerifyPost(apiPath, jsonPath, arg, HttpStatus.CREATED);
    }

    protected void simpleVerifyPost(String apiPath, String jsonPath, Object arg, HttpStatus status) throws Exception {
        genericVerify(apiPath, jsonPath, HttpMethod.POST, status, arg);
    }

    protected void simpleVerifyDelete(String apiPath, String jsonPath) throws Exception {
        simpleVerifyDelete(apiPath, jsonPath, HttpStatus.OK);
    }

    protected void simpleVerifyDelete(String apiPath, String jsonPath, HttpStatus status) throws Exception {
        genericVerify(apiPath, jsonPath, HttpMethod.DELETE, status, null);
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

    private MockHttpServletRequestBuilder getMockHttpServletRequestBuilder(HttpMethod method, String apiPath, Object argJson) throws JsonProcessingException {
        return switch (method) {
            case GET -> get(apiPath).accept(MediaType.APPLICATION_JSON_VALUE);
            case POST -> post(apiPath).contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsBytes(argJson));
            case PUT -> put(apiPath).contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsBytes(argJson));
            case PATCH -> patch(apiPath).contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsBytes(argJson));
            case DELETE -> delete(apiPath).accept(MediaType.APPLICATION_JSON_VALUE);
            default -> throw new IllegalStateException("Unexpected value: " + method);
        };
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
        return Files.readString(Paths.get(Objects.requireNonNull(getClass().getResource("/static/test_jsons/" + jsonName)).toURI()));
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
