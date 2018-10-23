package jp.tsubakicraft.easyreport.domain.dto;

public class OrderGridRowDTO {
	
    private Float orderTotal;
    private String orderIssueDate;
    private String senderCompanyName;
    private String receiverCompanyName;
    private String documentId;
    private String orderId;
    private String orderCurrency;

    public OrderGridRowDTO(String documentId, String orderId, Float orderTotal, String orderIssueDate, String
                                            senderCompanyName, String orderCurrency, String receiverCompanyName) {
        this.documentId = documentId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.orderIssueDate = orderIssueDate;
        this.senderCompanyName = senderCompanyName;
        this.orderCurrency = orderCurrency;
        this.receiverCompanyName = receiverCompanyName;
    }

    public String getReceiverCompanyName() {
        return receiverCompanyName;
    }

    public void setReceiverCompanyName(String receiverCompanyName) {
        this.receiverCompanyName = receiverCompanyName;
    }

    public Float getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Float orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderIssueDate() {
        return orderIssueDate;
    }

    public void setOrderIssueDate(String orderIssueDate) {
        this.orderIssueDate = orderIssueDate;
    }

    public String getSenderCompanyName() {
        return senderCompanyName;
    }

    public void setSenderCompanyName(String senderCompanyName) {
        this.senderCompanyName = senderCompanyName;
    }

    public String getDocId() {
        return documentId;
    }

    public void setDocId(String documentId) {
        this.documentId = documentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderGridRowDTO that = (OrderGridRowDTO) o;

        if (orderTotal != null ? !orderTotal.equals(that.orderTotal) : that.orderTotal != null) return false;
        if (orderIssueDate != null ? !orderIssueDate.equals(that.orderIssueDate) : that.orderIssueDate != null)
            return false;
        if (senderCompanyName != null ? !senderCompanyName.equals(that.senderCompanyName) : that.senderCompanyName != null)
            return false;
        if (documentId != null ? !documentId.equals(that.documentId) : that.documentId != null) return false;
        if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
        return orderCurrency != null ? orderCurrency.equals(that.orderCurrency) : that.orderCurrency == null;

    }

    @Override
    public int hashCode() {
        int result = orderTotal != null ? orderTotal.hashCode() : 0;
        result = 31 * result + (orderIssueDate != null ? orderIssueDate.hashCode() : 0);
        result = 31 * result + (senderCompanyName != null ? senderCompanyName.hashCode() : 0);
        result = 31 * result + (documentId != null ? documentId.hashCode() : 0);
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        result = 31 * result + (orderCurrency != null ? orderCurrency.hashCode() : 0);
        return result;
    }

}
