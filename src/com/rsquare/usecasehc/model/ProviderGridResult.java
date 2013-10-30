package com.rsquare.usecasehc.model;

import java.io.Serializable;
import java.util.List;

public class ProviderGridResult implements Serializable {
//	private int sEcho;
	private String iTotalRecords;
	private String iTotalDisplayRecords;
	private List<Provider> aaData;
	private static final long serialVersionUID = 99L;
	
	public ProviderGridResult(String iTotalRecords,
			String iTotalDisplayRecords, List<Provider> aaData) {
		super();
//		this.sEcho = sEcho;
		this.iTotalRecords = iTotalRecords;
		this.iTotalDisplayRecords = iTotalDisplayRecords;
		this.aaData = aaData;
	}
	
//	public int getsEcho() {
//		return sEcho;
//	}
//
//	public void setsEcho(int sEcho) {
//		this.sEcho = sEcho;
//	}

	public String getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(String iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public String getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(String iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<Provider> getAaData() {
		return aaData;
	}

	public void setAaData(List<Provider> aaData) {
		this.aaData = aaData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGridResult [iTotalRecords=");
//		builder.append(sEcho);
//		builder.append(", iTotalRecords=");
		builder.append(iTotalRecords);
		builder.append(", iTotalDisplayRecords=");
		builder.append(iTotalDisplayRecords);
		builder.append(", aaData=");
		builder.append(aaData);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
