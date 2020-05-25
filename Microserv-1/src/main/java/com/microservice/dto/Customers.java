package com.microservice.dto;

import java.util.*;

public class Customers {

	String name;	
	List<Customers> sub_Class = new ArrayList<>();
	
public List<Customers> getSub_Class() {
		return sub_Class;
	}
	public void setSub_Class(List<Customers> sub_Class) {
		this.sub_Class = sub_Class;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
