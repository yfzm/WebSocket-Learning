package com.yfzm.tutorial.websocket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping("/info")
    public String info() {
        return "info";
    }

    @GetMapping("/msg/user1")
    public  String ToMessage(){
        return "/user1";
    }
    @GetMapping("/msg/user2")
    public  String ToMessaget2(){
        return "/user2";
    }
}
