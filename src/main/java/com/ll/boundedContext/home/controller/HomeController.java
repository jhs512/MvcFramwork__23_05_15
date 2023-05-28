package com.ll.boundedContext.home.controller;

import com.ll.base.rq.Rq;
import com.ll.framework.annotation.Controller;
import com.ll.framework.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/usr/home/main")
    public void showMain(Rq rq) {
        rq.println("메인 페이지");
    }
}