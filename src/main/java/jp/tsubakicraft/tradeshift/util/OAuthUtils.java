package jp.tsubakicraft.tradeshift.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

import jp.tsubakicraft.tradeshift.common.constants.TsConst;
import jp.tsubakicraft.tradeshift.config.ApplicationProperties;
import jp.tsubakicraft.tradeshift.domain.dto.JwtDTO;
import jp.tsubakicraft.tradeshift.services.TokenService;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.signature.OAuthSignatureMethod;


@Component
public class OAuthUtils {

    static Logger LOGGER = LoggerFactory.getLogger(OAuthUtils.class);

    @Autowired
    TokenService tokenService;

    @Autowired
    ApplicationProperties applicationProperties;

    public boolean isAuthenticated() {
        if(StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            return tokenService.JwtDTO() != null;
        } else {
            return tokenService.getAccessTokenFromContext() != null;
        }
    }

    public void checkAuthenticated() throws JSONException, IOException, SAXException, ParserConfigurationException {
        if(!isAuthenticated()) {
            if(StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
                doOauth1();
            } else {
                throw new RuntimeException();
            }
        }
    }

    public String getAuthorizationCodeURL() {
        if(StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            return applicationProperties.getAppDomain();
        } else {
            return tokenService.getAuthorizationCodeURL();
        }
    }

