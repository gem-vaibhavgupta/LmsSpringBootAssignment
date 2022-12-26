package com.gemini.LMS.controller;

import com.gemini.LMS.model.UserCreds;
import com.gemini.LMS.model.UserInfo;
import com.gemini.LMS.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfo> login(@RequestBody final UserCreds userCreds){
        LOGGER.info("User Logging In -> {} ",userCreds.getUsername());
        UserInfo userInfo = loginService.loginUser(userCreds);
        LOGGER.info("User Login details -> {}",userInfo);
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }


}
