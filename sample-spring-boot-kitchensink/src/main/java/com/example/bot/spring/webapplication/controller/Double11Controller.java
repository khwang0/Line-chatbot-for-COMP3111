package com.example.bot.spring.webapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.webapplication.domain.Activity;
import com.example.bot.spring.webapplication.service.Double11Service;

@Controller
class Double11Controller {
	
	@Autowired
	Double11Service double11Service;
	
    @RequestMapping(value = "/double11", method = RequestMethod.GET)
    ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView("double11");
    	try {
			modelAndView.addObject("activities", double11Service.getAllActivities());
			modelAndView.addObject("tours", double11Service.getAllTours());
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", "Failed to get activity infos");
		}
        return modelAndView;
    }
	
	@RequestMapping(value="/addDouble11Activity", method=RequestMethod.POST)
	ModelAndView addDouble11Activity(@RequestParam(value="bootableid", required=true, defaultValue= "") String bootableid,
			@RequestParam(value="quota", required=true, defaultValue= "") Integer quota) {
		ModelAndView modelAndView = new ModelAndView("double11");
		try {
			Activity activity = new Activity();
			activity.setBootableId(bootableid);
			activity.setQuota(quota);
			double11Service.createNewActivity(activity);
		}catch(Exception e) {
			modelAndView.addObject("message", "Failed to create new activity");
		}
    	try {
			modelAndView.addObject("activities", double11Service.getAllActivities());
			modelAndView.addObject("tours", double11Service.getAllTours());
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", "Failed to get activity infos");
		}
		return modelAndView;
	}	
}
