package com.rsquare.usecasehc.model;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class ProviderReferralResult {

	private RDFNode doctor;
	private RDFNode referredDoctor;
	private RDFNode referralCount;
	private RDFNode direction;
	private RDFNode reverseCount;
	
	public ProviderReferralResult() {
		super();
	}
	
	public ProviderReferralResult(RDFNode doctor, RDFNode referredDoctor,
			RDFNode referralCount, RDFNode direction, RDFNode reverseCount) {
		super();
		this.doctor = doctor;
		this.referredDoctor = referredDoctor;
		this.referralCount = referralCount;
		this.direction = direction;
		this.reverseCount = reverseCount;
	}

	public RDFNode getDoctor() {
		return doctor;
	}
	public void setDoctor(RDFNode doctor) {
		this.doctor = doctor;
	}
	public RDFNode getReferredDoctor() {
		return referredDoctor;
	}
	public void setReferredDoctor(RDFNode referredDoctor) {
		this.referredDoctor = referredDoctor;
	}
	public RDFNode getReferralCount() {
		return referralCount;
	}
	public void setReferralCount(RDFNode referralCount) {
		this.referralCount = referralCount;
	}
	public RDFNode getDirection() {
		return direction;
	}
	public void setDirection(RDFNode direction) {
		this.direction = direction;
	}
	public RDFNode getReverseCount() {
		return reverseCount;
	}
	public void setReverseCount(RDFNode reverseCount) {
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
