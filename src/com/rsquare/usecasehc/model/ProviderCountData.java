package com.rsquare.usecasehc.model;

public class ProviderCountData {
	
	private String npi;
	private String relationCount;
	private String referralCount;
	private String referredCount;
	public ProviderCountData(String npi, String relationCount,
			String referralCount, String referredCount) {
		super();
		this.npi = npi;
		this.relationCount = relationCount;
		this.referralCount = referralCount;
		this.referredCount = referredCount;
	}
	public String getNpi() {
		return npi;
	}
	public void setNpi(String npi) {
		this.npi = npi;
	}
	public String getRelationCount() {
		return relationCount;
	}
	public void setRelationCount(String relationCount) {
		this.relationCount = relationCount;
	}
	public String getReferralCount() {
		return referralCount;
	}
	public void setReferralCount(String referralCount) {
		this.referralCount = referralCount;
	}
	public String getReferredCount() {
		return referredCount;
	}
	public void setReferredCount(String referredCount) {
		this.referredCount = referredCount;
	}
	public String toCSVString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(npi);
		builder.append(",");
		builder.append(relationCount);
		builder.append(",");
		builder.append(referralCount);
		builder.append(",");
		builder.append(referredCount);
		return builder.toString();
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderCountData [npi=");
		builder.append(npi);
		builder.append(", relationCount=");
		builder.append(relationCount);
		builder.append(", referralCount=");
		builder.append(referralCount);
		builder.append(", referredCount=");
		builder.append(referredCount);
		builder.append("]");
		return builder.toString();
	}
	
	

}
