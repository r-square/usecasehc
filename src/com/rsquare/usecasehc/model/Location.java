package com.rsquare.usecasehc.model;

public class Location {
	private String value;
	private double lat;
	private double lon;
	private String city;
	private String state;
	private String county;
	private String country;
	private LocationType type;
	
	public Location(String value, double lat, double lon, String city,
			String state, String county, String country, LocationType type) {
		super();
		this.value = value;
		this.lat = lat;
		this.lon = lon;
		this.city = city;
		this.state = state;
		this.county = county;
		this.country = country;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public LocationType getType() {
		return type;
	}

	public void setType(LocationType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [value=");
		builder.append(value);
		builder.append(", lat=");
		builder.append(lat);
		builder.append(", lon=");
		builder.append(lon);
		builder.append(", city=");
		builder.append(city);
		builder.append(", state=");
		builder.append(state);
		builder.append(", county=");
		builder.append(county);
		builder.append(", country=");
		builder.append(country);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}
