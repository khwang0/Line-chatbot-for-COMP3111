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
        	modelAndView.addObject("tours",customerService.getAllTours());
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("message", "Failed to get customer infos");
		}
        return modelAndView;
    }

    @RequestMapping(value = "/addcustomer", method = RequestMethod.POST)
    ModelAndView addCustomer(@RequestParam(value="name", required=true, defaultValue= "") String name,
    						@RequestParam(value="bootableid", required=true, defaultValue= "") String bootableid,
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
            customer.setPhone(phone);
            customer = customerService.addCustomer(customer);
            modelAndView.addObject("message", "customer added with name: " + customer.getName());
        }
        catch (Exception e){
        	e.printStackTrace();
            modelAndView.addObject("message", "Failed to add customer: " + e.getMessage());
        }
        try {
        	modelAndView.addObject("customers", customerService.getCustomers());
        	modelAndView.addObject("tours",customerService.getAllTours());
        }catch(Exception e) {
        	e.printStackTrace();
        	modelAndView.addObject("message", "Failed to get customer infos");
        }
        return modelAndView;
    }
    
    @RequestMapping(value = "/updatePayment", method = RequestMethod.POST)
    ModelAndView updatePayment(@RequestParam(value="", required=true, defaultValue= "") String name,
			@RequestParam(value="bootableid", required=true, defaultValue= "") String bootableid,
            @RequestParam(value="payment", required=true, defaultValue= "") Double payment,
            @RequestParam(value="pricePaid", required=true, defaultValue= "") Double pricePaid) {
    	ModelAndView modelAndView = new ModelAndView("customer");
    	try {
    		Customer customer = new Customer();
    		customer.setName(name);
    		customer.setBootableId(bootableid);
    		customer.setPricePaid(pricePaid+payment);
    		customerService.updatePayment(customer);
    	}catch(Exception e) {
    		modelAndView.addObject("message", "Failed update payment information.");
    	}
        try {
        	modelAndView.addObject("customers", customerService.getCustomers());
        	modelAndView.addObject("tours",customerService.getAllTours());
        }catch(Exception e) {
        	e.printStackTrace();
        	modelAndView.addObject("message", "Failed to get customer infos.");
        }
        return modelAndView;
    }
}
