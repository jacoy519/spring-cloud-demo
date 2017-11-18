package com.devchen.acount.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestResource {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "hello from account-app";
    }
}
