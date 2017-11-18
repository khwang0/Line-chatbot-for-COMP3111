package com.example.bot.spring.webapplication.domain;

public class Customer {

    int adults;
    int children;
    int toddler;
    String name;
    String bootableid;
    String phone;
    String special;
    double totalPrice;
    double pricePaid;
    int idx;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getBootableId() {
    	return bootableid;
    }

	public void setBootableId(String bootableid) {
		this.bootableid = bootableid;
	}
	
    public String getPhone() {
    	return phone;
    }

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getSpecial() {
		return special;
	}
	
	public void setSpecial(String special) {
		this.special = special;
	}
	
	public int getAdults() {
		return adults;
	}

	public void setAdults(Integer adults) {
		this.adults = adults;
	}
	
	public int getChildren() {
		return adults;
	}

	public void setChildren(Integer children) {
		this.children = children;
	}
	
	public int getToddler() {
		return toddler;
	}
	
	public void setToddler(Integer toddler) {
		this.toddler = toddler;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}
	
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public double getPricePaid() {
		return pricePaid;
	}
	
	public void setPricePaid(double pricePaid) {
		this.pricePaid = pricePaid;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public void setIdx(int idx) {
		this.idx = idx;
	}
}