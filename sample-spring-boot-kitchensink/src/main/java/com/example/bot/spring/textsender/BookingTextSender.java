package com.example.bot.spring.textsender;

import com.example.bot.spring.database.BookingDBEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingTextSender implements TextSender {
	
	private BookingDBEngine bookingDB;

	public BookingTextSender() {
		// TODO Auto-generated constructor stub
		bookingDB = new BookingDBEngine();
	}
	
	@Override
	public String process(String userId, String msg) {
		bookingDB.openConnection();
		String status = bookingDB.getStatus(userId);
		String reply = null;
		switch(status) {
			case "new":{
				if(msg.toLowerCase().contains("yes")||msg.toLowerCase().contains("yea")) {
					bookingDB.setStatus("date",userId);
					reply = getInfoQuestion("date");
				}else {
					bookingDB.setStatus("default",userId);
					reply = defaultCaseHandler(userId,msg);
				}
				break;
			}
			
			// Status name: asking for user's actual name
			case "name":{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				bookingDB.createNewBooking(userId, msg);
				break;
			}
			
			// Status date: asking for the desired departing date
			case "date":{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				String[] s = msg.split("/");
				if(s.length != 2) {
					reply = "Invalid date format. Please enter in (DD/MM) format.";
					break;
				}
				int dd,mm;
				try {
					dd = Integer.parseInt(s[0]);
					mm = Integer.parseInt(s[1]);
				}catch(NumberFormatException e){
					reply = "Invalid date format. Please enter in (DD/MM) format.";
					break;
				}
				boolean valid = bookingDB.checkValidDate(dd,mm,userId);
				if(valid) {
					bookingDB.recordDate(userId,dd,mm);
					String nextQ = bookingDB.findNextEmptyInfo(userId);
					reply = getInfoQuestion(nextQ);
					bookingDB.setStatus(nextQ, userId);
				}else {
					reply = "Invalid date. Please enter a valid date.";
				}
				break;
			}
			
			// Status adult; asking for number of adults
			case "adult":{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					bookingDB.recordAdults(userId,i);
				}catch(NumberFormatException e) {
					reply = "Invalid number of adults. Please enter again.";
					break;
				}
				String nextQ = bookingDB.findNextEmptyInfo(userId);
				if(nextQ == null) {
					reply = getTotalPrice(userId);
				}else {
					reply = getInfoQuestion(nextQ);
					bookingDB.setStatus(nextQ,userId);
				}
				break;
			}
			
			// Status children: asking for number of children (Age 4 to 11)
			case "children":{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					bookingDB.recordChildren(userId,i);
				}catch(Exception e) {
					reply = "Invalid number of children (Age 4 to 11). Please enter again.";
					break;
				}
				String nextQ = bookingDB.findNextEmptyInfo(userId);
				if(nextQ == null) {
					reply = getConfirmation(userId);
				}else {
					reply = getInfoQuestion(nextQ);
					bookingDB.setStatus(nextQ,userId);
				}
				break;
			}
			
			//Status toddler: asking for number of children (Age 0 to 3)
			case "toddler":{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					bookingDB.recordToddler(userId,i);
				}catch(Exception e) {
					reply = "Invalid number of children (Age 0 to 3). Please enter again.";
					break;
				}
				String nextQ = bookingDB.findNextEmptyInfo(userId);
				if(nextQ == null) {
					reply = getTotalPrice(userId);
				}else {
					reply = getInfoQuestion(nextQ);
					bookingDB.setStatus(nextQ,userId);
				}
				break;
			}
			
			//Status phone: asking for phone number
			case "phone" :{
				if(detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					bookingDB.recordPhone(userId,i);
				}catch(Exception e) {
					reply = "Invalid phone number. Please enter again.";
					break;
				}
				String nextQ = bookingDB.findNextEmptyInfo(userId);
				if(nextQ == null) {
					reply = getTotalPrice(userId);
				}else {
					reply = getInfoQuestion(nextQ);
					bookingDB.setStatus(nextQ,userId);
				}
				break;
			}
			
			// Status confirm: last step before everything finished
			case "confirm" :{
				if(msg.toLowerCase().contains("yes")||msg.toLowerCase().contains("yea")) {
					bookingDB.setStatus("default",userId);
					reply = "Thank you. Please pay the tour fee by ATM to "
							+ "123-345-432-211 of ABC Bank or by cash in our store.\n"
							+ "When you complete the ATM payment, please send the bank "
							+ "in slip to us. Our staff will validate it.";
				}else {
					reply = stopCurrentBooking(userId);
				}
				break;
			}
			case "default":{
				reply = defaultCaseHandler(userId,msg);
				break;
			}
			default:{
				log.info("Illegal status for user id: {}. ", userId);
				reply = defaultCaseHandler(userId,msg);
			}
		}
		bookingDB.close();
		return reply;
	}
	

	private boolean detectCancel(String msg) {
		// TODO Auto-generated method stub
		String[] negativeFlags = {"don't", "cancel", "stop", "remove", "quit", "sorry"};
		for(int i = 1; i < negativeFlags.length; i++) {
			if(msg.toLowerCase().contains(negativeFlags[i])) {
				return true;
			}
		}
		return false;
	}

	private String stopCurrentBooking(String userId) {
		// TODO Auto-generated method stub
		bookingDB.setStatus("default",userId);
		bookingDB.removeBooking(userId);
		return "You just stopped your booking request. Please start a new booking request if you want.";
	}

	private String defaultCaseHandler(String userId, String msg) {
		// TODO Auto-generated method stub
		// If he specifies the tour ID
		String[] tourIds = bookingDB.getAllTourIds();
		String reply = null;
		for(int i = 0; i < tourIds.length; i++) {
			if(msg.contains(tourIds[i])) {
				reply = this.getConfirmation(tourIds[i]);
				return reply;
			}
		}
		// If he specifies the number of the tour
		String[] candiTours = bookingDB.getTourIds(userId);
		String[] orders = {"first", "second", "third", "fourth", "fifth", "seventh", "eighth", "ninth", "tenth", "eleventh"};
		String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
		String[] engNumbers = {"one", "two", "three", "four", "five", "six", "seven" ,"eight", "nine", "ten", "eleven"};
		String[] s = msg.split("\\s");
		for(int i = 0; i < s.length; i++) {
			for(int j = 0; j < candiTours.length; j++) {
				if(s[i].toLowerCase().equals(orders[j])) {
					reply = this.getConfirmation(tourIds[j]);
					return reply;
				}else if(s[i].toLowerCase().equals(numbers[j])) {
					reply = this.getConfirmation(tourIds[j]);
					return reply;
				}else if(s[i].toLowerCase().equals(engNumbers[j])) {
					reply = this.getConfirmation(tourIds[j]);
					return reply;
				}
			}
		}
		
		// If he specifies the name of the tour
		String[] tourNames = bookingDB.getAllTourNames();
		for(int i = 0; i < tourNames.length; i++) {
			String[] t = tourNames[i].split("\\s|-");
			for(int j = 0; j < t.length; j++) {
				if(!msg.contains(t[j])) {
					break;
				}else if(j == t.length-1) {
					String tourId = bookingDB.findTourId(tourNames[i]);
					reply = this.getConfirmation(tourId);
				}
			}
		}
		return reply;
	}

	private String getTotalPrice(String userId) {
		// TODO Auto-generated method stub
		int adult = bookingDB.getAdult(userId);
		int toodler = bookingDB.getToddler(userId);
		int children = bookingDB.getChildren(userId);
		String tourId = bookingDB.getTourJoined(userId);
		int quota = bookingDB.getQuota(tourId);
		if(quota < (adult+toodler+children)) {
			String reply = String.format("We only have a quota of %d left for tour %s. "
					+ "Please change to another tour. Sorry", quota, tourId);
			bookingDB.setStatus("default",userId);
			bookingDB.removeBooking(userId);
			return reply;
		}else {
			double price = bookingDB.getPrice(tourId);
			double totalPrice = price*adult+price*0.8*children;
			String reply = String.format("Total price is %f, confirm? "
					+ "Please notice that there will be no refund for "
					+ "cancellation due to personal reasons.", totalPrice);
			bookingDB.setStatus("confirm",userId);
			return reply;
		}
	}
	
	private String getConfirmation(String tourId) {
		// TODO Auto-generated method stub
		return null;
	}


	private String getInfoQuestion(String nextQ) {
		// TODO Auto-generated method stub
		switch(nextQ) {
			case "name": {
				return "Your name please (Fistname LASTNAME)";
			}
			case "date": {
				return "On which date you are going? (in DD/MM format)";
			}
			case "adult":{
				return "How many adults?";
			}
			case "children":{
				return "How many children (Age 4 to 11)?";
			}
			case "toddler":{
				return "How many children (Age 0 to 3)?";
			}
			case "phone":{
				return "Your phone number please.";
			}
		}
		return null;
	}

}
