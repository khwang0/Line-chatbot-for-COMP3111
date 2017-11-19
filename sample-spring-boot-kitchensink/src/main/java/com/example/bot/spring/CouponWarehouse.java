package com.example.bot.spring;

import java.util.*;


public class CouponWarehouse{
  final private static int numOfCoupons = 5000;
  private static ArrayList<String> codes = new ArrayList<String>(numOfCoupons);
  private static CouponWarehouse couponWarehouse = new CouponWarehouse();
  private static int couponsRemaining;
  private static ArrayList<Users> users;

  private CouponWarehouse(){
    couponsRemaining = numOfCoupons;
    for(int i = 0; i < numOfCoupons; i++) {
    	String code = "";
    	do
      {
    		for(int j = 0; j < 6 ; j++) {
    			Random rand = new Random();
    			int rn = rand.nextInt(3) + 1;
    			int n = 0;
    			switch(rn) {
    				case 1:n = rand.nextInt(10)+48; break;
    				case 2:n = rand.nextInt(26)+65; break;
    				case 3:n = rand.nextInt(26)+97; break;
    			}
    			code += (char)n;
        }
        System.out.println("");
    	}while(codes.contains(code));

      codes.add(code);
    }
  }
  static public CouponWarehouse getInstance(){
    return couponWarehouse;
  }
  static public MsgAttachedData<Date> startCampaign(){
    String msg = "Campaign has been started!\n "
    +"Each current user can type \"friend\" into the chatbot and the chatbot will reply them a 6-digits unique code. "
    + "The user can give this code to his friend and recommend them to add the chatbot as their line friend. "
    + "The new user then type \"code\" into the chatbot followed by the 6-digits unique code, then the chatbot will send an ice-cream store electronic coupon to both of the new user and the old user. "
    + "Each new user can claim the coupon once only. Each user can recommend infinite number of friends. "
    + "Each new user can also recommend new users. Users who registered before the campaign cannot type \"code\" to get the coupon. "
    + "After 5000 copies of ice-cream coupon were given out, the campaign stops.";
    Date now = new Date();
    return new MsgAttachedData<Date>(msg,now);
  }

  public void register(Users obj) {
  		if ( !users.contains(obj) ) users.add(obj);
  }

  public void unregister(Users obj) {
  		users.remove(obj);
  }

  public MsgAttachedData<ArrayList<Users>> NotifyObservers() {
      for(Users u : users) u.update(couponsRemaining);
      String msg = "Someone has invited their firends and got coupon!\n"
           +"We have " + Integer.toString(couponsRemaining) + " coupons left!\n"
           +"Go invite friends and enjoy ice creams!";
   		return new MsgAttachedData<ArrayList<Users>>(msg,users);
  }

  public String issueCode() {
	  Random rand = new Random();
	  int n = rand.nextInt(codes.size());
	  assert(!(n >= 0 && n < codes.size()));
	  String code = codes.get(n);
    return code;
  }
  public String issueCoupon(){
    if (isCouponRemaining())
      couponsRemaining--;
    return "This is your ice cream coupon(for 2 perons) :)";
  }
  public boolean isCodeValid(String code){
    return codes.contains(code);
  }
  public boolean isCouponRemaining(){
    return (couponsRemaining > 0);
  }

}
