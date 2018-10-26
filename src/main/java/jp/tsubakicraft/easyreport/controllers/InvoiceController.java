package jp.tsubakicraft.easyreport.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

import jp.tsubakicraft.easyreport.domain.dto.InvoiceDetailDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoicePageDTO;
import jp.tsubakicraft.easyreport.services.InvoiceRetrievalService;
import jp.tsubakicraft.easyreport.util.DateTimeUtil;
import jp.tsubakicraft.tradeshift.config.ApplicationProperties;
import jp.tsubakicraft.tradeshift.services.TokenService;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

	static Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	InvoiceRetrievalService invoiceRetrievalService;
	
    @Autowired
    protected ApplicationProperties applicationProperties;

	
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public ResponseEntity<?> searchInvoice(
			@RequestParam(value="limit") final Integer limit,
			@RequestParam(value="page") final Integer page,
			@RequestParam(value="stag", required=false) final String stag,
			@RequestParam(value="minIssueDate", required=false) final String minIssueDate,
			@RequestParam(value="maxIssueDate", required=false) final String maxIssueDate,
			@RequestParam(value="createdAfter", required=false) final String createdAfter,
			@RequestParam(value="createdBefore", required=false) final String createdBefore,
			@RequestParam(value="processStates", required=false) final String[] processStates,
			@RequestParam(value="tzOffset", required=false) final int tzOffset,
			final HttpServletResponse response
			)  throws JSONException, IOException {
		
		LOGGER.debug("get list of invoices by : " + limit + ", " + page + ", " + stag + ", " + minIssueDate + ", " + maxIssueDate + ", " + createdAfter + ", " + createdBefore + ", " + processStates + ", " + tzOffset , InvoiceController.class);

		if(tokenService.getAccessTokenFromContext() != null) {
			try {
				InvoicePageDTO result = invoiceRetrievalService.getInvoices(
						limit, 
						page, 
						stag, 
						DateTimeUtil.toDateString(minIssueDate, tzOffset), 
						DateTimeUtil.toDateString(maxIssueDate, tzOffset), 
						DateTimeUtil.toDateTimeString(createdBefore, tzOffset), 
						DateTimeUtil.toDateTimeString(createdAfter, tzOffset), 
						processStates);
				return new ResponseEntity(result, HttpStatus.OK);
			} catch (Exception e) {
				LOGGER.error("Server error", e);
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOGGER.info("failed to get list of invoice, access token doesn't exist.", InvoiceController.class);
			response.sendRedirect(tokenService.getAuthorizationCodeURL());
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public ResponseEntity<?> downloadInvoice(@RequestParam("id") String docId, final HttpServletResponse response) throws IOException, ParserConfigurationException, SAXException, JSONException {
		if(tokenService.getAccessTokenFromContext() != null) {
			try {
				InvoiceDetailDTO invoice = invoiceRetrievalService.getInvoiceDetail(docId);
				return new ResponseEntity(invoice, HttpStatus.OK);
			} catch (Exception e) {
				LOGGER.error("Server error", e);
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOGGER.info("failed to get list of invoice, access token doesn't exist.", InvoiceController.class);
			response.sendRedirect(tokenService.getAuthorizationCodeURL());
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
		/*
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("requestedDocId", docId);
		return new ResponseEntity(result, HttpStatus.NOT_IMPLEMENTED);
		*/
	}

}
