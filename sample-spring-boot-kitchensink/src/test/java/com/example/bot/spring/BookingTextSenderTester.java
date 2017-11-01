package com.example.bot.spring;

import java.io.IOException;
import java.util.Scanner;

import com.example.bot.spring.textsender.BookingTextSender;

public class BookingTextSenderTester {
	
	private static final String userId = "123456";

	public BookingTextSenderTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		BookingTextSender ts = new BookingTextSender();
		Scanner scanner = new Scanner(System.in);
		while(true) {
			String input = scanner.nextLine();
			System.out.println("#########################");
			if(input.equals("quit"))
				break;
			try {
				System.out.println(ts.process(userId, input));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Sorry, we cannot answer your question.");
			}
		}
		scanner.close();
	}

}
