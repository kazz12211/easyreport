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
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, invoiceId);
		Document document = getDocumentFromResponse(responseEntity);
		logDocument(document);
		InvoiceDetailDTO detail = parseInvoice(document);
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

	private InvoiceDetailDTO parseInvoice(Document document) {
		InvoiceDetailDTO invoice = new InvoiceDetailDTO();
		
		invoice.setInvoiceId(objectValue(XMLUtil.getChildElementFromDocument(document, "cbc:ID"), String.class));
		invoice.setIssueDate(objectValue(XMLUtil.getElementFromDocument(document, "cbc:IssueDate"), String.class));
		invoice.setDocumentCurrencyCode(objectValue(XMLUtil.getElementFromDocument(document, "cbc:DocumentCurrencyCode"), String.class));
		
		Element orderReferenceElem = XMLUtil.getElementFromDocument(document, "cac:OrderReference");
		if(orderReferenceElem != null) {
			invoice.setOrderId(objectValue(XMLUtil.getElementFromElement(orderReferenceElem, "cdc:ID"), String.class));
		}
		
		invoice.setAccountingSupplierParty(parseAccountingSupplierParty(document));
		invoice.setAccountingCustomerParty(parseAccountingCustomerParty(document));
		invoice.setTaxTotal(parseTaxTotal(document));
		invoice.setLegalMonetaryTotal(parseLegalMonetaryTotal(document));
		
		NodeList list = document.getElementsByTagName("cac:InvoiceLine");
		List<InvoiceLineDTO> invoiceLines = new ArrayList<InvoiceLineDTO>();
		if(list.getLength() > 0) {
			for(int i = 0; i < list.getLength(); i++) {
				Element element = (Element)list.item(i);
				InvoiceLineDTO invoiceLine = parseInvoiceLine(element);
				if(invoiceLine != null) {
					invoiceLines.add(invoiceLine);
				}
			}
		}
		invoice.setInvoiceLines(invoiceLines);

		return invoice;
	}

	private AccountingSupplierPartyDTO parseAccountingSupplierParty(Document document) {
		Element accountingSupplierPartyElem = XMLUtil.getElementFromDocument(document, "cac:AccountingSupplierParty");
		if(accountingSupplierPartyElem != null) {
			Element partyElem = XMLUtil.getElementFromElement(accountingSupplierPartyElem, "cac:Party");
			if(partyElem != null) {
				AccountingSupplierPartyDTO accountingSupplierParty = new AccountingSupplierPartyDTO();
				Element partyIdentificationElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyIdentification");
				accountingSupplierParty.setId(objectValue(XMLUtil.getElementFromElement(partyIdentificationElem, "cbc:ID"), String.class));
				Element partyNameElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyName");
				accountingSupplierParty.setName(objectValue(XMLUtil.getElementFromElement(partyNameElem, "cbc:Name"), String.class));
				accountingSupplierParty.setPostalAddress(parsePostalAddress(partyElem));
				accountingSupplierParty.setContact(parseContact(partyElem));
				accountingSupplierParty.setPerson(parsePerson(partyElem));
				return accountingSupplierParty;
			}
		}
		return null;
	}
	
	private AccountingCustomerPartyDTO parseAccountingCustomerParty(Document document) {
		Element accountingCustomerPartyElem = XMLUtil.getElementFromDocument(document, "cac:AccountingCustomerParty");
		if(accountingCustomerPartyElem != null) {
			Element partyElem = XMLUtil.getElementFromElement(accountingCustomerPartyElem, "cac:Party");
			if(partyElem != null) {
				AccountingCustomerPartyDTO accountingCustomerParty = new AccountingCustomerPartyDTO();
				Element partyIdentificationElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyIdentification");
				accountingCustomerParty.setId(objectValue(XMLUtil.getElementFromElement(partyIdentificationElem, "cbc:ID"), String.class));
				Element partyNameElem = XMLUtil.getElementFromElement(partyElem, "cac:PartyName");
				accountingCustomerParty.setName(objectValue(XMLUtil.getElementFromElement(partyNameElem, "cbc:Name"), String.class));
				accountingCustomerParty.setPostalAddress(parsePostalAddress(partyElem));
				accountingCustomerParty.setContact(parseContact(partyElem));
				return accountingCustomerParty;
			}
		}
		return null;
	}
	
	private TaxTotalDTO parseTaxTotal(Document document) {
		Element taxTotalElem = XMLUtil.getElementFromDocument(document, "cac:TaxTotal");
		return parseTaxTotal(taxTotalElem);
	}
	
	private TaxTotalDTO parseTaxTotal(Element element) {
		if(element != null) {
			TaxTotalDTO taxTotal = new TaxTotalDTO();
			Element taxAmountElem = XMLUtil.getElementFromElement(element, "cbc:TaxAmount");
			if(taxAmountElem != null) {
				taxTotal.setCurrency(taxAmountElem.getAttribute("currencyID"));
				taxTotal.setTaxAmount(objectValue(taxAmountElem, Float.class));
			}
			Element taxSubtotalElem = XMLUtil.getElementFromElement(element, "cac:TaxSubtotal");
			if(taxSubtotalElem != null) {
				Element taxableAmountElem = XMLUtil.getElementFromElement(taxSubtotalElem, "cbc:TaxableAmount");
				if(taxableAmountElem != null) {
					taxTotal.setTaxableAmount(objectValue(taxableAmountElem, Float.class));
				}
				Element taxCategoryElem = XMLUtil.getElementFromElement(taxSubtotalElem, "cac:TaxCategory");
				if(taxCategoryElem != null) {
					taxTotal.setTaxCategoryId(objectValue(XMLUtil.getElementFromElement(taxCategoryElem, "cbc:ID"), String.class));
					taxTotal.setPercent(objectValue(XMLUtil.getElementFromElement(taxCategoryElem, "cbc:Percent"), Float.class));
					Element taxSchemeElem = XMLUtil.getElementFromElement(taxCategoryElem, "cac:TaxScheme");
					if(taxSchemeElem != null) {
						taxTotal.setTaxSchemeId(objectValue(XMLUtil.getElementFromElement(taxSchemeElem, "cbc:ID"), String.class));
						taxTotal.setTaxSchemeName(objectValue(XMLUtil.getElementFromElement(taxSchemeElem, "cbc:Name"), String.class));
					}
				}
			}
			return taxTotal;
		}
		return null;
	}
	
	private LegalMonetaryTotalDTO parseLegalMonetaryTotal(Document document) {
		Element legalMonetaryTotalElem = XMLUtil.getElementFromDocument(document, "cac:LegalMonetaryTotal");
		if(legalMonetaryTotalElem != null) {
			LegalMonetaryTotalDTO legalMonetaryTotal = new LegalMonetaryTotalDTO();
			legalMonetaryTotal.setLineExtensionAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:LineExtensionAmount"), Float.class));
			legalMonetaryTotal.setTaxExclusiveAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:TaxExclusiveAmount"), Float.class));
			legalMonetaryTotal.setTaxInclusiveAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:TaxInclusiveAmount"), Float.class));
			legalMonetaryTotal.setPayableAmount(objectValue(XMLUtil.getElementFromElement(legalMonetaryTotalElem, "cbc:PayableAmount"), Float.class));
			return legalMonetaryTotal;
		}
		return null;
	}
	
	private InvoiceLineDTO parseInvoiceLine(Element element) {
		InvoiceLineDTO invoiceLine = new InvoiceLineDTO();
		invoiceLine.setId(objectValue(XMLUtil.getElementFromElement(element, "cbc:ID"), String.class));
		Element invoicedQuantityElem = XMLUtil.getElementFromElement(element, "cbc:InvoicedQuantity");
		if(invoicedQuantityElem != null) {
			invoiceLine.setCurrency(invoicedQuantityElem.getAttribute("currencyID"));
			invoiceLine.setInvoicedQuantity(objectValue(invoicedQuantityElem, Integer.class));
		}
		invoiceLine.setTaxTotal(parseTaxTotal(XMLUtil.getElementFromElement(element, "cac:TaxTotal")));
		Element itemElem = XMLUtil.getElementFromElement(element, "cac:Item");
		if(itemElem != null) {
			invoiceLine.setItemDescription(objectValue(XMLUtil.getElementFromElement(itemElem, "cbc:Description"), String.class));
			invoiceLine.setItemName(objectValue(XMLUtil.getElementFromElement(itemElem, "cbc:Name"), String.class));
		}
		Element priceElem = XMLUtil.getElementFromElement(element, "cac:Price");
		if(priceElem != null) {
			invoiceLine.setPrice(objectValue(XMLUtil.getElementFromElement(priceElem, "cbc:PriceAmount"), Float.class));
		}
		return invoiceLine;
	}
	
	private PostalAddressDTO parsePostalAddress(Element deliveryElement) {
		Element paElem = XMLUtil.getElementFromElement(deliveryElement, "cac:PostalAddress");
		if(paElem == null) {
			return null;
		}
		PostalAddressDTO postalAddress = new PostalAddressDTO();
		postalAddress.setStreetName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:StreetName"), String.class));
		postalAddress.setAdditionalStreetName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:AdditionalStreetName"), String.class));
		postalAddress.setBuildingNumber(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:BuildingNumber"), String.class));
		postalAddress.setCityName(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:CityName"), String.class));
		postalAddress.setPostalZone(objectValue(XMLUtil.getElementFromElement(paElem, "cbc:PostalZone"), String.class));
		postalAddress.setCountry(parseCountry(paElem));
		return postalAddress;
	}
	
	private String parseCountry(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Country");
		if(elem == null) {
			return null;
		}
		return objectValue(XMLUtil.getElementFromElement(elem, "cbc:IdentificationCode"), String.class);
	}
	
	private ContactDTO parseContact(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Contact");
		if(elem == null) {
			return null;
		}
		ContactDTO contact = new ContactDTO();
		contact.setId(objectValue(XMLUtil.getElementFromElement(elem, "cbc:ID"), String.class));
		contact.setName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:Name"), String.class));
		return contact;
	}
	
	private PersonDTO parsePerson(Element deliveryElement) {
		Element elem = XMLUtil.getElementFromElement(deliveryElement, "cac:Person");
		if(elem == null) {
			return null;
		}
		PersonDTO person = new PersonDTO();
		person.setFirstName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:FirstName"), String.class));
		person.setFamilyName(objectValue(XMLUtil.getElementFromElement(elem, "cbc:FamilyName"), String.class));
		return person;
	}
	
	private static DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unchecked")
	protected <T> T objectValue(Element element, Class<?> clazz) {
		if(element == null)
			return null;

		String text = element.getTextContent();
		T value = null;
		if(text != null) {
			if(clazz.isAssignableFrom(String.class)) {
				value = (T) text;
			} else if(clazz.isAssignableFrom(Date.class)) {
				try {
					value = (T) _DATE_FORMAT.parse(text);
				} catch (ParseException e) {
				}
			} else if(clazz.isAssignableFrom(Integer.class)) {
				value =  (T) Integer.valueOf(text);
			} else if(clazz.isAssignableFrom(Long.class)) {
				value =  (T) Long.valueOf(text);
			} else if(clazz.isAssignableFrom(Float.class)) {
				value =  (T) Float.valueOf(text);
			} else if(clazz.isAssignableFrom(Double.class)) {
				value =  (T) Double.valueOf(text);
			} else if(clazz.isAssignableFrom(Boolean.class)) {
				value =  (T) Boolean.valueOf(text);
			} 
		}
		
		return value;
		
	}
	
    
	protected Document getDocumentFromResponse(ResponseEntity<String> responseEntity) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(responseEntity.getBody().toString().getBytes()));
	}

	protected void logDocument(Document document) {
		LOGGER.info(XMLUtil.toXmlString(document));
	}

}
