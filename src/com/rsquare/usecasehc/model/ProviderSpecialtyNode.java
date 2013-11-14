package com.rsquare.usecasehc.model;

import java.util.List;

public class ProviderSpecialtyNode {
	private String label;
	private String id;
	private List<ProviderSpecialtyNode> children;
	private String taxonomy_id;
	
	public ProviderSpecialtyNode(String label, String id,
			List<ProviderSpecialtyNode> children) {
		super();
		this.label = label;
		this.id = id;
		this.children = children;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<ProviderSpecialtyNode> getChildren() {
		return children;
	}
	public void setChildren(List<ProviderSpecialtyNode> children) {
		this.children = children;
	}
	public String getTaxonomy_id() {
		return taxonomy_id;
	}
	public void setTaxonomy_id(String taxonomy_id) {
		this.taxonomy_id = taxonomy_id;
	}
	
	public boolean hasChildren()
	{
		return children!=null && children.size() > 0;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderSpecialtyNode [label=");
		builder.append(label);
		builder.append(", id=");
		builder.append(id);
		builder.append(", taxonomy_id=");
		builder.append(taxonomy_id);
		builder.append(", children=");
		builder.append(children);
		builder.append("]");
		return builder.toString();
	}
}
