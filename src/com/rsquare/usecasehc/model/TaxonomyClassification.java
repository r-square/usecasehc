package com.rsquare.usecasehc.model;

public enum TaxonomyClassification {
	ROOT("taxonomy_root"), GENERAL_AREA("general_area"), SPECIALTY("specialty"), SUBSPECIALTY("subspecialty");
	
	private final String classification;
	
	private TaxonomyClassification(String classification)
	{
		this.classification = classification;
	}
	
	public boolean equalsType(String othertype){
        return (othertype == null)? false:classification.equals(othertype);
    }

    public String toString(){
       return classification;
    }
}
