package com.example.bot.spring;
/**
* A parameter class which contains a message along with the corresponding data container
* @version 1.0
* @since   2017/11/19
*/
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
