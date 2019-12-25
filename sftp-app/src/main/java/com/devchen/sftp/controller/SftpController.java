package com.devchen.sftp.controller;


import com.devchen.sftp.controller.resp.UnionResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sftp")
public class SftpController {



    @RequestMapping(value = "/lock-sftp-request", method = RequestMethod.POST)
    public UnionResponse proxySftpDownload(@RequestParam("requestId") String requestId,
                                     @RequestParam("sftpPath") String sftpPath,
                                     @RequestParam("fileName") String fileName,
                                     @RequestParam("host") String host,
                                     @RequestParam("port") String port,
                                     @RequestParam("userName") String userName,
                                     @RequestParam("password") String password){

        return null;
    }

}
