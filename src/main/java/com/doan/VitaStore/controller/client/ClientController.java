package com.doan.VitaStore.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ClientController {
    @RequestMapping("/home")
    public String client() {
        return "client/index";
    }
}