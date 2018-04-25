package jp.tsubakicraft.easyreport.tradeshift.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	
	private static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private static String formatDate(Date date) {
		if(date == null) {
			return null;
		}
		return FORMAT.format(date);
	}
	
	@RequestMapping(value = "/invoice/search", method = RequestMethod.GET)
	public ResponseEntity<?> searchInvoice(
			@RequestParam(value="limit") final Integer limit,
			@RequestParam(value="page") final Integer page,
			@RequestParam(value="stag", required=false) final String stag,
			@RequestParam(value="minIssueDate", required=false) final Date minIssueDate,
			@RequestParam(value="maxIssueDate", required=false) final Date maxIssueDate,
			@RequestParam(value="createdAfter", required=false) final Date createdAfter,
			@RequestParam(value="createdBefore", required=false) final Date createdBefore,
			@RequestParam(value="processStates", required=false) final String[] processStates,
			final HttpServletResponse response
			)  throws JSONException, IOException {
		
		LOGGER.info("get list of invoices by : " + limit + ", " + page + ", " + stag + ", " + minIssueDate + ", " + maxIssueDate + ", " + createdAfter + ", " + createdBefore + ", " + processStates , InvoiceController.class);

		if(tokenService.getAccessTokenFromContext() != null) {
			List<?> result = invoiceRetrievalService.getInvoices(limit, page, stag, formatDate(minIssueDate), formatDate(maxIssueDate), formatDate(createdBefore), formatDate(createdAfter), processStates);
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
