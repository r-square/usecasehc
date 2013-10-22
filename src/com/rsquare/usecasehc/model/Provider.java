package com.rsquare.usecasehc.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Provider {
	private String npi;
	private String organization;
	private String first;
	private String last;
	private String taxonomy;

	public Provider(ResultSet rs) throws SQLException {
		npi = rs.getString("npi");
		organization = rs.getString("provider_organization_name_legal_business_name_");
		first = rs.getString("provider_first_name");
		last = rs.getString("provider_last_name_legal_name_");
		taxonomy = rs.getString("healthcare_provider_taxonomy_code_1");
	}
	
	public Provider(String npi, String organization, String first, String last,
			String taxonomy) {
		super();
		this.npi = npi;
		this.organization = organization;
		this.first = first;
		this.last = last;
		this.taxonomy = taxonomy;
	}



	public String getNpi() {
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
