package com.microservice3.dto;

public class CustomerDTO {

	String firstName;
	String lastNname;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastNname() {
		return lastNname;
	}
	public void setLastNname(String lastNname) {
		this.lastNname = lastNname;
	}
	@Override
	public String toString() {
		return "CustomerDTO [firstName=" + firstName + ", lastNname=" + lastNname + "]";
	}
	
	
}
