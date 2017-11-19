package com.example.bot.spring;

public class Coupon{
  private String inviter;
  private String invitee;
  private String code;
  private String coupon;
  public Coupon(String inviter, String code){
    this.inviter = inviter;
    this.code = code;
    coupon = "~This is your ice cream coupon~\n ISSUE CODE: "+code;
  }
  public Coupon(String inviter, String invitee, String code){
    this(inviter,code);
    this.invitee = invitee;
  }
  public String getCode(){return code;}
  public String getInviter(){return inviter;}
  public String getInvitee(){return invitee;}
  public String getCoupon(){return coupon;}
  public void setInvitee(String invitee){this.invitee = invitee;}
  @Override
  public boolean equals(Object c){return code.equals(((Coupon)c).code);}
  @Override
  public int hashCode(){
    return code.hashCode();
  }
}
