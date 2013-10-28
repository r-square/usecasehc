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
	private String graphViewButtonHTML;

	public Provider(ResultSet rs) throws SQLException {
		npi = rs.getString("npi");
		organization = rs.getString("provider_organization_name_legal_business_name_");
		first = rs.getString("provider_first_name");
		last = rs.getString("provider_last_name_legal_name_");
		taxonomy = rs.getString("healthcare_provider_taxonomy_code_1");
		general_area = rs.getString("general_area");
		specialty = rs.getString("specialty");
		this.graphViewButtonHTML = makeGraphViewButtonHTML();
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
		StringBuilder builder = new StringBuilder();
		builder.append((first==null || first.equals("")) ? "" : first);
		builder.append((first==null || first.equals("")) && (last==null || last.equals("")) ? "" : " ");
		builder.append((last==null || last.equals("")) ? "" : last);
		return builder.toString();
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public String getGeneral_area() {
		return general_area==null ? "" : general_area.replace("\"", "");
	}

	public String getSpecialty() {
		return specialty==null ? "" : specialty.replace("\"", "");
	}

	public String getGraphViewButtonHTML() {
		if(graphViewButtonHTML==null)
		{
			graphViewButtonHTML = makeGraphViewButtonHTML();
		}
		return graphViewButtonHTML;
	}
	
	private String makeGraphViewButtonHTML() {
		StringBuilder s = new StringBuilder("<input type='button' value='View' onclick=\"javascript:loadIframe('graphFrame', '");
		s.append(npi);
		s.append("');javascript:$('#tab-container').easytabs('select', '#network-graph-tab'); showNetworkGraphLeftPanel();\" />");
		return s.toString();
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
	
	public String toJSONString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[\"");
		builder.append(npi);
		builder.append("\",\"");
		builder.append(organization);
		builder.append("\",\"");
		builder.append(getName());
		builder.append("\",\"");
		builder.append(getSpecialty());
		builder.append("\",\"");
		builder.append("\u003cinput type\u003d\u0027button\u0027 value\u003d\u0027View\u0027 onclick\u003d\\\"javascript:loadIframe(\u0027graphFrame\u0027, \u0027" + npi + "\u0027);javascript:$(\u0027#tab-container\u0027).easytabs(\u0027select\u0027, \u0027#network-graph-tab\u0027); showNetworkGraphLeftPanel();\\\" /\u003e");
		builder.append("\"]");
		
		return builder.toString();
	}
	
	

}
