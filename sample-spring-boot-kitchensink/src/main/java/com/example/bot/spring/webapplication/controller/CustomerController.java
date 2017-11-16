package com.example.bot.spring.webapplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.bot.spring.webapplication.domain.Customer;
import com.example.bot.spring.webapplication.service.CustomerService;


@Controller
class CustomerController {
    @Autowired
    CustomerService customerService;

    @RequestMapping(value = "/customer", method = RequestMethod.GET)
    ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView("customer");
    	try {
			modelAndView.addObject("customers", customerService.getCustomers());
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", "Failed to get customer infos");
		}
        return modelAndView;
    }

    @RequestMapping(value = "/addcustomer")
    ModelAndView addCustomer(@RequestParam(value="name", required=true, defaultValue= "") String name,
    						@RequestParam(value="bootable", required=true, defaultValue= "") String bootableid,
                            @RequestParam(value="adults", required=true, defaultValue= "") Integer adults,
                            @RequestParam(value="children", required=true, defaultValue= "") Integer children,
                            @RequestParam(value="toddler", required=true, defaultValue= "") Integer toddler,
                            @RequestParam(value="phone", required=true, defaultValue= "") String phone,
                            @RequestParam(value="special", required=true, defaultValue= "") String special) throws Exception {

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
        	e.printStackTrace();
        	modelAndView.addObject("message", "Failed to get customer infos");
        }
        return modelAndView;
    }
}
