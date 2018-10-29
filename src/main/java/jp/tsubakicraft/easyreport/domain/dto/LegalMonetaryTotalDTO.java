package jp.tsubakicraft.easyreport.domain.dto;

public class LegalMonetaryTotalDTO {

	private Float lineExtensionAmount;
	private Float taxExclusiveAmount;
	private Float taxInclusiveAmount;
	private Float payableAmount;
	
	public Float getLineExtensionAmount() {
		return lineExtensionAmount;
	}
	public void setLineExtensionAmount(Float lineExtensionAmount) {
		this.lineExtensionAmount = lineExtensionAmount;
	}
	public Float getTaxExclusiveAmount() {
		return taxExclusiveAmount;
	}
	public void setTaxExclusiveAmount(Float taxExclusiveAmount) {
		this.taxExclusiveAmount = taxExclusiveAmount;
	}
	public Float getTaxInclusiveAmount() {
		return taxInclusiveAmount;
	}
	public void setTaxInclusiveAmount(Float taxInclusiveAmount) {
		this.taxInclusiveAmount = taxInclusiveAmount;
	}
	public Float getPayableAmount() {
		return payableAmount;
	}
	public void setPayableAmount(Float payableAmount) {
		this.payableAmount = payableAmount;
	}
}
