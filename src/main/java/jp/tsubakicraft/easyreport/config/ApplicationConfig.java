package jp.tsubakicraft.easyreport.config;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("serial")
@Configuration
public class ApplicationConfig implements Serializable {

	private static Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);
	FetchLimits fetchLimits;
	
	@Autowired
	protected ApplicationConfig(@Value("${fetchLimit.invoice}") String invoiceFetchLimit) {
		LOGGER.info("fetchLimit.invoice = " + invoiceFetchLimit);
		fetchLimits = new FetchLimits();
		try {
			int limit = Integer.parseInt(invoiceFetchLimit);
			fetchLimits.setInvoice(limit);
		} catch (Exception e) {
			fetchLimits.setInvoice(100);
		}
	}
	
	public FetchLimits getFetchLimits() {
		return fetchLimits;
	}
	
}
