package jp.tsubakicraft.easyreport.domain.dto;

public class InvoiceLineDTO {
	private String id;
	private String unitCode;
	private Integer invoicedQuantity;
	private Float lineExtensionAmount;
	private TaxTotalDTO taxTotal;
	private String itemDescription;
	private String itemName;
	private Float itemAmount;
	private String currency;
	private Float price;
	
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public Integer getInvoicedQuantity() {
		return invoicedQuantity;
	}
	public void setInvoicedQuantity(Integer invoicedQuantity) {
		this.invoicedQuantity = invoicedQuantity;
	}
	public Float getLineExtensionAmount() {
		return lineExtensionAmount;
	}
	public void setLineExtensionAmount(Float lineExtensionAmount) {
		this.lineExtensionAmount = lineExtensionAmount;
	}
	public TaxTotalDTO getTaxTotal() {
		return taxTotal;
	}
	public void setTaxTotal(TaxTotalDTO taxTotal) {
		this.taxTotal = taxTotal;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Float getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(Float itemAmount) {
		this.itemAmount = itemAmount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
