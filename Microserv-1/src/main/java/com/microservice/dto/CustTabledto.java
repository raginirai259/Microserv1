package com.microservice.dto;

public class CustTabledto {
	private Integer Id;
	private Integer ParentId;
	private String Name;
	private String Color;
	private String select;
	
	
	public String getSelect() {
		return select;
	}
	public void setSelect(String select) {
		this.select = select;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public Integer getParentId() {
		return ParentId;
	}
	public void setParentId(Integer parentId) {
		ParentId = parentId;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getColor() {
		return Color;
	}
	public void setColor(String color) {
		Color = color;
	}
	@Override
	public String toString() {
		return "CustTabledto [Id=" + Id + ", ParentId=" + ParentId + ", Name=" + Name + ", Color=" + Color + "]";
	}
	
	
	
}
