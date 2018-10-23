package jp.tsubakicraft.easyreport.domain.dto;

import java.util.List;

public class DocumentGridDTO {

    private Integer numPages;
    private Integer pageId;
    private Integer documentCount;
    private List<DocumentGridRowDTO> documents;

    public DocumentGridDTO() {
    }

    public DocumentGridDTO(Integer numPages, Integer pageId, Integer documentCount, List<DocumentGridRowDTO> documents) {
        this.numPages = numPages;
        this.pageId = pageId;
        this.documentCount = documentCount;
        this.documents = documents;
    }

    public Integer getNumPages() {
        return numPages;
    }

    public void setNumPages(Integer numPages) {
        this.numPages = numPages;
    }

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public List<DocumentGridRowDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentGridRowDTO> documents) {
        this.documents = documents;
    }

}
