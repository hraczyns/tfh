package com.hraczynski.trains.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.exceptions.definitions.ErrorDuringPropertiesCopying;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class PropertiesCopier {

    private static final int ESCAPED_SET_OR_GET = 3;

    public static <T> void copyNotNullAndNotEmptyProperties(T source, T destination) {
        try {
            BeanUtils.copyProperties(source, destination, getIgnoredValues(source));
        } catch (Exception e) {
            log.error("Error during copying properties. Cause: {} ", e.getMessage());
        }
    }

    public static void copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(Object source, Object destination) {
        copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(source, destination, "");
    }

    public static void copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(Object source, Object destination, String... skipArray) {
        try {
            log.info("Copying properties from {} to {}", source.getClass().getName(), destination.getClass().getName());
            Method[] methods = filterStartsWith(source, "get", skipArray);
            Method[] methodsDesc = filterStartsWith(destination, "set", skipArray);
            for (Method methodFromSource : methods) {
                Optional<Method> methodDescOpt = Arrays.stream(methodsDesc)
                        .filter(s -> s.getName().substring(ESCAPED_SET_OR_GET).equals(methodFromSource.getName().substring(ESCAPED_SET_OR_GET)))
                        .findFirst();
                if (methodDescOpt.isEmpty()) {
                    continue;
                }
                Class<?> returnedTypeFromSource = methodFromSource.getReturnType();
                Method methodDesc = methodDescOpt.get();
                Class<?>[] paramMethodDescArray = methodDesc.getParameterTypes();
                if (!Void.TYPE.equals(returnedTypeFromSource) && paramMethodDescArray.length == 1) {
                    Class<?> paramMethodDesc = paramMethodDescArray[0];
                    if (returnedTypeFromSource.equals(paramMethodDesc)) {
                        Object returned = methodFromSource.invoke(source);
                        if (returned != null) {
                            if (returned instanceof String) {
                                if (!((String) returned).isEmpty()) {
                                    methodDesc.invoke(destination, returned);
                                }
                            } else {
                                methodDesc.invoke(destination, returned);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String sourceClass = null;
            String destClass = null;
            if (source != null)
                sourceClass = source.getClass().getName();
            if (destination != null) {
                destClass = destination.getClass().getName();
            }
            log.error("Error during copying properties using different classes {} and {}. Cause: {} ", sourceClass, destClass, e.getMessage());
            throw new ErrorDuringPropertiesCopying(sourceClass, destClass);
        }
    }

    private static Method[] filterStartsWith(Object object, String prefix, String[] skipArray) {
        return Arrays.stream(object.getClass().getMethods())
                .filter(s -> {
                    boolean startsWith = s.getName().startsWith(prefix);
                    boolean noneMatchSkipValues = Arrays.stream(skipArray)
                            .noneMatch(skip -> s.getName().substring(prefix.length()).equalsIgnoreCase(skip));
                    return startsWith && noneMatchSkipValues;
                })
                .toArray(Method[]::new);
    }

    private static String[] getIgnoredValues(Object source) {
        BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null || String.valueOf(wrappedSource.getPropertyValue(propertyName)).isEmpty())
                .toArray(String[]::new);
    }
}
