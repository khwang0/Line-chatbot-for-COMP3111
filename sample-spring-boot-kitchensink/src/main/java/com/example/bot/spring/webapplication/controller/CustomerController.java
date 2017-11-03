package com.example.bot.spring.webapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.webapplication.domain.Customer;
import com.example.bot.spring.webapplication.service.CustomerService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Controller
@RequestMapping("/")
class CustomerController {
    final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CustomerService customerService;

    @RequestMapping(value = "getcustomer", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView("customer");
    	try {
			modelAndView.addObject("customers", customerService.getCustomers());
		} catch (Exception e) {
			modelAndView.addObject("message", "Failed to get customer infos");
		}
        return modelAndView;
    }

    @RequestMapping(value = "addcustomer", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    ModelAndView addCustomer(@RequestParam String name,
    						@RequestParam String bootableid,
                            @RequestParam Integer adults,
                            @RequestParam Integer children,
                            @RequestParam Integer toddler,
                            @RequestParam String phone,
                            @RequestParam String special) throws Exception {

        ModelAndView modelAndView = new ModelAndView("customer");
        try {
            Customer customer = new Customer();
            customer.setBootableId(bootableid);
            customer.setName(name);
            customer.setAdults(adults);
            customer.setToddler(toddler);
            customer.setChildren(children);
            customer.setSpecial(special);
            customer = customerService.addCustomer(customer);
            modelAndView.addObject("message", "customer added with name: " + customer.getName());
        }
        catch (Exception ex){
            modelAndView.addObject("message", "Failed to add customer: " + ex.getMessage());
        }
        try {
        modelAndView.addObject("customers", customerService.getCustomers());
        }catch(Exception e) {
        	modelAndView.addObject("message", "Failed to get customer infos");
        }
        return modelAndView;
    }
}
