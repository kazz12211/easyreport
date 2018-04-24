package jp.tsubakicraft.easyreport.tradeshift.services;

import java.util.List;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import jp.tsubakicraft.easyreport.tradeshift.domain.dto.InvoiceDTO;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.InvoiceDetailDTO;

@Service
public interface InvoiceRetrievalService {

	List<InvoiceDTO> getInvoices(
			Integer limit, 
			Integer page, 
			String stag, 
			String minIssueDate, 
			String maxIssueDate, 
			String createdBefore, 
			String createdAfter, 
			String[] processStates) throws JSONException;
	

	InvoiceDetailDTO getInvoiceDetail(String invoiceId);

}
