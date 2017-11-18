package com.example.bot.spring.webapplication.domain;

public class Tour {
	
	String tourId;
	String tourName;
	String tourDate;
	int tourGuideId;
	String nameOfHotel;
	int tourCapacity;
	int registeredNum;
	int minTourist;
	
	public void setTourId(String tourId) {
		this.tourId = tourId;
	}
	
	public String getTourId() {
		return tourId;
	}
	
	public void setTourName(String tourName) {
		this.tourName = tourName;
	}
	
	public String getTourName() {
		return tourName;
	}
	
	public void setTourDate(String tourDate) {
		this.tourDate = tourDate;
	}
	
	public String getTourDate() {
		return tourDate;
	}
	
	public void setTourGuideId(int tourGuideId) {
		this.tourGuideId = tourGuideId;
	}
	
	public int getTourGuideId() {
		return tourGuideId;
	}
	
	public void setNameOfHotel(String nameOfHotel) {
		this.nameOfHotel = nameOfHotel;
	}
	
	public String getNameOfHotel() {
		return nameOfHotel;
	}
	
	public void setTourCapacity(int tourCapacity) {
		this.tourCapacity = tourCapacity;
	}
	
	public int getTourCapacity() {
		return tourCapacity;
	}
	
	public void setRegisteredNum(int registeredNum) {
		this.registeredNum = registeredNum;
	}
	
	public int getRegisteredNum() {
		return registeredNum;
	}
	
	public void setMinTourist(int minTourist) {
		this.minTourist = minTourist;
	}
	
	public int getMinTourist() {
		return minTourist;
	}
}