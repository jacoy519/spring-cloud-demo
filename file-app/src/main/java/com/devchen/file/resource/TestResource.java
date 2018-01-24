package com.devchen.file.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestResource {

    @RequestMapping("/")
    public String test1() {
        return "{" +
                "\"status\":\"success\"," +
                "\"session.id\":\"c001aba5-a90f-4daf-8f11-62330d034c0a\"" +
                "}";
    }

    @RequestMapping(value = "/executor", method = RequestMethod.GET)

    public String test2(@RequestParam("session.id") String test1,
                        @RequestParam("ajax") String ajax,
                        @RequestParam("project") String project,
                        @RequestParam("flow") String flow) {
        if(ajax.equals("getRunning")) {
            return "{\"execIds\":[295, 302]}";
        }
        return "{" +
                "message:\"Execution submitted successfully with exec id 295\"," +
                "project:\"foo-demo\"," +
                "flow:\"test\"," +
                "execid:295" +
                "}";
    }
}
