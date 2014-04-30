package com.rsquare.usecasehc.model;

public enum LocationType {
	ZIPCODE("zipcode"), CITY("city"), STATE("state"), COUNTY("county"), COUNTRY("country");
	
	private final String type;
	
	private LocationType(String type)
	{
		this.type = type;
	}
	
	public boolean equalsType(String othertype){
        return (othertype == null)? false:type.equals(othertype);
    }

    public String toString(){
       return type;
    }
}
