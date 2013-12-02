package com.rsquare.usecasehc.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Provider implements Serializable {
	private String npi;
	private String organization;
	private String first;
	private String last;
	private String name;
	private String taxonomy;
	private String general_area;
	private String specialty;
	private String graphViewButtonHTML;
	private String city;
	private String state;
	private static final long serialVersionUID = 100L;

	public Provider(ResultSet rs) throws SQLException {
		npi = rs.getString("npi");
		organization = rs.getString("provider_organization_name_legal_business_name_");
		first = rs.getString("provider_first_name");
		last = rs.getString("provider_last_name_legal_name_");
		taxonomy = rs.getString("healthcare_provider_taxonomy_code_1");
		general_area = rs.getString("general_area");
		makeSpecialtyHTML(rs.getString("specialty"));
		state = rs.getString("provider_business_mailing_address_state_name");
		city = rs.getString("provider_business_mailing_address_city_name");
		this.graphViewButtonHTML = makeGraphViewButtonHTML();
		this.name = makeNameString();
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
		this.graphViewButtonHTML = makeGraphViewButtonHTML();
		this.name = makeNameString();
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
		if(name == null)
		{
			name = makeNameString();
		}
		return name;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public String getGeneral_area() {
		return general_area==null ? "" : general_area.replace("\"", "");
	}

	public String getSpecialty() {
		return specialty;
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

	public String getGraphViewButtonHTML() {
		if(graphViewButtonHTML==null)
		{
			graphViewButtonHTML = makeGraphViewButtonHTML();
		}
		return graphViewButtonHTML;
	}
	
	private String makeGraphViewButtonHTML() {
		StringBuilder s = new StringBuilder("<input type='button' value='View' onclick=\"javascript:loadGraph('");
		s.append(npi);
		s.append("');\" />");
		return s.toString();
	}
	
	private void makeSpecialtyHTML(String s) {
		specialty = "<div id=\"city\">" + (s==null ? "" : s.replace("\"", ""))  + "</div>";
	}
	
	private String makeNameString() {
		StringBuilder builder = new StringBuilder();
		if(organization==null || organization.equals(""))
		{
			builder.append((first==null || first.equals("")) ? "" : first);
			builder.append((first==null || first.equals("")) && (last==null || last.equals("")) ? "" : " ");
			builder.append((last==null || last.equals("")) ? "" : last);
		}
		else
		{
			builder.append(organization);
		}
		return builder.toString();
	}

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
		builder.append(", name=");
		builder.append(name);
		builder.append(", taxonomy=");
		builder.append(taxonomy);
		builder.append(", general_area=");
		builder.append(general_area);
		builder.append(", specialty=");
		builder.append(specialty);
		builder.append(", graphViewButtonHTML=");
		builder.append(graphViewButtonHTML);
		builder.append(", city=");
		builder.append(city);
		builder.append(", state=");
		builder.append(state);
		builder.append("]");
		return builder.toString();
	}

	

}
