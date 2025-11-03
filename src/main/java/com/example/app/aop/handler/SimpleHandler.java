package com.example.app.aop.handler;

import com.example.app.aop.util.MethodMatcher;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class SimpleHandler implements InvocationHandler {
    private final Object target;
    private final MethodMatcher methodMatcher;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        invoke: 메소드 실행해주는 메소드
        Object returnValue = method.invoke(target, args);

//        리턴값이 문자열이고 PointCut에서 설정한 메소드 이름이 같다면, 주면로직(!)을 알맞은 시점에 실행해준다.
        if(returnValue instanceof String && methodMatcher.matches(method)){
            return ((String) returnValue) + "!";
        }

        return returnValue;
    }
}

















