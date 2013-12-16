package com.rsquare.usecasehc.dto;

public class ProviderGraphNode {
	
	private String id;
	private ProviderGraphNodeData data;
	private int refCount;
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
	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraphNode [id=");
		builder.append(id);
		builder.append(", data=");
		builder.append(data);
		builder.append(", refCount=");
		builder.append(refCount);
		builder.append("]");
		return builder.toString();
	}
	
}
