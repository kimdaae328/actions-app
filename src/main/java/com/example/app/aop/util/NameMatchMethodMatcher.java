package com.example.app.aop.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Method;

@RequiredArgsConstructor
@Primary
public class NameMatchMethodMatcher implements MethodMatcher {
    private final String methodName;

    @Override
    public boolean matches(Method method) {
        return methodName.equals(method.getName());
    }
}
