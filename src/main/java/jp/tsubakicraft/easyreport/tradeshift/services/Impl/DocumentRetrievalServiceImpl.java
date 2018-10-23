package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jp.tsubakicraft.easyreport.tradeshift.config.PropertySources;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.DocumentGridDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.DocumentGridParamDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.DocumentGridRowDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.enums.DocState;
import jp.tsubakicraft.easyreport.tradeshift.domain.enums.DocType;
import jp.tsubakicraft.easyreport.tradeshift.services.DocumentRetrievalService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Service
public class DocumentRetrievalServiceImpl implements DocumentRetrievalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRetrievalServiceImpl.class);

    private final String URI_LIST_DOCUMENTS;
    private final String URI_COMPANY_INFO_BY_ID;
    private final String URI_DOCUMENT_BY_ID;

    @Autowired
    TokenService tokenService;

    private RestTemplate restTemplate;

    /**
     * Inject PropertySources bean by constructor,
     * init URI_LIST_DOCUMENTS
     *
     * @param propertySources
     */
    public DocumentRetrievalServiceImpl(@Qualifier("propertySources") PropertySources propertySources) {
        this.URI_LIST_DOCUMENTS = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents";
        this.URI_COMPANY_INFO_BY_ID = propertySources.getTradeshiftAPIDomainName() +
                "/tradeshift/rest/external/companies/{companyAccountId}";

        this.URI_DOCUMENT_BY_ID = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents/{documentId}";
        this.restTemplate = new RestTemplate();
    }


    /**
     * Get Tradeshift documents for current user
     *
     * @param gridParamDTO
     * @return DocumentGridDTO
     * @throws JSONException 
     */
    @Override
    public DocumentGridDTO getDocumentsPage(DocumentGridParamDTO gridParamDTO) throws JSONException {
        LOGGER.info("get documents page for grid view ");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URI_LIST_DOCUMENTS);
        builder.queryParam("limit", gridParamDTO.getRowLimitPerPage());
        builder.queryParam("page", gridParamDTO.getPageNumber());
        builder.queryParam("ascending ", "false");
        gridParamDTO.getDocTypes().stream().map(docType -> builder.queryParam("type", docType)).collect(Collectors.toList());

        if (gridParamDTO.getDocStates() != null && gridParamDTO.getDocStates().size() > 0) {
            for (String docType : gridParamDTO.getDocStates()) {
                switch (docType) {
                    case "Accepted":
                        builder.queryParam("state", DocState.ACCEPTED);
                        break;
                    case "Awaiting_connection":
                        builder.queryParam("state", DocState.PENDING_NOT_A_CONTACT);
                        break;
                    case "Failed_delivery":
                        builder.queryParam("state", DocState.FAILED_DELIVERY);
                        break;
                    case "Disputed":
                        builder.queryParam("state", DocState.DISPUTED_BY_RECEIVER);
                        break;
                    case "Void":
                        builder.queryParam("state", DocState.REJECTED_BY_SENDER);
                        break;
                    case "Payment_Sent":
                        builder.queryParam("state", DocState.PAID_UNCONFIRMED);
                        break;
                    case "Payment_Received":
                        builder.queryParam("state", DocState.PAID_CONFIRMED);
                        break;
                    case "Overdue":
                        builder.queryParam("state", DocState.OVERDUE);
                        break;
                    case "Delivered":
                        builder.queryParam("state", DocState.DELIVERED);
                        break;
                    case "Rejected":
                        builder.queryParam("state", DocState.REJECTED_BY_RECEIVER);
                        break;
                }
            }
        } else {
            builder.queryParam("state", gridParamDTO.getDocState());
        }
        gridParamDTO.getStagList().stream().map(stag -> builder.queryParam("stag", stag)).collect(Collectors.toList());

        HttpEntity requestEntity = tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity responseEntity = this.restTemplate.exchange(builder.build().toString(), HttpMethod.GET, requestEntity, String.class);

        return this.parseDocuments(responseEntity.getBody().toString());
    }

    /**
     * Parse document list from json to DocumentGridDTO
     *
     * @param documentsJson
     * @return
     * @throws JSONException 
     */
    private DocumentGridDTO parseDocuments(String documentsJson) throws JSONException {
        List<DocumentGridRowDTO> documentDTOs = new ArrayList<>();
        JSONObject gridJson = new JSONObject(documentsJson);
        Integer numPages = gridJson.getInt("numPages");
        Integer pageId = gridJson.getInt("pageId");
        Integer documentCount = (Integer) gridJson.get("itemCount");
        LOGGER.info("Document exist " + gridJson.has("Document"));
        JSONArray docArrayJson = new JSONArray(gridJson.get("Document").toString());
        LOGGER.info("Array length " + docArrayJson.length());
        String currentUserCompanyName = getCompanyNameById(tokenService.getCurrentCompanyId());
        for (int i = 0; i < docArrayJson.length(); i++) {
            JSONObject docJsonOb = docArrayJson.getJSONObject(i);
            String docState = docJsonOb.getString("UnifiedState");
            String docId = docJsonOb.getString("DocumentId");
            String docNum = docJsonOb.getString("ID");

            String docIssueDate = ((JSONObject) ((JSONArray) docJsonOb.get("ItemInfos")).get(3)).get("value").toString();
            String docType = ((JSONObject) docJsonOb.get("DocumentType")).getString("type");
            String totalAmount = ((JSONObject) ((JSONArray) docJsonOb.get("ItemInfos")).get(1)).get("value").toString();
            String currency = ((JSONObject) ((JSONArray) docJsonOb.get("ItemInfos")).get(2)).get("value").toString();
            String receiverCompany = "";
            String senderCompany = "";
            if (docJsonOb.has("SenderCompanyName")) {
                senderCompany = docJsonOb.getString("SenderCompanyName");
            } else {
                senderCompany = currentUserCompanyName;
            }
            if (docJsonOb.has("ReceiverCompanyName")) {
                receiverCompany = docJsonOb.getString("ReceiverCompanyName");
            } else {
                if (docJsonOb.has("ReceivedToCompanyAccountId")) {
                    receiverCompany = getCompanyNameById(docJsonOb.getString("ReceivedToCompanyAccountId"));
                }
            }

            documentDTOs.add(new DocumentGridRowDTO(docId, docNum, DocType.valueOf(docType), docIssueDate, senderCompany, receiverCompany, totalAmount, currency, docState));
        }
        return new DocumentGridDTO(numPages, pageId, documentCount, documentDTOs);
    }

    /**
     * Get company name by company ID
     *
     * @param companyId
     * @return
     * @throws JSONException 
     */
    @Override
    public String getCompanyNameById(String companyId) throws JSONException {
        LOGGER.info("get Company Name ");

        HttpEntity<String> requestEntity = (HttpEntity<String>) tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity responseEntity = restTemplate.exchange(URI_COMPANY_INFO_BY_ID, HttpMethod.GET, requestEntity, String.class, companyId);

        return new JSONObject(responseEntity.getBody().toString()).get("CompanyName").toString();
    }

}