    /**
     * do oauth 1 and store user info to session
     * @throws JSONException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void doOauth1() throws JSONException, IOException, SAXException, ParserConfigurationException {
        // Call Trashift APi get user info.
        String userUrl = applicationProperties.getTradeshiftAPIDomainName() + TsConst.TS_GET_USER_URL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userUrl);

        LOGGER.info("URI to request: " + builder.toUriString());

        HttpEntity<?> requestEntity = this.getRequestHttpEntity(
                HttpMethod.GET.name(),
                builder.toUriString(),
                MediaType.APPLICATION_JSON_UTF8_VALUE);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET, requestEntity,
                String.class);
        tokenService.setJwtDTO(this.parseJwtDTO(responseEntity));
    }

    /**
     * get OAuth Message
     * @param method
     * @param url
     * @return
     */
    public OAuthMessage getOAuthMessage(String method, String url) {
        ApplicationProperties oauthProps = this.applicationProperties;

        OAuthMessage om = new OAuthMessage(method, url, null);
        om.addParameter(OAuth.OAUTH_CONSUMER_KEY, oauthProps.getConsumerKey());
        om.addParameter(OAuth.OAUTH_SIGNATURE_METHOD, oauthProps.getSignatureMethod());
        om.addParameter(OAuth.OAUTH_VERSION, oauthProps.getVersion());
        om.addParameter(OAuth.OAUTH_TIMESTAMP, new Long((new Date().getTime()) / 1000).toString());
        om.addParameter(OAuth.OAUTH_NONCE, oauthProps.getNonce());
        om.addParameter(OAuth.OAUTH_TOKEN, oauthProps.getAccessToken());

        OAuthConsumer oc = new OAuthConsumer(null, oauthProps.getConsumerKey(),
                applicationProperties.getConsumerSecret(), null);
        OAuthAccessor oAuthAccessor = new OAuthAccessor(oc);
        oAuthAccessor.accessToken = oauthProps.getAccessToken();
        oAuthAccessor.tokenSecret = oauthProps.getTokenSecret();

        try {
            OAuthSignatureMethod osm = OAuthSignatureMethod.newMethod(oauthProps.getSignatureMethod(),
                    oAuthAccessor);
            osm.setTokenSecret(oauthProps.getTokenSecret());
            osm.sign(om);

            return om;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get Request Http Entity
     * @param method
     * @param url
     * @param acceptType
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public HttpEntity<?> getRequestHttpEntity(String method, String url, String acceptType)
            throws JSONException, IOException {
        if (StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            OAuthMessage osAuthMessage = getOAuthMessage(method, url);

            if (osAuthMessage == null) {
                throw new OAuthException("Unauthorized, Can not get oauth message.");
            }

            StringBuilder sbAuthorization = new StringBuilder();
            sbAuthorization.append("OAuth oauth_consumer_key=").append("\"" + osAuthMessage.getConsumerKey() + "\"");
            sbAuthorization.append(",oauth_token=")
                    .append("\"" + UrlEncoded.encodeString(osAuthMessage.getToken()) + "\"");
            sbAuthorization.append(",oauth_signature_method=").append("\"" + osAuthMessage.getSignatureMethod() + "\"");
            sbAuthorization.append(",oauth_timestamp=").append("\"")
                    .append(osAuthMessage.getParameter("oauth_timestamp")).append("\"");
            sbAuthorization.append(",oauth_nonce=").append("\"" + osAuthMessage.getParameter("oauth_nonce") + "\"");
            sbAuthorization.append(",oauth_version=").append("\"" + osAuthMessage.getParameter("oauth_version") + "\"");
            sbAuthorization.append(",oauth_signature=")
                    .append("\"" + UrlEncoded.encodeString(osAuthMessage.getSignature()) + "\"");

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Authorization", sbAuthorization.toString());
            requestHeaders.add("Content-Type", "application/json");
            requestHeaders.add("Accept", "application/json");

            return new HttpEntity<>(requestHeaders);
        } else {
            return tokenService.getRequestHttpEntityWithAccessToken(acceptType);
        }
    }

    /**
     * Get Request Http Entity
     * @param method
     * @param url
     * @param acceptType
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public HttpEntity<?> getRequestHttpEntity(String method, String url, Object body, String contentType, String acceptType)
            throws JSONException, IOException {
        if (StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            OAuthMessage osAuthMessage = getOAuthMessage(method, url);

            if (osAuthMessage == null) {
                throw new OAuthException("Unauthorized, Can not get oauth message.");
            }

            StringBuilder sbAuthorization = new StringBuilder();
            sbAuthorization.append("OAuth oauth_consumer_key=").append("\"" + osAuthMessage.getConsumerKey() + "\"");
            sbAuthorization.append(",oauth_token=")
                    .append("\"" + UrlEncoded.encodeString(osAuthMessage.getToken()) + "\"");
            sbAuthorization.append(",oauth_signature_method=").append("\"" + osAuthMessage.getSignatureMethod() + "\"");
            sbAuthorization.append(",oauth_timestamp=").append("\"")
                    .append(osAuthMessage.getParameter("oauth_timestamp")).append("\"");
            sbAuthorization.append(",oauth_nonce=").append("\"" + osAuthMessage.getParameter("oauth_nonce") + "\"");
            sbAuthorization.append(",oauth_version=").append("\"" + osAuthMessage.getParameter("oauth_version") + "\"");
            sbAuthorization.append(",oauth_signature=")
                    .append("\"" + UrlEncoded.encodeString(osAuthMessage.getSignature()) + "\"");

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Authorization", sbAuthorization.toString());
            requestHeaders.add("Content-Type", contentType);
            requestHeaders.add("Accept", acceptType);

            return new HttpEntity<>(body, requestHeaders);
        } else {
            HttpEntity<?> httpEntity = tokenService.getRequestHttpEntityWithAccessToken(acceptType);
            return new HttpEntity<>(body, httpEntity.getHeaders());
        }
    }

    /**
     * Get Request Http Entity
     * @param method
     * @param url
     * @param acceptType
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public HttpEntity<?> getAsyncRequestHttpEntity(String method, String url, Object body, String contentType, String acceptType, OAuth2AccessToken accessToken)
            throws JSONException, IOException {
        if (StringUtils.equalsIgnoreCase(applicationProperties.getOauthType(), "1")) {
            return getRequestHttpEntity(method, url, body, contentType, acceptType);
        } else {
            HttpEntity<?> httpEntity = tokenService.getRequestHttpEntityWithAccessToken(accessToken, contentType, acceptType);
            return new HttpEntity<>(body, httpEntity.getHeaders());
        }
    }

    public OAuth2AccessToken refreshAccessToken(OAuth2AccessToken accessToken) {
        if(accessToken != null && accessToken.isExpired()) {
            try {
                accessToken = tokenService.refreshToken(accessToken);
            } catch (JSONException e) {
            }
        }
        return accessToken;
    }


    /**
     * parse JwtDTO for oauth1
     * @param responseEntity
     * @return JwtDTO
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws JSONException
     */
    private JwtDTO parseJwtDTO(ResponseEntity<?> responseEntity)
            throws SAXException, ParserConfigurationException, IOException, JSONException {

        // Check the response is compatible media type
        // (MediaType.APPLICATION_JSON)
        HttpHeaders headers = responseEntity.getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)
                || !headers.getContentType().equals(MediaType.APPLICATION_JSON)) {
            return null;
        }

        JwtDTO jwtDTO = new JwtDTO();

        // Parse the response to JSON
        JSONObject jsonObject = new JSONObject((String) responseEntity.getBody());

        jwtDTO.setUserId(jsonObject.getString("Id"));
        jwtDTO.setCompanyId(jsonObject.getString("CompanyAccountId"));

        return jwtDTO;
    }
}
