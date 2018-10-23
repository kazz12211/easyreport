package jp.tsubakicraft.easyreport.domain.dto;

import jp.tsubakicraft.tradeshift.domain.enums.DocType;

public class DocumentGridRowDTO {

    private String docId;
    private String docNum;
    private DocType docType;
    private String docIssueDate;
    private String senderCompany;
    private String receiverCompany;
    private String docTotal;
    private String docCurrency;
    private String docState;

    public DocumentGridRowDTO() {
    }

    public DocumentGridRowDTO(String docId, String docNum, DocType docType, String docIssueDate,
                              String senderCompany, String receiverCompany, String docTotal, String docCurrency) {
        this.docId = docId;
        this.docNum = docNum;
        this.docType = docType;
        this.docIssueDate = docIssueDate;
        this.senderCompany = senderCompany;
        this.receiverCompany = receiverCompany;
        this.docTotal = docTotal;
        this.docCurrency = docCurrency;
    }

    public DocumentGridRowDTO(String docId, String docNum, DocType docType, String docIssueDate,
                              String senderCompany, String receiverCompany, String docTotal, String docCurrency, String docState) {
        this.docId = docId;
        this.docNum = docNum;
        this.docType = docType;
        this.docIssueDate = docIssueDate;
        this.senderCompany = senderCompany;
        this.receiverCompany = receiverCompany;
        this.docTotal = docTotal;
        this.docCurrency = docCurrency;
        this.docState = docState;
    }

    public String getDocState() {
        return docState;
    }

    public void setDocState(String docState) {
        this.docState = docState;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getDocIssueDate() {
        return docIssueDate;
    }

    public void setDocIssueDate(String docIssueDate) {
        this.docIssueDate = docIssueDate;
    }

    public DocType getDocType() {
        return docType;
    }

    public void setDocType(DocType docType) {
        this.docType = docType;
    }

    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }

    public String getReceiverCompany() {
        return receiverCompany;
    }

    public void setReceiverCompany(String receiverCompany) {
        this.receiverCompany = receiverCompany;
    }

    public String getDocTotal() {
        return docTotal;
    }

    public void setDocTotal(String docTotal) {
        this.docTotal = docTotal;
    }

    public String getDocCurrency() {
        return docCurrency;
    }

    public void setDocCurrency(String docCurrency) {
        this.docCurrency = docCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentGridRowDTO)) return false;

        DocumentGridRowDTO that = (DocumentGridRowDTO) o;

        if (docId != null ? !docId.equals(that.docId) : that.docId != null) return false;
        if (docNum != null ? !docNum.equals(that.docNum) : that.docNum != null) return false;
        if (docType != that.docType) return false;
        if (docIssueDate != null ? !docIssueDate.equals(that.docIssueDate) : that.docIssueDate != null) return false;
        if (senderCompany != null ? !senderCompany.equals(that.senderCompany) : that.senderCompany != null)
            return false;
        if (receiverCompany != null ? !receiverCompany.equals(that.receiverCompany) : that.receiverCompany != null)
            return false;
        if (docTotal != null ? !docTotal.equals(that.docTotal) : that.docTotal != null) return false;
        if (docCurrency != null ? !docCurrency.equals(that.docCurrency) : that.docCurrency != null) return false;
        return docState != null ? docState.equals(that.docState) : that.docState == null;
    }

    @Override
    public int hashCode() {
        int result = docId != null ? docId.hashCode() : 0;
        result = 31 * result + (docNum != null ? docNum.hashCode() : 0);
        result = 31 * result + (docType != null ? docType.hashCode() : 0);
        result = 31 * result + (docIssueDate != null ? docIssueDate.hashCode() : 0);
        result = 31 * result + (senderCompany != null ? senderCompany.hashCode() : 0);
        result = 31 * result + (receiverCompany != null ? receiverCompany.hashCode() : 0);
        result = 31 * result + (docTotal != null ? docTotal.hashCode() : 0);
        result = 31 * result + (docCurrency != null ? docCurrency.hashCode() : 0);
        result = 31 * result + (docState != null ? docState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DocumentGridRowDTO{" +
                "docId='" + docId + '\'' +
                ", docNum='" + docNum + '\'' +
                ", docType=" + docType +
                ", docIssueDate='" + docIssueDate + '\'' +
                ", senderCompany='" + senderCompany + '\'' +
                ", receiverCompany='" + receiverCompany + '\'' +
                ", docTotal='" + docTotal + '\'' +
                ", docCurrency='" + docCurrency + '\'' +
                ", docState='" + docState + '\'' +
                '}';
    }

}
