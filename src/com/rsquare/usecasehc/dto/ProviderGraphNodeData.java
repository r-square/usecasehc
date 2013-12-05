package com.rsquare.usecasehc.dto;

public class ProviderGraphNodeData {
	
	private String label;
	private String depth_loaded;
	private String tooltip;
	private String labelFontFamily;
	private String selectedgraphicfillcolor;
	private String graphictype;
	private String leftIconUrl;
	private String graphicSize;
	private String selectedgraphicsize;
	
	public ProviderGraphNodeData(String label, String depth_loaded, String tooltip,
			String labelFontFamily, String selectedgraphicfillcolor,
			String graphictype, String leftIconUrl, String graphicSize,
			String selectedgraphicsize) {
		super();
		this.label = label;
		this.depth_loaded = depth_loaded;
		this.tooltip = tooltip;
		this.labelFontFamily = labelFontFamily;
		this.selectedgraphicfillcolor = selectedgraphicfillcolor;
		this.graphictype = graphictype;
		this.leftIconUrl = leftIconUrl;
		this.graphicSize = graphicSize;
		this.selectedgraphicsize = selectedgraphicsize;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDepth_loaded() {
		return depth_loaded;
	}
	public void setDepth_loaded(String depth_loaded) {
		this.depth_loaded = depth_loaded;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getLabelFontFamily() {
		return labelFontFamily;
	}
	public void setLabelFontFamily(String labelFontFamily) {
		this.labelFontFamily = labelFontFamily;
	}
	public String getSelectedgraphicfillcolor() {
		return selectedgraphicfillcolor;
	}
	public void setSelectedgraphicfillcolor(String selectedgraphicfillcolor) {
		this.selectedgraphicfillcolor = selectedgraphicfillcolor;
	}
	public String getGraphictype() {
		return graphictype;
	}
	public void setGraphictype(String graphictype) {
		this.graphictype = graphictype;
	}
	public String getLeftIconUrl() {
		return leftIconUrl;
	}
	public void setLeftIconUrl(String leftIconUrl) {
		this.leftIconUrl = leftIconUrl;
	}
	public String getGraphicSize() {
		return graphicSize;
	}
	public void setGraphicSize(String graphicSize) {
		this.graphicSize = graphicSize;
	}
	public String getSelectedgraphicsize() {
		return selectedgraphicsize;
	}
	public void setSelectedgraphicsize(String selectedgraphicsize) {
		this.selectedgraphicsize = selectedgraphicsize;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderGraphNodeData [label=");
		builder.append(label);
		builder.append(", depth_loaded=");
		builder.append(depth_loaded);
		builder.append(", tooltip=");
		builder.append(tooltip);
		builder.append(", labelFontFamily=");
		builder.append(labelFontFamily);
		builder.append(", selectedgraphicfillcolor=");
		builder.append(selectedgraphicfillcolor);
		builder.append(", graphictype=");
		builder.append(graphictype);
		builder.append(", leftIconUrl=");
		builder.append(leftIconUrl);
		builder.append(", graphicSize=");
		builder.append(graphicSize);
		builder.append(", selectedgraphicsize=");
		builder.append(selectedgraphicsize);
		builder.append("]");
		return builder.toString();
	}
	
}
