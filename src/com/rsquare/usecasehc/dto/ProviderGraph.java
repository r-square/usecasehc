package com.rsquare.usecasehc.dto;

import java.util.List;

public class ProviderGraph {
	private List<ProviderGraphNode> nodes;
	private List<ProviderGraphEdge> edges;
	public ProviderGraph(List<ProviderGraphNode> nodes,
			List<ProviderGraphEdge> edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}
	public List<ProviderGraphNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<ProviderGraphNode> nodes) {
		this.nodes = nodes;
	}
	public List<ProviderGraphEdge> getEdges() {
		return edges;
	}
	public void setEdges(List<ProviderGraphEdge> edges) {
		this.edges = edges;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraph [nodes=");
		builder.append(nodes);
		builder.append(", edges=");
		builder.append(edges);
		builder.append("]");
		return builder.toString();
	}
}
