package jp.tsubakicraft.easyreport.domain.dto;

public class InvoiceDTO {

	private String id;
	private String receiverCompanyName;
	private String senderCompanyName;
	private String state;
	private String type;
	private String currency;
	private String description;
	private String issueDate;
	private Float total;
	private String documentId;
	private String uri;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getReceiverCompanyName() {
		return receiverCompanyName;
	}

	public void setReceiverCompanyName(String receiverCompanyName) {
		this.receiverCompanyName = receiverCompanyName;
	}
	
	public String getSenderCompanyName() {
		return senderCompanyName;
	}

	public void setSenderCompanyName(String senderCompanyName) {
		this.senderCompanyName = senderCompanyName;
	}

	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getIssueDate() {
		return issueDate;
	}
	
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public Float getTotal() {
		return total;
	}
	
	public void setTotal(Float total) {
		this.total = total;
	}

	public String getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getURI() {
		return uri;
	}
	
	public void setDocumentURI(String uri) {
		this.uri = uri;
	}

}
