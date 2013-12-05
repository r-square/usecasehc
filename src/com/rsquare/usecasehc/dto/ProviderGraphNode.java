package com.rsquare.usecasehc.dto;

public class ProviderGraphNode {
	
	private String id;
	private ProviderGraphNodeData data;
	public ProviderGraphNode(String id, ProviderGraphNodeData data) {
		super();
		this.id = id;
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ProviderGraphNodeData getData() {
		return data;
	}
	public void setData(ProviderGraphNodeData data) {
		this.data = data;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraphNode [id=");
		builder.append(id);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}
