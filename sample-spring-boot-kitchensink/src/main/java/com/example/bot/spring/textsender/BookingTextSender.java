package com.example.bot.spring.textsender;

import java.util.LinkedList;

import com.example.bot.spring.database.BookingDBEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingTextSender implements TextSender {
	
	private BookingDBEngine bookingDB;

	public BookingTextSender() {
		bookingDB = new BookingDBEngine();
	}
	

	@Override
	/** The major processing function
	 * @param userId
	 * @param msg
	 * @return
	 */
	public String process(String userId, String msg) throws Exception {
		bookingDB.openConnection();
		String status = "";
		try {
			status = bookingDB.getStatus(userId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String reply = null;
		switch(status) {
			case "new":{
				if(bookingDB.detectPositive(msg)) {
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
				reply = getInfoQuestion("adult");
				bookingDB.setStatus("adult", userId);
				bookingDB.createNewBooking(userId, msg);
				break;
			}
			
			// Status date: asking for the desired departing date
			case "date":{
				if(bookingDB.detectCancel(msg)) {
					bookingDB.setStatus("default", userId);
					reply = this.getInfoQuestion("cancel");
					break;
				}
				String[] s = msg.split("/");
				if(s.length != 2) {
					reply = this.getInfoQuestion("invalid date");
					break;
				}
				int dd,mm;
				try {
					dd = Integer.parseInt(s[0]);
					mm = Integer.parseInt(s[1]);
				}catch(NumberFormatException e){
					reply = this.getInfoQuestion("invalid date");
					break;
				}
				
				boolean valid = false;
				try {
					valid = bookingDB.checkValidDate(dd, mm, userId);
				}catch(Exception e) {
					if(e.getMessage().equals("FULL")) {
						reply = this.getInfoQuestion("fulltour");
						break;
					}else if(e.getMessage().equals("NO SUCH DATE")){
						reply = this.getInfoQuestion("no such date");
						break;
					}else if(e.getMessage().equals("OCCUPIED")) {
						reply = this.getInfoQuestion("occupied");
						bookingDB.setStatus("hold", userId);
						bookingDB.recordDate(userId, dd, mm);
						break;
					}else if(e.getMessage().equals("REBOOK")) {
						reply = this.getInfoQuestion("no rebook");
						break;
					}
				}
				if(valid) {
					bookingDB.recordDate(userId,dd,mm);
					String name = bookingDB.getName(userId);
					if(name.equals("")) {
						reply = getInfoQuestion("name");
						bookingDB.setStatus("name", userId);
					}else {
						bookingDB.createNewBooking(userId, name);
						bookingDB.setStatus("adult", userId);
						reply = this.getInfoQuestion("adult");
					}
				}
				break;
			}
			
			// Status adult; asking for number of adults
			case "adult":{
				if(bookingDB.detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					if(i<0) {
						reply = this.getInfoQuestion("invalid adult");
						break;
					}
					bookingDB.recordAdults(userId,i);
				}catch(NumberFormatException e) {
					reply = this.getInfoQuestion("invalid adult");;
					break;
				}
				reply = getInfoQuestion("children");
				bookingDB.setStatus("children",userId);
				break;
			}
			
			// Status children: asking for number of children (Age 4 to 11)
			case "children":{
				if(bookingDB.detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					if(i<0) {
						reply = this.getInfoQuestion("invalid children");
						break;
					}
					bookingDB.recordChildren(userId,i);
				}catch(Exception e) {
					reply = this.getInfoQuestion("invalid children");
					break;
				}
				reply = getInfoQuestion("toddler");
				bookingDB.setStatus("toddler",userId);
				break;
			}
			
			//Status toddler: asking for number of children (Age 0 to 3)
			case "toddler":{
				if(bookingDB.detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					int i = Integer.parseInt(msg);
					if(i<0) {
						reply = this.getInfoQuestion("invalid toddler");
						break;
					}
					bookingDB.recordToddler(userId,i);
				}catch(Exception e) {
					reply = this.getInfoQuestion("invalid toddler");
					break;
				}
				reply = getInfoQuestion("phone");
				bookingDB.setStatus("phone",userId);
				break;
			}
			
			//Status phone: asking for phone number
			case "phone" :{
				if(bookingDB.detectCancel(msg)) {
					this.stopCurrentBooking(userId);
					break;
				}
				try {
					long i = Long.parseLong(msg);
					bookingDB.recordPhone(userId,i);
				}catch(Exception e) {
					reply = this.getInfoQuestion("invalid phone");
					break;
				}
				reply = getTotalPrice(userId);
				break;
			}
			
			// Status confirm: last step before everything finished
			case "confirm" :{
				if(bookingDB.detectPositive(msg)) {
					bookingDB.setStatus("default",userId);
					bookingDB.updateRegisteredNumber(userId);
					reply = this.getInfoQuestion("confirm");
				}else {
					reply = stopCurrentBooking(userId);
				}
				break;
			}
			case "hold" :{
				if(bookingDB.detectPositive(msg)) {
					String name = bookingDB.getName(userId);
					if(name.equals("")) {
						bookingDB.setStatus("name", userId);
						reply = this.getInfoQuestion("name");
					}else {
						bookingDB.createNewBooking(userId, name);
						bookingDB.setStatus("adult", userId);
						reply = this.getInfoQuestion("adult");
					}
				}else {
					bookingDB.setStatus("default",userId);
					reply = defaultCaseHandler(userId,msg);
				}
				break;
			}
			default:{
				reply = defaultCaseHandler(userId,msg);
			}
		}
		bookingDB.close();
		if(reply.equals(null)) {
			throw new Exception("CANNOT ANSWER");
		}
		return reply;
	}

	/** stop the current booking request of one user
	 * 
	 * @param userId
	 * @return
	 */
	private String stopCurrentBooking(String userId) {
		bookingDB.setStatus("default",userId);
		bookingDB.removeBooking(userId);
		return this.getInfoQuestion("cancel");
	}

	/** handle a question if the user is not in the booking flow
	 * 
	 * @param userId
	 * @param msg
	 * @return
	 */
	private String defaultCaseHandler(String userId, String msg) {
		// If he specifies the tour ID
		LinkedList<String> tourIds = bookingDB.getAllTourIds();
		String reply = null;
		for(int i = 0; i < tourIds.size(); i++) {
			if(msg.toLowerCase().contains(tourIds.get(i).toLowerCase())) {
				reply = this.getConfirmation(userId, tourIds.get(i));
				return reply;
			}
		}
		
		/*
		// If he specifies the number of the tour
		String[] candiTours = bookingDB.getTourIds(userId);
		String[] orders = {"first", "second", "third", "fourth", "fifth", "seventh", "eighth", "ninth", "tenth", "eleventh"};
		String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
		String[] engNumbers = {"one", "two", "three", "four", "five", "six", "seven" ,"eight", "nine", "ten", "eleven"};
		String[] s = msg.split("\\s");
		for(int i = 0; i < s.length; i++) {
			for(int j = 0; j < candiTours.length; j++) {
				if(s[i].toLowerCase().equals(orders[j])) {
					reply = this.getConfirmation(userId, tourIds.get(j));
					return reply;
				}else if(s[i].toLowerCase().equals(numbers[j])) {
					reply = this.getConfirmation(userId, tourIds.get(j));
					return reply;
				}else if(s[i].toLowerCase().equals(engNumbers[j])) {
					reply = this.getConfirmation(userId, tourIds.get(j));
					return reply;
				}
			}
		}
		*/
		
		// If he specifies the name of the tour
		LinkedList<String> tourNames = bookingDB.getAllTourNames();
		for(int i = 0; i < tourNames.size(); i++) {
			String[] t = tourNames.get(i).split("\\s|-");
			for(int j = 0; j < t.length; j++) {
				if(!msg.toLowerCase().contains(t[j].toLowerCase())) {
					break;
				}else if(j == t.length-1) {
					String tourId = bookingDB.findTourId(tourNames.get(i));
					reply = this.getConfirmation(userId, tourId);
					return reply;
				}
			}
		}
		
		if(bookingDB.detectAsk(msg)) {
			reply = this.getInfoQuestion("asking");
			return reply;
		}
		if(bookingDB.detectCancel(msg))
			reply = this.getInfoQuestion("cancel");
		return reply;
	}

	/** calculate the total price of the current booking request for one user
	 * 
	 * @param userId
	 * @return
	 */
	private String getTotalPrice(String userId) {
		int adult = bookingDB.getAdult(userId);
		int toodler = bookingDB.getToddler(userId);
		int children = bookingDB.getChildren(userId);
		String tourId = bookingDB.getTourIds(userId)[0];
		int quota = bookingDB.getQuota(tourId);
		if(quota < (adult+toodler+children)) {
			String reply = String.format(this.getInfoQuestion("no quota"), quota, tourId);
			bookingDB.setStatus("default",userId);
			bookingDB.removeBooking(userId);
			return reply;
		}else {
			double price = bookingDB.getPrice(tourId);
			double totalPrice = price*adult+price*0.8*children;
			String reply = String.format(this.getInfoQuestion("price"), totalPrice);
			bookingDB.recordTotalPrice(totalPrice,userId);
			bookingDB.setStatus("confirm",userId);
			return reply;
		}
	}
	
	/** Get the confirmation message of one tour before start a booking request
	 * 
	 * @param tourId
	 * @return
	 */
	private String getConfirmation(String userId, String tourId) {
		LinkedList<String> tourInfo = bookingDB.getTourInfos(tourId);
		String dates = bookingDB.getAllDates(tourId);
		StringBuilder sb = new StringBuilder();
		String[] allDates = dates.split(",");
		for(int i = 0; i < allDates.length; i++) {
			if(i == allDates.length-1 && i != 0)
				sb.append(" and ");
			else if(i != 0)
				sb.append(", ");
			sb.append(allDates[i].substring(2)+"/"+allDates[i].substring(0,2));
		}
		String dateSeg = sb.toString();
		String reply = this.getInfoQuestion("allinfo");
		bookingDB.setTourid(userId, tourId);
		bookingDB.setStatus("new", userId);
		return String.format(reply, tourId, tourInfo.get(0), tourInfo.get(1), 
				dateSeg, tourInfo.get(2), tourInfo.get(3));
	}
	/*

	*/


	/** Return next question according to tag
	 * 
	 * @param nextQ
	 * @return
	 */
	private String getInfoQuestion(String nextQ) {
		String reply = bookingDB.getReplyMessage(nextQ);
		return reply;
	}
	
}
