package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
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
		URI_LIST_DOCUMENTS = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents?type={documentType}";
	}
	
	@Override
	public List<InvoiceDTO> getInvoices(Integer limit, Integer page, String stag, String minIssueDate,
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
	
	private List<InvoiceDTO> parseDocuments(ResponseEntity<?> responseEntity) {
		LOGGER.info("Parsing invoices");
		
		List<InvoiceDTO> invoiceDTOs = new ArrayList<InvoiceDTO>();
		
		if(responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getHeaders().getContentType() == MediaType.APPLICATION_JSON) {
			LinkedHashMap<String, Object> linkedMap = (LinkedHashMap<String, Object>) responseEntity.getBody();
			int pageId = ((Integer) linkedMap.get("pageId")).intValue();
			int itemCount = ((Integer) linkedMap.get("itemCount")).intValue();
			
			List<Map> docs = (List<Map>) linkedMap.get("Document");
			for(Map doc : docs) {
				InvoiceDTO invoice = new InvoiceDTO();
				invoice.setPageId(pageId);
				invoice.setItemCount(itemCount);
				invoice.setId((String) doc.get("ID"));
				invoice.setReceiverCompanyName((String) doc.get("ReceiverCompanyName"));
				invoice.setSenderCompanyName((String) doc.get("SenderCompanyName"));
				invoice.setState((String) doc.get("ProcessState"));
				invoice.setType("invoice");
				List<Map> items = (List<Map>) doc.get("ItemInfos");
				for(Map item : items) {
					if("document.currency".equals(item.get("type"))) {
						invoice.setCurrency((String) item.get("value"));
					}
					if("document.description".equals(item.get("type"))) {
						invoice.setDescription((String) item.get("value"));
					}
					if("document.issuedate".equals(item.get("type"))) {
						invoice.setIssueDate((String) item.get("value"));
					}
					if("document.total".equals(item.get("type"))) {
						invoice.setTotal(Float.valueOf((String) item.get("value")));
					}
				}
				invoiceDTOs.add(invoice);
			}
		}
		
		return invoiceDTOs;
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
		HttpEntity requestEntity = 	tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_JSON_VALUE);
		String url = buildUrl(limit, page, stag, minIssueDate, maxIssueDate, createdBefore, createdAfter, processStates);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("documentType", documentType);
		ResponseEntity<?> responseEntity = restTemplate.exchange(builder.build().toString(), HttpMethod.GET, requestEntity, String.class);
		return responseEntity;
	}

	private String buildUrl(Integer limit, Integer page, String stag, String minIssueDate, String maxIssueDate,
			String createdBefore, String createdAfter, String[] processStates) {
		String url = URI_LIST_DOCUMENTS;
		url += "&";
		url += buildPredicateParams(limit, page, stag, minIssueDate, maxIssueDate, createdBefore, createdAfter, processStates);
		LOGGER.info("Searching invoice: " + url);
		return url;
	}

	private String buildPredicateParams(Integer limit, Integer page, String stag, String minIssueDate,
			String maxIssueDate, String createdBefore, String createdAfter, String[] processStates) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("%s=%s&", "limit", limit.toString()));
		buffer.append(String.format("%s=%s&", "page", page.toString()));
		if(stag != null) {
			buffer.append(String.format("%s=%s&", "stag", stag));
		}
		if(minIssueDate != null) {
			buffer.append(String.format("%s=%s&", "minissuedate", minIssueDate));
		}
		if(maxIssueDate != null) {
			buffer.append(String.format("%s=%s&", "maxissuedate", maxIssueDate));
		}
		if(createdBefore != null) {
			buffer.append(String.format("%s=%s&", "createdBefore", createdBefore));
		}
		if(createdAfter != null) {
			buffer.append(String.format("%s=%s&", "createdAfter", createdAfter));
		}
		if(processStates != null && processStates.length > 0) {
			for(String processState : processStates) {
				buffer.append(String.format("%s=%s&", "processState", processState));
			}
		}
		buffer.setLength(buffer.length() - 1);
		return buffer.toString();
	}


}
