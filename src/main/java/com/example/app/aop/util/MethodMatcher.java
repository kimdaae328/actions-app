package com.example.app.aop.util;

import java.lang.reflect.Method;

public interface MethodMatcher {
    boolean matches(Method method);
}
