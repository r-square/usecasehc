package com.rsquare.usecasehc.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Provider {
	private String npi;
	private String organization;
	private String first;
	private String last;
	private String taxonomy;
	private String general_area;
	private String specialty;

	public Provider(ResultSet rs) throws SQLException {
		npi = rs.getString("npi");
		organization = rs.getString("provider_organization_name_legal_business_name_");
		first = rs.getString("provider_first_name");
		last = rs.getString("provider_last_name_legal_name_");
		taxonomy = rs.getString("healthcare_provider_taxonomy_code_1");
		general_area = rs.getString("general_area");
		specialty = rs.getString("specialty");
	}
	
	public Provider(String npi, String organization, String first, String last,
			String taxonomy, String general_area, String specialty) {
		super();
		this.npi = npi;
		this.organization = organization;
		this.first = first;
		this.last = last;
		this.taxonomy = taxonomy;
		this.general_area = general_area;
		this.specialty = specialty;
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

	/**
	 * @return the general_area
	 */
	public String getGeneral_area() {
		return general_area.replace("\"", "");
	}

	/**
	 * @return the specialty
	 */
	public String getSpecialty() {
		return specialty.replace("\"", "");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Provider [npi=");
		builder.append(npi);
		builder.append(", organization=");
		builder.append(organization);
		builder.append(", first=");
		builder.append(first);
		builder.append(", last=");
		builder.append(last);
		builder.append(", taxonomy=");
		builder.append(taxonomy);
		builder.append(", general_area=");
		builder.append(general_area);
		builder.append(", specialty=");
		builder.append(specialty);
		builder.append("]");
		return builder.toString();
	}
	
	

}
