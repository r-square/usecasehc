package com.rsquare.usecasehc.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Provider {
	private int npi;
	private String organization;
	private String first;
	private String last;
	private String taxonomy;

	public Provider(ResultSet rs) throws SQLException {
		npi = rs.getInt(0);
		organization = rs.getString(1);
		first = rs.getString(2);
		last = rs.getString(3);
		taxonomy = rs.getString(4);
	}

	public int getNpi() {
		return npi;
	}

	public String getOrganization() {
		return organization;
	}

	public String getFirst() {
		return first;
	}

	public String getLast() {
		return last;
	}

	public String getName() {
		return first + " " + last;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

}
