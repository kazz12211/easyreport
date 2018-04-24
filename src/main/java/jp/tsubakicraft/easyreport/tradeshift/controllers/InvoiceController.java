package jp.tsubakicraft.easyreport.tradeshift.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.tsubakicraft.easyreport.tradeshift.services.InvoiceRetrievalService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@RestController
public class InvoiceController {

	static Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	InvoiceRetrievalService invoiceRetrievalService;
	
	@RequestMapping(value = "/invoice/search", method = RequestMethod.GET)
	public ResponseEntity<?> searchInvoice(
			@RequestParam(value="limit") final Integer limit,
			@RequestParam(value="page") final Integer page,
			@RequestParam(value="stag", required=false) final String stag,
			@RequestParam(value="minissuedate", required=false) final String minIssueDate,
			@RequestParam(value="maxissuedate", required=false) final String maxIssueDate,
			@RequestParam(value="createdAfter", required=false) final String createdAfter,
			@RequestParam(value="createdBefore", required=false) final String createdBefore,
			@RequestParam(value="processState", required=false) final String[] processStates,
			final HttpServletResponse response
			)  throws JSONException, IOException {
		
		LOGGER.info("get list of invoices by : " + limit + ", " + page + ", " + stag + ", " + minIssueDate + ", " + maxIssueDate + ", " + createdAfter + ", " + createdBefore + ", " + processStates , InvoiceController.class);

		if(tokenService.getAccessTokenFromContext() != null) {
			List<?> result = invoiceRetrievalService.getInvoices(limit, page, stag, minIssueDate, maxIssueDate, createdBefore, createdAfter, processStates);
			return new ResponseEntity(result, HttpStatus.OK);
		} else {
			LOGGER.info("failed to get list of invoice, access token doesn't exist.", InvoiceController.class);
			response.sendRedirect(tokenService.getAuthorizationCodeURL());
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/invoice/download", method = RequestMethod.GET)
	public ResponseEntity<?> downloadInvoice(@RequestParam("id") String docId) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("requestedDocId", docId);
		return new ResponseEntity(result, HttpStatus.NOT_IMPLEMENTED);
	}

}