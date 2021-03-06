package jp.tsubakicraft.easyreport.services.Impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jp.tsubakicraft.easyreport.domain.dto.AccountingCustomerPartyDTO;
import jp.tsubakicraft.easyreport.domain.dto.AccountingSupplierPartyDTO;
import jp.tsubakicraft.easyreport.domain.dto.ContactDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoiceDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoiceDetailDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoiceLineDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoicePageDTO;
import jp.tsubakicraft.easyreport.domain.dto.LegalMonetaryTotalDTO;
import jp.tsubakicraft.easyreport.domain.dto.PersonDTO;
import jp.tsubakicraft.easyreport.domain.dto.PostalAddressDTO;
import jp.tsubakicraft.easyreport.domain.dto.TaxTotalDTO;
import jp.tsubakicraft.easyreport.services.InvoiceRetrievalService;
import jp.tsubakicraft.easyreport.util.XMLUtil;
import jp.tsubakicraft.tradeshift.config.PropertySources;
import jp.tsubakicraft.tradeshift.services.TokenService;

@Service
public class InvoiceRetrievalServiceImpl implements InvoiceRetrievalService {

	static Logger LOGGER = LoggerFactory.getLogger(InvoiceRetrievalServiceImpl.class);
	
	@Autowired
	TokenService tokenService;
	
	private final String URI_LIST_DOCUMENTS;
	private final String URI_DOCUMENT_BY_ID;
	
	
	@Autowired
	public InvoiceRetrievalServiceImpl(@Qualifier("propertySources") PropertySources propertySources) {
		super();
		URI_LIST_DOCUMENTS = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents";
        URI_DOCUMENT_BY_ID = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/rest/external/documents/{documentId}";
	}
	
	@Override
	public InvoicePageDTO getInvoices(Integer limit, Integer page, String stag, String minIssueDate,
			String maxIssueDate, String createdBefore, String createdAfter, String[] processStates)
			throws JSONException {
		ResponseEntity<?> responseEntity = getDocumentList("invoice", limit, page, stag, minIssueDate, maxIssueDate, createdBefore, createdAfter, processStates);
		return parseDocuments(responseEntity);
	}

	@Override
	public InvoiceDetailDTO getInvoiceDetail(String invoiceId) throws ParserConfigurationException, SAXException, IOException, JSONException {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URI_DOCUMENT_BY_ID);
		builder.queryParam("type", "invoice");
		String url = builder.build().toString();
		LOGGER.info(url);
		
		HttpEntity<String> requestEntity = (HttpEntity<String>) tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_XML);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, invoiceId);
		Document document = getDocumentFromResponse(responseEntity);
		LOGGER.info(XMLUtil.toXmlString(document));
		InvoiceDetailDTO detail = InvoiceDetailParser.sharedInstance.parse(document);
		detail.setDocumentId(invoiceId);
		return detail;
	}
	
	
	private InvoiceDTO parseDocument(JSONObject doc) throws JSONException {
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
		return invoice;
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
	            invoiceDTOs.add(parseDocument(doc));
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
		HttpEntity<?> requestEntity = 	tokenService.getRequestHttpEntityWithAccessToken(MediaType.APPLICATION_JSON_UTF8_VALUE);
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
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		ResponseEntity<?> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		LOGGER.info("*********** \nResponse conent type: " + responseEntity.getHeaders().getContentType());
		return responseEntity;
	}


    
	protected Document getDocumentFromResponse(ResponseEntity<String> responseEntity) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(responseEntity.getBody().toString().getBytes(Charset.forName("UTF-8"))));
	}

}
