package com.rsquare.usecasehc.dto;

public class ProviderGraphEdge {
	
	private String id;
	private String target;
	private String source;
	private ProviderGraphEdgeData data;
	public ProviderGraphEdge(String id, String target, String source, ProviderGraphEdgeData data) {
		super();
		this.id = id;
		this.target = target;
		this.source = source;
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public ProviderGraphEdgeData getData() {
		return data;
	}
	public void setData(ProviderGraphEdgeData data) {
		this.data = data;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraphEdge [id=");
		builder.append(id);
		builder.append(", target=");
		builder.append(target);
		builder.append(", source=");
		builder.append(source);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}
