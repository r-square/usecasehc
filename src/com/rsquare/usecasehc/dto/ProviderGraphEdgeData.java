package com.rsquare.usecasehc.dto;

public class ProviderGraphEdgeData {

	private String tooltip;
	private String edgeLineColor;
	private String bidirectional;
	public ProviderGraphEdgeData(String tooltip, String edgeLineColor, String bidirectional) {
		super();
		this.tooltip = tooltip;
		this.edgeLineColor = edgeLineColor;
		this.bidirectional = bidirectional;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getEdgeLineColor() {
		return edgeLineColor;
	}
	public void setEdgeLineColor(String edgeLineColor) {
		this.edgeLineColor = edgeLineColor;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraphEdgeData [tooltip=");
		builder.append(tooltip);
		builder.append(", edgeLineColor=");
		builder.append(edgeLineColor);
		builder.append(", bidirectional=");
		builder.append(bidirectional);
		builder.append("]");
		return builder.toString();
	}
}
