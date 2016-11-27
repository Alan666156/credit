package com.credit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fuhongxing on 16/11/27.
 */
@Controller
public class IndexController {
    public static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    @RequestMapping("/")
    public String index(Model model){
        LOGGER.info("index");
        return "index";
    }
}
