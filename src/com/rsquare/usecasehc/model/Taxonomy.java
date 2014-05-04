package com.rsquare.usecasehc.model;

public class Taxonomy {
	private String taxonomyCode;
	private String name;
	private TaxonomyClassification classification;
	private String description;
	private String taxonomyType;
	
	public Taxonomy(String taxonomyCode, String name,
			TaxonomyClassification classification, String description,
			String taxonomyType) {
		super();
		this.taxonomyCode = taxonomyCode;
		this.name = name;
		this.classification = classification;
		this.description = description;
		this.taxonomyType = taxonomyType;
	}

	public String getTaxonomyCode() {
		return taxonomyCode;
	}

	public void setTaxonomyCode(String taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaxonomyClassification getClassification() {
		return classification;
	}

	public void setClassification(TaxonomyClassification classification) {
		this.classification = classification;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaxonomyType() {
		return taxonomyType;
	}

	public void setTaxonomyType(String taxonomyType) {
		this.taxonomyType = taxonomyType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Taxonomy [taxonomyCode=");
		builder.append(taxonomyCode);
		builder.append(", name=");
		builder.append(name);
		builder.append(", classification=");
		builder.append(classification);
		builder.append(", description=");
		builder.append(description);
		builder.append(", taxonomyType=");
		builder.append(taxonomyType);
		builder.append("]");
		return builder.toString();
	}
	
	
}
