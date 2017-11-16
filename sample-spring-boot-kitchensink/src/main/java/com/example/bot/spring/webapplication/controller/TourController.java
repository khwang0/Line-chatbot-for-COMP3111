package com.example.bot.spring.webapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.webapplication.service.TourService;

@Controller
class TourController {
    @Autowired
    TourService tourService;

    @RequestMapping(value = "/tour", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView("tour");
    	try {
			modelAndView.addObject("tours", tourService.getTours());
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", "Failed to get tour infos");
		}
        return modelAndView;
    }
}
