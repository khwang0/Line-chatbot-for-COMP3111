package com.example.bot.spring;

/**
* Coupon will store the message of inviter/invitee/code/coupon
* @version 1.0
* @since   2017/11/19
*/
public class Coupon{
  private String inviter;
  private String invitee;
  private String code;
  private String coupon;

  /**
  * Consturcts a Coupon with a code wrapped with inviter. Coupon's format declared here.
  * @param inviter A varaible of type String
  * @param code A variable of type String
  */
  public Coupon(String inviter, String code){
    this.inviter = inviter;
    this.code = code;
    coupon = "G8's ICE CREAM COUPON\n ISSUE CODE: "+code;
  }
  /**
  *  Consturcts a Coupon with a code wrapped with inviter and invitee.
  * @param inviter The inviter user id of String tytpe
  * @param invitee The invitee user id of String type
  * @param code Corresponding code of String type
  */
  public Coupon(String inviter, String invitee, String code){
    this(inviter,code);
    this.invitee = invitee;
  }
  /**
  * Retrieve the value of code.
  * @return Corresponding code of String type
  */
  public String getCode(){return code;}
  /**
  * Retrieve the value of inviter.
  * @return The user id of inviter
  */
  public String getInviter(){return inviter;}
  /**
  * Retrieve the value of invitee.
  * @return The user id of invitee
  */
  public String getInvitee(){return invitee;}
  /**
  * Retrieve the coupon which is a String.
  * @return Coupon with proper format
  */
  public String getCoupon(){return coupon + invitee;}
  /**
  * Set the value of invitee.
  * @param invitee The user id of invitee
  */
  public void setInvitee(String invitee){this.invitee = invitee;}
  /**
  * Overrides Object.equals(Object o) with code as its key.
  * @param c Reference of coupon to compare
  * @return Boolean indicating two coupons code are the same
  */
  @Override
  public boolean equals(Object c){return code.equals(((Coupon)c).code);}
  /**
  * Overrides hasCode() given key as code
  * @return An hashCode integer of String type code
  */
  @Override
  public int hashCode(){
    return code.hashCode();
  }
}
