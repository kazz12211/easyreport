package jp.tsubakicraft.easyreport.domain.dto;

import java.util.List;

public class InvoiceDetailDTO {

	private String invoiceId;
	private String issueDate;
	private String documentCurrencyCode;
	private String orderId;
	private String documentId;
	private AccountingSupplierPartyDTO accountingSupplierParty;
	private AccountingCustomerPartyDTO accountingCustomerParty;
	private TaxTotalDTO taxTotal;
	private LegalMonetaryTotalDTO legalMonetaryTotal;
	private List<InvoiceLineDTO> invoiceLines;
	
	public String getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getIssueDate() {
		return issueDate;
		
	}
	public void setIssueDate(String issueDate) {
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

	public AccountingSupplierPartyDTO getAccountingSupplierParty() {
		return accountingSupplierParty;
	}

	public void setAccountingSupplierParty(AccountingSupplierPartyDTO accountingSupplierParty) {
		this.accountingSupplierParty = accountingSupplierParty;
	}

	public AccountingCustomerPartyDTO getAccountingCustomerParty() {
		return accountingCustomerParty;
	}
	
	public void setAccountingCustomerParty(AccountingCustomerPartyDTO accountingCustomerParty) {
		this.accountingCustomerParty = accountingCustomerParty;
	}
	
	public TaxTotalDTO getTaxTotal() {
		return taxTotal;
	}
	
	public void setTaxTotal(TaxTotalDTO taxTotal) {
		this.taxTotal = taxTotal;
	}

	public LegalMonetaryTotalDTO getLegalMonetaryTotal() {
		return legalMonetaryTotal;
	}

	public void setLegalMonetaryTotal(LegalMonetaryTotalDTO legalMonetaryTotal) {
		this.legalMonetaryTotal = legalMonetaryTotal;		
	}

	public List<InvoiceLineDTO> getInvoiceLines() {
		return invoiceLines;
	}

	public void setInvoiceLines(List<InvoiceLineDTO> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

}
