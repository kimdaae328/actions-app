package com.example.app.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({MemberLoginFailException.class})
    public RedirectView handleMemberLoginFailException(MemberLoginFailException e){
        return new RedirectView("/member/login");
    }

    @ExceptionHandler({PostNotFoundException.class})
    public RedirectView handlePostNotFoundException(PostNotFoundException e){
        return new RedirectView("/post/list/1");
    }

    @ExceptionHandler({MemberNotFoundException.class})
    public RedirectView handleMemberNotFoundException(MemberNotFoundException e){
        return new RedirectView("/member/login");
    }
}
