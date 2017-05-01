package com.masters.moscowopen.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * @author Georgii Ovsiannikov
 * @since 5/1/17
 */
@Controller
public class MapController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView showMainPage() throws IOException {
        return new ModelAndView("main");
    }

    @RequestMapping(value = "/env_map", method = RequestMethod.GET)
    public ModelAndView showMap() throws IOException {
        return new ModelAndView("map");
    }
}
