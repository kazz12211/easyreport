package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jp.tsubakicraft.easyreport.tradeshift.config.PropertySources;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.InvoiceDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.InvoiceDetailDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.InvoicePageDTO;
import jp.tsubakicraft.easyreport.tradeshift.services.InvoiceRetrievalService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Service
public class InvoiceRetrievalServiceImpl implements InvoiceRetrievalService {

	static Logger LOGGER = LoggerFactory.getLogger(InvoiceRetrievalServiceImpl.class);
	
	@Autowired
	TokenService tokenService;
	
	private final String URI_LIST_DOCUMENTS;
	
	@Autowired
	public InvoiceRetrievalServiceImpl(@Qualifier("propertySources") PropertySources propertySources) {
		super();
		URI_LIST_DOCUMENTS = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents";
	}
	
	@Override
	public InvoicePageDTO getInvoices(Integer limit, Integer page, String stag, String minIssueDate,
			String maxIssueDate, String createdBefore, String createdAfter, String[] processStates)
			throws JSONException {
		ResponseEntity<?> responseEntity = getDocumentList("invoice", limit, page, stag, minIssueDate, maxIssueDate, createdBefore, createdAfter, processStates);
		return parseDocuments(responseEntity);
	}

	@Override
	public InvoiceDetailDTO getInvoiceDetail(String invoiceId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private InvoicePageDTO parseDocuments(ResponseEntity<?> responseEntity) throws JSONException {
				
		InvoicePageDTO page = new InvoicePageDTO(0, 0, 0, 0, null);

		if(responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
			LOGGER.info("Parsing invoices");
	        JSONObject json = new JSONObject((String) responseEntity.getBody());
			int pageId = json.getInt("pageId");
			int itemCount = json.getInt("itemCount");
			int numPages = json.getInt("numPages");
			int itemsPerPage = json.getInt("itemsPerPage");
			
			List<InvoiceDTO> invoiceDTOs = new ArrayList<InvoiceDTO>();
			
			JSONArray docs = json.getJSONArray("Document");
	        for (int i = 0; i < docs.length(); i++) {
	            JSONObject doc = docs.getJSONObject(i);
				InvoiceDTO invoice = new InvoiceDTO();
				invoice.setId(doc.getString("ID"));
				invoice.setDocumentId(doc.getString("DocumentId"));
				invoice.setDocumentURI(doc.getString("URI"));
				if(doc.has("ReceiverCompanyName")) {
					invoice.setReceiverCompanyName(doc.getString("ReceiverCompanyName"));
				}
				if(doc.has("SenderCompanyName")) {
					invoice.setSenderCompanyName(doc.getString("SenderCompanyName"));
				}
				invoice.setState(doc.getString("ProcessState"));
				JSONObject docType = doc.getJSONObject("DocumentType");
				invoice.setType(docType.getString("type"));
				JSONArray items = doc.getJSONArray("ItemInfos");
				for(int j = 0; j < items.length(); j++ ) {
					JSONObject item = items.getJSONObject(j);
					String type = item.getString("type");
					if("document.currency".equals(type)) {
						invoice.setCurrency(item.getString("value"));
					}
					if("document.description".equals(type)) {
						invoice.setDescription(item.getString("value"));
					}
					if("document.issuedate".equals(type)) {
						invoice.setIssueDate(item.getString("value"));
					}
					if("document.total".equals(type)) {
						invoice.setTotal(Float.valueOf(item.getString("value")));
					}
				}
				invoiceDTOs.add(invoice);
			}
			page.setPageId(pageId);
			page.setItemsPerPage(itemsPerPage);
			page.setItemCount(itemCount);
			page.setNumPages(numPages);
			page.setInvoices(invoiceDTOs);
		}
		
		
		return page;
	}

	private ResponseEntity<?> getDocumentList(
			String documentType,
			Integer limit,
			Integer page,
			String stag,
			String minIssueDate,
			String maxIssueDate,
			String createdBefore,
			String createdAfter,
			String[] processStates) throws JSONException {
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<?> requestEntity = 	tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URI_LIST_DOCUMENTS);
		builder.queryParam("type", documentType);
		builder.queryParam("limit", limit);
		builder.queryParam("page", page);
		if(stag != null) {
			builder.queryParam("stag", stag);
		}
		if(minIssueDate != null && minIssueDate.length() > 0) {
			builder.queryParam("minissuedate", minIssueDate);
		}
		if(maxIssueDate != null && maxIssueDate.length() > 0) {
			builder.queryParam("maxissuedate", maxIssueDate);
		}
		if(createdBefore != null && createdBefore.length() > 0) {
			builder.queryParam("createdBefore", createdBefore);
		}
		if(createdAfter != null && createdAfter.length() > 0) {
			builder.queryParam("createdAfter", createdAfter);
		}
		if(processStates != null && processStates.length > 0) {
			for(String processState : processStates) {
				builder.queryParam("processState", processState);
			}
		}
		String url = builder.build().toString();
		LOGGER.info("*********** \nQuery: " + url);
		ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		LOGGER.info("*********** \nResponse conent type: " + responseEntity.getHeaders().getContentType());
		return responseEntity;
	}


}
