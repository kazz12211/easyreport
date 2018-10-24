package jp.tsubakicraft.easyreport.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.tsubakicraft.easyreport.config.ApplicationConfig;
import jp.tsubakicraft.easyreport.domain.dto.FetchLimitDTO;

@Controller
@RequestMapping("/init")
public class InitialController {

	static Logger LOGGER = LoggerFactory.getLogger(InitialController.class);
	
    @Autowired
    protected ApplicationConfig applicationConfig;


    @RequestMapping(value="/params", method=RequestMethod.GET, produces="application/json; charset=utf-8")
    public ResponseEntity<?> getParam(final HttpServletResponse response) throws JSONException, IOException {
    	LOGGER.debug("getting app parameters", InitialController.class);
    	FetchLimitDTO fetchLimits = new FetchLimitDTO();
    	fetchLimits.setInvoice(applicationConfig.getFetchLimits().getInvoice());
    	return new ResponseEntity(fetchLimits, HttpStatus.OK);
    	
    }

}
