package jp.tsubakicraft.easyreport.domain.dto;

import java.util.Date;

public class InvoiceDetailDTO {

	private String invoiceId;
	private Date issueDate;
	private String documentCurrencyCode;
	private String orderId;
	private String documentId;
	
	public String getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Date getIssueDate() {
		return issueDate;
		
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getDocumentCurrencyCode() {
		return documentCurrencyCode;
	}
	
	public void setDocumentCurrencyCode(String documentCurrencyCode) {
		this.documentCurrencyCode = documentCurrencyCode;
	}

	public String getOrderId() {
		return orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	

}
