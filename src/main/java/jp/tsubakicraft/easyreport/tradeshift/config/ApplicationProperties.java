package jp.tsubakicraft.easyreport.tradeshift.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("serial")
@Configuration
public class ApplicationProperties implements Serializable {
	
	private static final int DEFAULT_FETCH_LIMIT = 100;

	@Value("${invoice.fetchLimit}")
	private String invoiceFetchLimit;
	
	public int getInvoiceFetchLimit() {
		try {
			int limit = Integer.parseInt(invoiceFetchLimit);
			return limit;
		} catch (Exception e) {
			return DEFAULT_FETCH_LIMIT;
		}
	}
}
