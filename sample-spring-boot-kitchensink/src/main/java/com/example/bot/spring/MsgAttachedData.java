package com.example.bot.spring;

class MsgAttachedData<E>{
		private String msg ;
		private E data;
		public MsgAttachedData(String msg, E data){
			this.msg = msg;
			this.data = data;
		}
		public String getMsg(){return msg;}
		public E getData(){return data;}
}
