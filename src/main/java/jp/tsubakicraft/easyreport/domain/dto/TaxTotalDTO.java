package jp.tsubakicraft.easyreport.domain.dto;

public class TaxTotalDTO {

	private String currency;
	private Float taxAmount;
	private Float taxableAmount;
	private String taxCategoryId;
	private Float percent;
	private String taxSchemeId;
	private String taxSchemeName;
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Float getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(Float taxAmount) {
		this.taxAmount = taxAmount;
	}
	public Float getTaxableAmount() {
		return taxableAmount;
	}
	public void setTaxableAmount(Float taxableAmount) {
		this.taxableAmount = taxableAmount;
	}
	public String getTaxCategoryId() {
		return taxCategoryId;
	}
	public void setTaxCategoryId(String taxCategoryId) {
		this.taxCategoryId = taxCategoryId;
	}
	public Float getPercent() {
		return percent;
	}
	public void setPercent(Float percent) {
		this.percent = percent;
	}
	public String getTaxSchemeId() {
		return taxSchemeId;
	}
	public void setTaxSchemeId(String taxSchemeId) {
		this.taxSchemeId = taxSchemeId;
	}
	public String getTaxSchemeName() {
		return taxSchemeName;
	}
	public void setTaxSchemeName(String taxSchemeName) {
		this.taxSchemeName = taxSchemeName;
	}
	

}
