package com.rsquare.usecasehc.model;

import java.util.List;

public class ProviderSpecialtyNodes {
	private List<ProviderSpecialtyNode> nodes;

	public List<ProviderSpecialtyNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ProviderSpecialtyNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderSpecialtyNodes [nodes=");
		builder.append(nodes);
		builder.append("]");
		return builder.toString();
	}
}
