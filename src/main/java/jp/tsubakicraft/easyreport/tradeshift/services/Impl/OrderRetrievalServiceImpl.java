package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.tsubakicraft.easyreport.tradeshift.config.PropertySources;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.OrderGridRowDTO;
import jp.tsubakicraft.easyreport.tradeshift.services.OrderRetrievalService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Service
public class OrderRetrievalServiceImpl implements OrderRetrievalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRetrievalServiceImpl.class);

    protected final String URI_ORDER_BY_ID;
    private final String URI_PARAM_DOC_TYPE = "order";
    private final String URI_PARAM_ORDER_BY = "Date";
    private final String URI_LIST_DOCUMENTS;
    private final String URI_COMPANY_INFO_BY_ID;

    protected RestTemplate restTemplate = new RestTemplate();
    protected TokenService tokenService;

    /**
     * Inject PropertySources bean by constructor,
     * init URI_LIST_DOCUMENTS
     * init URI_COMPANY_INFO_BY_ID
     *
     * @param propertySources
     */
    @Autowired
    public OrderRetrievalServiceImpl(@Qualifier("propertySources") PropertySources propertySources) {
        URI_LIST_DOCUMENTS = new StringBuilder().append(propertySources.getTradeshiftAPIDomainName())
                .append("/tradeshift/rest/external/documents?")
                .append("type={docType}&limit={orderLimitPerPage}")
                .append("&page={orderPageNumber}&ordering={Sorting}&state={orderState}").toString();

        URI_ORDER_BY_ID = new StringBuilder().append(propertySources.getTradeshiftAPIDomainName())
                .append("/tradeshift/rest/external/documents/{orderId}?&type=order").toString();

        URI_COMPANY_INFO_BY_ID = new StringBuilder().append(propertySources.getTradeshiftAPIDomainName())
                .append("/tradeshift/rest/external/companies/{companyAccountId}").toString();
    }

    /**
     * Get orders page for grid view  by pageNumber, orderLimitPerPage, orderState, sorting by issueDate
     *
     * @param ordersLimitPerPage
     * @param pageNumber
     * @param orderState
     * @return List of orders converted to List<TradeshiftOrderGridRowDTO>
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JSONException 
     */
    @Override
    public List<OrderGridRowDTO> getOrdersPage(Integer ordersLimitPerPage, Integer pageNumber, String orderState)
            throws IOException, SAXException, ParserConfigurationException, JSONException {

        LOGGER.info("get orders page for grid view ");

        if (tokenService.getAccessTokenFromContext() == null || tokenService.getAccessTokenFromContext().isExpired()) {
            tokenService.refreshToken();
        }

        HttpEntity<String> requestEntity = tokenService.getRequestHttpEntityWithAccessToken();
        ResponseEntity<?> responseEntity = restTemplate.exchange(URI_LIST_DOCUMENTS, HttpMethod.GET,
                                                                    requestEntity, String.class, URI_PARAM_DOC_TYPE,
                                                                    ordersLimitPerPage, pageNumber, URI_PARAM_ORDER_BY, orderState);

        return parseDocuments(responseEntity);
    }

    /**
     * Get company name by company id
     * @param companyId
     * @return company name
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws JSONException 
     */
    @Override
    public String getCompanyName(String companyId) throws IOException, SAXException, ParserConfigurationException, JSONException {
        LOGGER.info("get company name by company id ");

        HttpEntity<String> requestEntity = tokenService.getRequestHttpEntityWithAccessToken();
        ResponseEntity<?> responseEntity = restTemplate.exchange(URI_COMPANY_INFO_BY_ID, HttpMethod.GET,
                requestEntity, String.class, companyId);

        return parseCompanyInfo(responseEntity);
    }

    protected String getTextContentFromElement(Element senderElement, String value) {
        return senderElement.getElementsByTagName(value).item(0).getTextContent();
    }

    protected Node getNodeFromElement(Element deliveryElement, String value) {
        return deliveryElement.getElementsByTagName(value).item(0);
    }

    protected Document getDocumentFromResponse(ResponseEntity responseEntity) throws ParserConfigurationException,
            IOException, SAXException {
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return dBuilder.parse(new ByteArrayInputStream(responseEntity.getBody().toString().getBytes()));
    }

    protected Element getElementFromDocument(Document document, String value) {
        return (Element) document.getElementsByTagName(value).item(0);
    }

    /**
     * Convert list of documents from UBL format to list
     *
     * @param responseEntity ResponseEntity with list of documents in the UBL format
     * @return List of documents converted to List<TradeshiftOrderGridRowDTO>
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws JSONException 
     * @throws DOMException 
     */
    private List<OrderGridRowDTO> parseDocuments(ResponseEntity responseEntity)
            throws SAXException, ParserConfigurationException, IOException, DOMException, JSONException {

        LOGGER.info("parse Tradeshift Documents");

        Document document = getDocumentFromResponse(responseEntity);

        NodeList nList = document.getElementsByTagName("ts:Document");

        List<OrderGridRowDTO> documentDTOs = new ArrayList<>();
        for (int i = 0; i < nList.getLength(); i++) {
            String senderCompanyName = "";
            String receivedCompanyName = "";
            Element rootDocumentElement = (Element) nList.item(i);
            String documentId = getTextContentFromElement(rootDocumentElement, "ts:DocumentId");
            String orderId = "#" + getTextContentFromElement(rootDocumentElement, "ts:ID");

            String orderIssueDate = getNodeFromElement(rootDocumentElement, "ts:ItemInfos").getChildNodes().item(7)
                                                                                                    .getTextContent();

            NodeList nodeListSenderCompany = rootDocumentElement.getElementsByTagName("ts:SenderCompanyName");

            if (nodeListSenderCompany != null && nodeListSenderCompany.getLength() > 0) {
                senderCompanyName = nodeListSenderCompany.item(0).getTextContent();
            } else {
                String companyId = tokenService.getCurrentCompanyId();
                senderCompanyName = getCompanyName(companyId);
            }

            NodeList nodeReceivedCompany = rootDocumentElement.getElementsByTagName("ts:ReceivedToCompanyAccountId");

            if (nodeReceivedCompany != null && nodeReceivedCompany.getLength() > 0) {
                receivedCompanyName = getCompanyName(nodeReceivedCompany.item(0).getTextContent());
            }

            Float documentTotal = Float.valueOf(getNodeFromElement(rootDocumentElement, "ts:ItemInfos").getChildNodes().item(3)
                                                                                                        .getTextContent());

            String orderCurrency = getNodeFromElement(rootDocumentElement, "ts:ItemInfos").getChildNodes().item(5)
                                                                                            .getTextContent();

            documentDTOs.add(new OrderGridRowDTO(documentId, orderId, documentTotal, orderIssueDate, senderCompanyName, orderCurrency, receivedCompanyName));
        }

        LOGGER.info("return parsed list of Tradeshift Documents");

        return documentDTOs;
    }

    /**
     * Convert company info from UBL format to list
     *
     * @param responseEntity
     * @return company name
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private String parseCompanyInfo(ResponseEntity responseEntity)
            throws SAXException, ParserConfigurationException, IOException {
        LOGGER.info("parse Tradeshift Company");
        return getDocumentFromResponse(responseEntity).getElementsByTagName("ts:CompanyName").item(0).getTextContent();
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate= restTemplate;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

}
