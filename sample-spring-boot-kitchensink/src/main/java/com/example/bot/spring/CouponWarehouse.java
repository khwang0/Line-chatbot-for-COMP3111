package com.example.bot.spring;

import java.util.*;

import java.io.IOException;

import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.Date;
import java.util.TimeZone;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
public class CouponWarehouse{
  final private static int NUMOFCOUPONS = 5000;
  final private static int NUMOFCODES = 50000;
  private static ArrayList<String> existingUids;
  private static ArrayList<String> gotCouponNewUsers = new ArrayList<String>();
  private static ArrayList<String> newUids = new ArrayList<String>();
  private static ArrayList<String> codes = new ArrayList<String>();
  private static ArrayList<Coupon> coupons = new ArrayList<Coupon>();
  private static int couponsRemaining = NUMOFCOUPONS;
  private static boolean started = false;
  private static ArrayList<String> uids;
  private static CouponWarehouse couponWarehouse = new CouponWarehouse();


  // Generate 6-digit random codes(A-Z, a-z, 0-9 including) and store.
  private static void generateRandomCode(){
    for(int i = 0; i < NUMOFCODES; i++) {
      String code = "";
      do{
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
      }while(codes.contains(code));
      codes.add(code);
    }
  }

  //Fetch user ids from the database and store.
  private static void fetchUsers(){
    SQLDatabaseEngine db = new SQLDatabaseEngine();
    existingUids = db.fetchUIDs();
  }

  // Construct a unique object of CouponWarehouse.
  private CouponWarehouse(){
    generateRandomCode();
    fetchUsers();
  }
  /**
  * Retrieve the unique CouponWarehouse Object.
  * @return An object of couponWarehouse type
  */
  static public CouponWarehouse getInstance(){
    return couponWarehouse;
  }
  /**
  * Starts the campaign base on current time.
  * @return Starting message about campaign
  */
  static public String startCampaign(){
    String msg = "Campaign has been started!\n "
    +"Each current user can type \"friend\" into the chatbot and the chatbot will reply them a 6-digits unique code. "
    + "The user can give this code to his friend and recommend them to add the chatbot as their line friend. "
    + "The new user then type \"code\" into the chatbot followed by the 6-digits unique code, then the chatbot will send an ice-cream store electronic coupon to both of the new user and the old user. "
    + "Each new user can claim the coupon once only. Each user can recommend infinite number of friends. "
    + "Each new user can also recommend new users. Users who registered before the campaign cannot type \"code\" to get the coupon. "
    + "After 5000 copies of ice-cream coupon were given out, the campaign stops.";
    started = true;
    fetchUsers();
    return msg;
  }
  /**
  * Add a user into observer List.
  * @param obj User as observer
  */
  public void register(Users obj) {
      String uid = obj.getID();
  		if ( !newUids.contains(uid) ) newUids.add(uid);
  }
  /**
  * Delete a user from observer List.
  * @param obj User as observer
  */
  public void unregister(Users obj) {
      String uid = obj.getID();
      existingUids.remove(uid);
  		newUids.remove(uid);
  }

  /**
  * Get the observers along with the message to push to the observers which is wrapped in MsgAttachedData type.
  * @param msg Message to push
  * @return An instance of MsgAttachedData type containing message and observers info
  */
  public MsgAttachedData<ArrayList<String>> getNotifiableObservers(String msg) {
      ArrayList<String> allUids = new ArrayList<String>(existingUids);
      allUids.addAll(newUids);
   		return new MsgAttachedData<ArrayList<String>>(msg, allUids);
  }

  /**
  * Issue the 6-digit valid code corresponding to the id of users.
  * @param inviter User asking for code
  * @return Code corresponding the user id
  */
  public String issueCode(String inviter) {
    for(Coupon c:coupons){
      if(c != null)
        if(c.getInviter().equals(inviter))
          return c.getCode();
    }

	  Random rand = new Random();
	  int n = rand.nextInt(codes.size());
	  String code = codes.get(n);
    coupons.add(new Coupon(inviter,code));
    codes.remove(code);

    return code;
  }

  /**
  * Cast and Issue a coupon of the given code.
  * @param invitee The asker of the coupon
  * @param code Code of the coupon
  * @return An instance of type COUPON
  */
  public Coupon issueCoupon(String invitee, String code){
    if (couponRemaining() > 0){
      couponsRemaining--;

      int i = 0;
      boolean found = false;
      for(Coupon c : coupons){
        if(c.getCode().equals(code)) {found = true; break;}
        else i++;
      }
      if(found){
        coupons.get(i).setInvitee(invitee);
        if( ! gotCouponNewUsers.contains(coupons.get(i).getInviter()) ) couponsRemaining--;
        gotCouponNewUsers.add(coupons.get(i).getInviter());
        gotCouponNewUsers.add(invitee);
        return coupons.get(i);
      }
     return null;
    }
    return null;
  }
  /**
  * Checks whether the input code is valid for a invitee for casting into coupons.
  * @param invitee User source of code inputs
  * @param code Code to check
  * @return The validity of code along with user source
  */
  public boolean isCodeValid(String invitee,String code){
    for(Coupon c : coupons){
      if(c.getCode().equals(code)) {
        if(!isNewUser(c.getInviter())){ // old user case
          if(!gotCouponNewUsers.contains(invitee)) return true;
        }
        else{
         if(c.getInvitee() == null) return true;
        }
      }
    }
    return false;
  }
  /**
  * Checks if coupons still remain.
  * @return Remaining coupon numbers
  */
  public int couponRemaining(){
    return couponsRemaining ;
  }
  /**
  * Checks whether a user is registered after campaign.
  * @param user Source of type Users
  * @return whether a user is registered after campaign
  */
  public boolean isNewUser(Users user){
    String uid = user.getID();
    return isNewUser(uid);
  }
  /**
  * Checks whether a user is registered after campaign.
  * @param usid Id of source user of type String
  * @return whether a uid is registered after campaign
  */
  public boolean isNewUser(String uid){
    return newUids.contains(uid);
  }

  /**
  * Checks whether a user is qualified to get coupon from entering code.
  * @param user Source of type Users
  * @return whether a user is qualified to get coupon from entering code
  */
  public boolean canGetCouponFromCode(Users user){
    if (isNewUser(user)){
      return (!gotCouponNewUsers.contains(user.getID()));
    }
    else return false;
  }
  /**
  * Checks whether a user is requesting a coupon with its own code.
  * @param usid Id of source user of type String
  * @param text Code to check
  * @return whether a user is requesting a coupon with its own code
  */
  public boolean checkSelf(String uid, String text){
    for(Coupon c : coupons){
      if(c.getCode().equals(text))
        if(c.getInviter().equals(uid))
          return true;
    }
    return false;
  }
  /**
  * Checks if the campaign is started.
  */
  public static boolean isCampaignStarted(){
    return started;
  }
  /**
  * Checks whether a user of user id has receieve a coupon or not.
  * @param uid User id of String types
  * @return if a user of user id has receieve a coupon
  */
  public boolean notGotCoupon(String uid){
    return gotCouponNewUsers.contains(uid);
  }

}
