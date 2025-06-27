package com.merveartut.task_manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = { "/", "/{path:^(?!api|auth|static).*}/**" })
    public String forwardReactRoutes() {
        return "forward:/index.html";
    }
}