package com.example.bot.spring.webapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.webapplication.service.UQService;

@Controller
class UQController {
	
	@Autowired
	UQService uqService;
	
    @RequestMapping(value = "/UQ", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView("UQ");
    	try {
			modelAndView.addObject("uqs", uqService.getAllUQs());
		} catch (Exception e) {
        	modelAndView.addObject("uqs", null);
			e.printStackTrace();
		}
        return modelAndView;
    }
	
    @RequestMapping(value = "/answerUQ")
    ModelAndView answerQuestion(@RequestParam(value="question", required=true, defaultValue= "") String question,
    						@RequestParam(value="id", required=true, defaultValue= "") String id,
    						@RequestParam(value="answer", required=true, defaultValue= "") String answer) throws Exception {
        ModelAndView modelAndView = new ModelAndView("UQ");
        try {
        	uqService.answerUQ(question,id,answer);
        	modelAndView.addObject("uqs", uqService.getAllUQs());
        }catch(Exception e) {
        	modelAndView.addObject("uqs", null);
        	e.printStackTrace();
        }
        return modelAndView;
    }
    
    
}
