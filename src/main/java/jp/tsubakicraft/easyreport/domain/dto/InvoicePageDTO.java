package jp.tsubakicraft.easyreport.domain.dto;

import java.util.List;

public class InvoicePageDTO {

	private int pageId;
	private int itemCount;
	private int numPages;
	private int itemsPerPage;
	private List<InvoiceDTO> invoices;
	
	public InvoicePageDTO(int pageId, int itemsPerPage, int itemCount, int numPages, List<InvoiceDTO> invoices) {
		this.pageId = pageId;
		this.itemsPerPage = itemsPerPage;
		this.itemCount = itemCount;
		this.numPages = numPages;
		this.invoices = invoices;
	}
	
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public int getNumPages() {
		return numPages;
	}
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	public int getItemsPerPage() {
		return itemsPerPage;
	}
	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public List<InvoiceDTO> getInvoices() {
		return invoices;
	}
	public void setInvoices(List<InvoiceDTO> invoices) {
		this.invoices = invoices;
	}


}
