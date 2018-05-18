package com.rmadegps.rmadeservermanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restapi/rpt/app")
public class page_404
{

    @RequestMapping("/404.html")
    public String render404 ()
    {
        String jsonData = "{\"result\":\"failure\"}";
        return jsonData;
    }
}


