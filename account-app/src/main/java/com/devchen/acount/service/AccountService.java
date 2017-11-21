package com.devchen.acount.service;

import com.devchen.acount.dal.entity.AccountEntity;
import com.devchen.acount.dal.repo.AccountRepo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service("accountService")
public class AccountService {

    private final static Logger logger = Logger.getLogger(AccountService.class);

    @Resource
    private AccountRepo accountRepo;

    public String loginIn(String username, String password, HttpSession httpSession) {
        AccountEntity account = accountRepo.findByUsername(username);
        if(account!=null && account.getPassword().equals(password)) {
            httpSession.setMaxInactiveInterval(3600000);
            httpSession.setAttribute("login-status", "logined");
            return "success login in";
        }
        return "fail login in";
    }
}
