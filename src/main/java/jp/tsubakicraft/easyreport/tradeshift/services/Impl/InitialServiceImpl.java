package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jp.tsubakicraft.easyreport.tradeshift.common.constants.TsConst;
import jp.tsubakicraft.easyreport.tradeshift.config.ApplicationProperties;
import jp.tsubakicraft.easyreport.tradeshift.services.InitialService;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;
import jp.tsubakicraft.easyreport.util.OAuthUtils;

@Service
public class InitialServiceImpl implements InitialService {

	private static Logger LOGGER = LoggerFactory.getLogger(InitialService.class);
	
	@Autowired
	protected ApplicationProperties applicationProperties;
	
	@Autowired
	protected OAuthUtils oAuthUtils;
	
	@Autowired
	protected TokenService tokenService;
	
	public boolean isLoggedIn(String tsUserId, String tsCompanyAccountId) {
        String tokenTsUserId = tokenService.getCurrentUserId();
        String tokenTsCompanyAccountId = tokenService.getCurrentCompanyId();
        if (!StringUtils.equals(tokenTsCompanyAccountId, tsCompanyAccountId)) {
            return false;
        }

        return StringUtils.equals(tokenTsUserId, tsUserId);
	}
	
	@Override
	public void initUser() {
		
	}

    public JSONObject getUserFromTs(String tsUserId) {
        // Call Trashift APi get user info.
        String userUrl = applicationProperties.getTradeshiftAPIDomainName() + TsConst.TS_GET_USER_URL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userUrl);

        HttpEntity<?> requestEntity;
        try {
            requestEntity = oAuthUtils.getRequestHttpEntity(
                    HttpMethod.GET.name(),
                    builder.toUriString(),
                    MediaType.APPLICATION_JSON_UTF8_VALUE);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<?> responseEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET, requestEntity,
                    String.class);
            // Check the response is compatible media type
            // (MediaType.APPLICATION_JSON)
            HttpHeaders headers = responseEntity.getHeaders();
            if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)
                    || !headers.getContentType().equals(MediaType.APPLICATION_JSON)) {
                LOGGER.warn("Cannot get User");
                return null;
            }

            try {
                // Parse the response to JSON
                JSONObject jsonObject = new JSONObject((String) responseEntity.getBody());
                return jsonObject;
            } catch (Exception e) {
                LOGGER.warn("Cannot get User");
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
