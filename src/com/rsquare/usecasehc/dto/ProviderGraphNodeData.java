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
	private String name;
	private String specialty;
	private String generalArea;
	private String city;
	private String state;
	private String graphicFillColor;
	private String referralCountToolTip;
	private String pid;
	
	public ProviderGraphNodeData(String label, String depth_loaded, String pid,
			String labelFontFamily, String selectedgraphicfillcolor,
			String graphictype, String leftIconUrl, String graphicSize,
			String selectedgraphicsize, String name, String specialty, String generalArea, 
			String city, String state, String graphicFillColor, String referralCountToolTip) {
		super();
		this.label = label;
		this.depth_loaded = depth_loaded;
		this.pid = pid;
		this.labelFontFamily = labelFontFamily;
		this.selectedgraphicfillcolor = selectedgraphicfillcolor;
		this.graphictype = graphictype;
		this.leftIconUrl = leftIconUrl;
		this.graphicSize = graphicSize;
		this.selectedgraphicsize = selectedgraphicsize;
		this.name = name;
		this.specialty = specialty;
		this.generalArea = generalArea;
		this.city = city;
		this.state = state;
		this.graphicFillColor = graphicFillColor;
		this.referralCountToolTip = referralCountToolTip;
		this.tooltip = makeHTMLToolTip();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getGeneralArea() {
		return generalArea;
	}
	public void setGeneralArea(String generalArea) {
		this.generalArea = generalArea;
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
	
	private String makeHTMLToolTip()
	{
		StringBuilder s = new StringBuilder("<b>PID:</b> ");
		s.append(pid);
		s.append("&#13;");
		s.append("<b>Provider:</b> ");
		s.append(name);
		s.append("&#13;");
		s.append("<b>General Area:</b> ");
		s.append(generalArea);
		s.append("&#13;");
		s.append("<b>Specialty:</b> ");
		s.append(specialty);
		s.append("&#13;");
		s.append("<b>City:</b> ");
		s.append(city);
		s.append("&#13;");
		s.append("<b>State:</b> ");
		s.append(state);
		s.append("&#13;");
		s.append(referralCountToolTip);
		return s.toString();
	}
	public String getGraphicFillColor() {
		return graphicFillColor;
	}
	public void setGraphicFillColor(String graphicFillColor) {
		this.graphicFillColor = graphicFillColor;
	}
	public String getReferralCountToolTip() {
		return referralCountToolTip;
	}
	public void setReferralCountToolTip(String referralCountToolTip) {
		this.referralCountToolTip = referralCountToolTip;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
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
		builder.append(", name=");
		builder.append(name);
		builder.append(", specialty=");
		builder.append(specialty);
		builder.append(", generalArea=");
		builder.append(generalArea);
		builder.append(", city=");
		builder.append(city);
		builder.append(", state=");
		builder.append(state);
		builder.append(", graphicFillColor=");
		builder.append(graphicFillColor);
		builder.append(", referralCountToolTip=");
		builder.append(referralCountToolTip);
		builder.append(", pid=");
		builder.append(pid);
		builder.append("]");
		return builder.toString();
	}
	
}
