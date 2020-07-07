package com.sly.demo.webrtc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author SLY
 * @description TODO
 * @date 2020/7/7
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return "index.html";
    }
}
