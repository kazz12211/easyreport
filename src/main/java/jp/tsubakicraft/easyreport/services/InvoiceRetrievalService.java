package jp.tsubakicraft.easyreport.services;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import jp.tsubakicraft.easyreport.domain.dto.InvoiceDetailDTO;
import jp.tsubakicraft.easyreport.domain.dto.InvoicePageDTO;

@Service
public interface InvoiceRetrievalService {

	InvoicePageDTO getInvoices(
			Integer limit, 
			Integer page, 
			String stag, 
			String minIssueDate, 
			String maxIssueDate, 
			String createdBefore, 
			String createdAfter, 
			String[] processStates) throws JSONException;
	

	InvoiceDetailDTO getInvoiceDetail(String invoiceId) throws ParserConfigurationException, SAXException, IOException, JSONException;

}
