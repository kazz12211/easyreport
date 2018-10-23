package jp.tsubakicraft.easyreport.services;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import jp.tsubakicraft.easyreport.domain.dto.DocumentGridDTO;
import jp.tsubakicraft.easyreport.domain.dto.DocumentGridParamDTO;

@Service
public interface DocumentRetrievalService {

    DocumentGridDTO getDocumentsPage(DocumentGridParamDTO gridParamDTO) throws JSONException;

    String getCompanyNameById(String companyId) throws JSONException;

}
