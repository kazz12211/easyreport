package jp.tsubakicraft.easyreport.tradeshift.services;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import jp.tsubakicraft.easyreport.tradeshift.domain.dto.OrderGridRowDTO;

@Service
public interface OrderRetrievalService {
	
    List<OrderGridRowDTO> getOrdersPage(Integer ordersLimitPerPage, Integer pageNumber, String orderState)
            throws ParserConfigurationException, IOException, SAXException, JSONException;

    String getCompanyName(String companyId) throws IOException, SAXException, ParserConfigurationException, JSONException;

}
