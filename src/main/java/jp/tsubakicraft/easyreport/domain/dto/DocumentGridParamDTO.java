package jp.tsubakicraft.easyreport.domain.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DocumentGridParamDTO {

    @NotNull
    private Integer pageNumber = 0;

    @Min(1)
    @Max(1000)
    private Integer rowLimitPerPage = 25;
    private String docState = "DELIVERED";

    private List<String> docStates;

    @Size(min = 1)
    @NotNull
    private List<String> docTypes;

    private List<String> stagList;

    public DocumentGridParamDTO() {
        docStates = new ArrayList<>();
    }

    public DocumentGridParamDTO(Integer pageNumber, Integer rowLimitPerPage, String docState,
                                List<String> docTypes, List<String> stagList) {
        this();
        this.pageNumber = pageNumber;
        this.rowLimitPerPage = rowLimitPerPage;
        this.docState = docState;
        this.docTypes = docTypes;
        this.stagList = stagList;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getRowLimitPerPage() {
        return rowLimitPerPage;
    }

    public void setRowLimitPerPage(Integer rowLimitPerPage) {
        this.rowLimitPerPage = rowLimitPerPage;
    }

    public String getDocState() {
        return docState;
    }

    public void setDocState(String docState) {
        this.docState = docState;
    }

    public List<String> getDocTypes() {
        return docTypes;
    }

    public void setDocTypes(List<String> docTypes) {
        this.docTypes = docTypes;
    }

    public List<String> getDocStates() {
        return docStates;
    }

    public void setDocStates(List<String> docStates) {
        this.docStates = docStates;
    }

    public List<String> getStagList() {
        return stagList;
    }

    public void setStagList(List<String> stagList) {
        this.stagList = stagList;
    }

}
