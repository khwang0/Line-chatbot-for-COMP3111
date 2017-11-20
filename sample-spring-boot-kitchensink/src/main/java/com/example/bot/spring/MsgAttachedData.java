package com.example.bot.spring;

public class MsgAttachedData<E>{
		private String msg ;
		private E data;
		/**
	  * Construct a instance of E type data wrapped with String type message
		* @param msg Message
		* @param data Data of type E
	  */
		public MsgAttachedData(String msg, E data){
			this.msg = msg;
			this.data = data;
		}
		/**
	  * Retrieve the message
		* @return Message
	  */
		public String getMsg(){return msg;}
		/**
	  * Retreive the data
		* @return Data of type E
	  */
		public E getData(){return data;}
}
