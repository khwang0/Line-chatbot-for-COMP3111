package com.example.bot.spring.webapplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HelloServlet", urlPatterns = {"hello"}, loadOnStartup = 1) 
public class HelloServlet extends HttpServlet {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 592132091471231308L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    	throws ServletException, IOException {
        response.getWriter().print("Hello, World!");  
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    	throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null) name = "World";
        request.setAttribute("user", name);
        request.getRequestDispatcher("response.jsp").forward(request, response); 
    }

}
