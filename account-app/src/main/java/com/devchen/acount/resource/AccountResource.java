package com.devchen.acount.resource;


import com.devchen.acount.resource.entity.UnionResponse;
import com.devchen.acount.resource.factory.UnionResponseFactory;
import com.devchen.acount.service.AccountService;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/account")
public class AccountResource {

    private final static Logger logger = Logger.getLogger(AccountResource.class);

    @Resource
    private AccountService accountService;

    @RequestMapping(value = "/login-in", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnionResponse loginIn(@RequestParam("account") String account, @RequestParam("password") String password, HttpServletRequest httpServletRequest) {
        logger.info("login in request with account " + account + " password " + password);
        String response = accountService.loginIn(account, password, httpServletRequest.getSession());
        return UnionResponseFactory.createSuccessResponse(response);
    }

    @RequestMapping(value = "/change-pw-after-login-in", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnionResponse changePwAfterloginIn(@RequestParam("account") String account, @RequestParam("password") String password, HttpServletRequest httpServletRequest) {
        return UnionResponseFactory.createSuccessResponse("change success");
    }
}
