package com.rsquare.usecasehc.model;


public class ProviderReferralResult {

	private String doctor;
	private String referredDoctor;
	private String referralCount;
	private String direction;
	private String reverseCount;
	
	public ProviderReferralResult() {
		super();
	}
	
	public ProviderReferralResult(String doctor, String referredDoctor,
			String referralCount, String direction, String reverseCount) {
		super();
		this.doctor = doctor;
		this.referredDoctor = referredDoctor;
		this.referralCount = referralCount;
		this.direction = direction;
		this.reverseCount = reverseCount;
	}

	public String getDoctor() {
		return doctor;
	}
	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}
	public String getReferredDoctor() {
		return referredDoctor;
	}
	public void setReferredDoctor(String referredDoctor) {
		this.referredDoctor = referredDoctor;
	}
	public String getReferralCount() {
		return referralCount;
	}
	public void setReferralCount(String referralCount) {
		this.referralCount = referralCount;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getReverseCount() {
		return reverseCount;
	}
	public void setReverseCount(String reverseCount) {
		this.reverseCount = reverseCount;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((doctor == null) ? 0 : doctor.hashCode());
		result = prime * result
				+ ((referralCount == null) ? 0 : referralCount.hashCode());
		result = prime * result
				+ ((referredDoctor == null) ? 0 : referredDoctor.hashCode());
		result = prime * result
				+ ((reverseCount == null) ? 0 : reverseCount.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProviderReferralResult other = (ProviderReferralResult) obj;
		if (direction == null) {
			if (other.direction != null) {
				return false;
			}
		} else if (!direction.equals(other.direction)) {
			return false;
		}
		if (doctor == null) {
			if (other.doctor != null) {
				return false;
			}
		} else if (!doctor.equals(other.doctor)) {
			return false;
		}
		if (referralCount == null) {
			if (other.referralCount != null) {
				return false;
			}
		} else if (!referralCount.equals(other.referralCount)) {
			return false;
		}
		if (referredDoctor == null) {
			if (other.referredDoctor != null) {
				return false;
			}
		} else if (!referredDoctor.equals(other.referredDoctor)) {
			return false;
		}
		if (reverseCount == null) {
			if (other.reverseCount != null) {
				return false;
			}
		} else if (!reverseCount.equals(other.reverseCount)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProviderReferralResult [doctor=");
		builder.append(doctor);
		builder.append(", referredDoctor=");
		builder.append(referredDoctor);
		builder.append(", referralCount=");
		builder.append(referralCount);
		builder.append(", direction=");
		builder.append(direction);
		builder.append(", reverseCount=");
		builder.append(reverseCount);
		builder.append("]");
		return builder.toString();
	}

}
