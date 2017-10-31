package com.example.bot.spring.textsender;

import java.util.LinkedList;

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
	/** The major processing function
	 * @param userId
	 * @param msg
	 * @return
	 */
	public String process(String userId, String msg) throws Exception {
		bookingDB.openConnection();
		String status = null;
		try {
			status = bookingDB.getStatus(userId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String reply = null;
		switch(status) {
			case "new":{

				if(msg.toLowerCase().contains("yes")||msg.toLowerCase().contains("yeah")||
						msg.toLowerCase().contains("good")) {
					bookingDB.setStatus("date",userId);
					reply = getInfoQuestion("date");
					// >>>>>>>>>>>>> should it be getInfoQuestion("name");?
					// bookingDB.setStatus("name",userId);
				}else {
					bookingDB.setStatus("default",userId);
					reply = defaultCaseHandler(userId,msg);
				}
				break;
			}
			
			// Status name: asking for user's actual name
			case "name":{
				bookingDB.createNewBooking(userId, msg);
				// >>>>>>>>>>>>> reply = getInfoQuestion("date");?
				// >>>>>>>>>>>>> bookingDB.setStatus("date",userId);
				break;
			}
			
			// Status date: asking for the desired departing date
			case "date":{
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
				String tourId = bookingDB.getTourIds(userId)[0]; // >>>>>>>>>>>>>> not necessarily the first one; 
				String dates = bookingDB.getAllDates(tourId);
				String date = Integer.toString(mm)+Integer.toString(dd);
				if(dates.contains(date)) {
					bookingDB.recordDate(userId,dd,mm);
					reply = getInfoQuestion("adult");
					bookingDB.setStatus("adult", userId);
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
				reply = getInfoQuestion("children");
				bookingDB.setStatus("children",userId);
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
				reply = getInfoQuestion("toddler");
				bookingDB.setStatus("toddler",userId);
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
				reply = getInfoQuestion("phone");
				bookingDB.setStatus("phone",userId);
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
				reply = getTotalPrice(userId);
				break;
			}
			
			// Status confirm: last step before everything finished
			case "confirm" :{
				if(msg.toLowerCase().contains("yes")||msg.toLowerCase().contains("yeah")
						||msg.toLowerCase().contains("good")) {
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
				//log.info("Illegal status for user id: {}. ", userId);
				reply = defaultCaseHandler(userId,msg);
			}
		}
		bookingDB.close();
		if(reply.equals(null)) {
			throw new Exception("CANNOT ANSWER");
		}
		return reply;
	}
	

	/** determine if a message has the potential to cancel
	 * 
	 * @param msg
	 * @return
	 */
	private boolean detectCancel(String msg) {
		String[] negativeFlags = {"don't", "cancel", "stop", "remove", "quit", "sorry"};
		for(int i = 1; i < negativeFlags.length; i++) {
			if(msg.toLowerCase().contains(negativeFlags[i])) {
				return true;
			}
		}
		return false;
	}

	/** stop the current booking request of one user
	 * 
	 * @param userId
	 * @return
	 */
	private String stopCurrentBooking(String userId) {
		bookingDB.setStatus("default",userId);
		bookingDB.removeBooking(userId);
		return "You just stopped your booking request. Please start a new booking request if you want.";
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
			if(msg.contains(tourIds.get(i))) {
				reply = this.getConfirmation(userId, tourIds.get(i));
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
		
		// If he specifies the name of the tour
		LinkedList<String> tourNames = bookingDB.getAllTourNames();
		for(int i = 0; i < tourNames.size(); i++) {
			String[] t = tourNames.get(i).split("\\s|-");
			for(int j = 0; j < t.length; j++) {
				if(!msg.contains(t[j])) {
					break;
				}else if(j == t.length-1) {
					String tourId = bookingDB.findTourId(tourNames.get(i));
					reply = this.getConfirmation(userId, tourId);
				}
			}
		}
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
	
	/** Get the confirmation message of one tour before start a booking request
	 * 
	 * @param tourId
	 * @return
	 */
	private String getConfirmation(String userId, String tourId) {
		LinkedList<String> tourInfo = bookingDB.getTourInfos(tourId);
		String dates = bookingDB.getAllDates(tourId);
		StringBuilder sb = new StringBuilder();
		sb.append(tourId+" "+tourInfo.get(0));
		sb.append(":"+tourInfo.get(1)+".\n");
		sb.append("We have tours on ");
		String[] allDates = dates.split(",");
		for(int i = 0; i < allDates.length; i++) {
			if(i == allDates.length-1 && i != 0)
				sb.append(" and ");
			else if(i != 0)
				sb.append(", ");
			sb.append(allDates[i].substring(2)+"//"+allDates[i].substring(0,2));
		}
		sb.append(" still available for booking.\n");
		sb.append("The fee for this tour is: Weekday: ");
		sb.append(tourInfo.get(2));
		sb.append(" / Weekend: ");
		sb.append(tourInfo.get(3)).append("\n");
		sb.append("Do you want to book this one?");
		bookingDB.setTourid(userId, tourId);
		return sb.toString();
	}


	/** Return next question according to the current status
	 * 
	 * @param nextQ
	 * @return
	 */
	private String getInfoQuestion(String nextQ) {
		switch(nextQ) {
			case "name": {
				return "Your name please (Firstname LASTNAME)";
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
