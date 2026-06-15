package com.doan.VitaStore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/index"})
    public String dashboard() {
        return "admin/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/Admin_logon";
    }

    @GetMapping("/user/list")
    public String userList() {
        return "admin/user/list";
    }

    @GetMapping("/product/list")
    public String productList() {
        return "admin/product/list";
    }

    @GetMapping("/product/category")
    public String categoryList() {
        return "admin/product/category";
    }

    @GetMapping("/product/add")
    public String productAdd() {
        return "admin/product/add";
    }

    @GetMapping("/order/list")
    public String orderList() {
        return "admin/order/list";
    }

    @GetMapping("/order/detail")
    public String orderDetail() {
        return "admin/order/detail";
    }
}
