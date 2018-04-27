package jp.tsubakicraft.easyreport.tradeshift.services.Impl;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.nimbusds.jwt.PlainJWT;

import jp.tsubakicraft.easyreport.tradeshift.config.PropertySources;
import jp.tsubakicraft.easyreport.tradeshift.config.authentication.CustomAuthenticationProvider;
import jp.tsubakicraft.easyreport.tradeshift.domain.SessionData;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.JwtDTO;
import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

    static Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final String CONTENT_TYPE_WEB_FORM = "application/x-www-form-urlencoded";
    private final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String HEADER_AUTHORIZATION_TYPE = "Basic ";
    private final String HEADER_CHAR_SET_TYPE = "utf-8";
    private final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private final String AUTHORIZE_URL;
    private final String ACCESS_TOKEN_URI;
    private final String REDIRECT_URI;
    private final String CLIENT_ID;
    private final String CLIENT_SECRET;

    private PropertySources propertySources;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    /**
     * Inject PropertySources bean by constructor,
     * Init AUTHORIZE_URL, ACCESS_TOKEN_URI, CLIENT_ID, REDIRECT_URI, CLIENT_SECRET
     *
     * @param propertySources
     */
    @Autowired
    public TokenServiceImpl(@Qualifier("propertySources") PropertySources propertySources) {
        super();
        this.propertySources = propertySources;

        this.AUTHORIZE_URL = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/auth/login?response_type=code";
        this.ACCESS_TOKEN_URI = propertySources.getTradeshiftAPIDomainName() + "/tradeshift/auth/token";
        this.CLIENT_ID = propertySources.getClientID();
        this.REDIRECT_URI = propertySources.getRedirectUri();
        this.CLIENT_SECRET = propertySources.getClientSecret();
        
    }

    /**
     * Get authorization server url for to get authorization code
     *
     * @return URL for oauth2 authorization
     */
    @Override
    public String getAuthorizationCodeURL() {
        LOGGER.info("get authorization url", TokenServiceImpl.class);

        String authorizationCodeURL = AUTHORIZE_URL + "&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&scope=offline";

        return authorizationCodeURL;
    }

    /**
     * Receive oauth2 token from authentication server and store it in the session context
     *
     * @param authorizationCode from authentication server
     * @return oauth2 token
     * @throws JSONException 
     * @throws IOException
     */
    @Override
    public OAuth2AccessToken getAccessTokenByAuthCode(String authorizationCode) throws ParseException, JSONException {
        LOGGER.info("get oauth2 access token", TokenServiceImpl.class);

        OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, ACCESS_TOKEN_URI);
        oAuthRequest.addHeader("Content-Type", HEADER_CONTENT_TYPE);
        oAuthRequest.addHeader("Authorization", HEADER_AUTHORIZATION_TYPE + Base64.encodeBase64String(new String(CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));
        oAuthRequest.setCharset(HEADER_CHAR_SET_TYPE);
        oAuthRequest.addBodyParameter("grant_type", AUTHORIZATION_CODE_GRANT_TYPE);
        oAuthRequest.addBodyParameter("code", authorizationCode);

        LOGGER.info("send request for access token", TokenServiceImpl.class);
        String accessTokenResponse = oAuthRequest.send().getBody();
        JSONObject responseJson;
        responseJson = new JSONObject(accessTokenResponse);

        DefaultOAuth2AccessToken accessToken = parseAccessToken(responseJson, 30000);
        accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(parseJsonField(responseJson, "refresh_token")));
        this.sessionData.setAccessToken(accessToken);
        this.sessionData.setRefreshToken(accessToken.getRefreshToken());
        this.sessionData.setJwtDTO(parseJWTToken(responseJson));

        return accessToken;
    }


    /**
     * Obtain access token by refresh token
     * @throws JSONException 
     */
    @Override
    public void refreshToken() throws JSONException {

        if (this.sessionData.getRefreshToken() != null) {
            OAuth2RefreshToken tempRefreshToken = this.sessionData.getRefreshToken();
            OAuthRequest oAuthRequest = new OAuthRequest(Verb.POST, ACCESS_TOKEN_URI);
            oAuthRequest.addHeader("Content-Type", CONTENT_TYPE_WEB_FORM);
            oAuthRequest.addHeader("Authorization", HEADER_AUTHORIZATION_TYPE + Base64.encodeBase64String(new String(CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));
            oAuthRequest.setCharset(HEADER_CHAR_SET_TYPE);
            oAuthRequest.addBodyParameter("grant_type", REFRESH_TOKEN_GRANT_TYPE);
            oAuthRequest.addBodyParameter("refresh_token", getAccessTokenFromContext().getRefreshToken().getValue());
            oAuthRequest.addBodyParameter("scope", CLIENT_ID + "." + propertySources.getTradeshiftAppVersion());

            LOGGER.info("send request for access token by refresh token", TokenServiceImpl.class);
            String refreshTokenResponse = oAuthRequest.send().getBody();
            JSONObject responseJson;

            if (refreshTokenResponse != null) {
                responseJson = new JSONObject(refreshTokenResponse);
                Object accessToken = new JSONObject(refreshTokenResponse).get("access_token");
                if (accessToken != null) {
                    this.sessionData.setAccessToken(parseAccessToken(responseJson, 1000));

                    ((DefaultOAuth2AccessToken) this.sessionData.getAccessToken()).setRefreshToken(tempRefreshToken);
                } else {
                    LOGGER.warn("failed to get authorization token by refresh token", TokenServiceImpl.class);
                }
            } else {
                LOGGER.warn("failed to get authorization token by refresh token", TokenServiceImpl.class);
            }
        } else {

            LOGGER.error("Refresh token doesn't exist", TokenServiceImpl.class);
            customAuthenticationProvider.logout();
            throw new OAuthException("Unauthorized, Refresh token doesn't exist");
        }
    }

    /**
     * Get Access Token from session context
     *
     * @return OAuth2AccessToken
     */
    @Override
    public OAuth2AccessToken getAccessTokenFromContext() {
        LOGGER.info("get oauth2 access token from session context", TokenServiceImpl.class);

        return this.sessionData.getAccessToken();
    }

    /**
     * Get HttpEntity that contains HttpHeader with Access Token
     *
     * @return request HttpEntity with Access Token
     * @throws JSONException 
     */
    @Override
    public HttpEntity getRequestHttpEntityWithAccessToken() throws JSONException {
        LOGGER.info("get HttpEntity with Access Token", TokenServiceImpl.class);

        if (getAccessTokenFromContext().isExpired()) {
            refreshToken();
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + getAccessTokenFromContext().getValue());
        requestHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_XML_VALUE);
        return new HttpEntity<>(requestHeaders);
    }

    @Override
    public HttpEntity getRequestHttpEntityWithAccessToken(MediaType mediaType) throws JSONException {
        LOGGER.info("get HttpEntity with Access Token", TokenServiceImpl.class);

        if (getAccessTokenFromContext().isExpired()) {
            refreshToken();
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + getAccessTokenFromContext().getValue());
        requestHeaders.add(HttpHeaders.ACCEPT, mediaType.getType());
        //requestHeaders.add(HttpHeaders.ACCEPT_CHARSET, "utf-8");

        return new HttpEntity<>(requestHeaders);
    }

    /**
     * Get HttpEntity that contains HttpHeader with Access Token
     *
     * @return request HttpEntity with Access Token
     * @throws JSONException 
     */
    @Override
    public HttpEntity getRequestHttpEntityWithAccessToken(String acceptType) throws JSONException {
        LOGGER.info("get HttpEntity with Access Token", TokenServiceImpl.class);
        if (getAccessTokenFromContext().isExpired()) {
            refreshToken();
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + getAccessTokenFromContext().getValue());
        if (acceptType != null) {
            requestHeaders.add("Accept", acceptType);
        }
        HttpEntity requestEntity = new HttpEntity(requestHeaders);

        return requestEntity;
    }

    /**
     * Get current user Id from session context
     *
     * @return userId
     */
    @Override
    public String getCurrentUserId() {
        return this.sessionData.getJwtDTO().getUserId();
    }

    @Override
    public String getCurrentCompanyId() {
        return this.sessionData.getJwtDTO().getCompanyId();
    }

    /**
     * Get tradeshift JWT token info
     *
     * @return JwtDTO
     */
    @Override
    public JwtDTO JwtDTO() {
        return sessionData.getJwtDTO();
    }

    @Override
    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.sessionData.setAccessToken(accessToken);
    }

    private JwtDTO parseJWTToken(JSONObject responseJson) throws ParseException, JSONException {
        String idToken = parseJsonField(responseJson, "id_token");
        JwtDTO jwtToken = new JwtDTO();
        net.minidev.json.JSONObject jwtJson = PlainJWT.parse(idToken).getPayload().toJSONObject();
        jwtToken.setOriginalJWTToken(idToken);
        jwtToken.setUserId(parseJwtField(jwtJson, "userId"));
        jwtToken.setTradeshiftUserEmail(parseJwtField(jwtJson, "sub"));
        jwtToken.setAppName(parseJwtField(jwtJson, "aud"));
        jwtToken.setCompanyId(parseJwtField(jwtJson, "companyId"));
        jwtToken.setIss(parseJwtField(jwtJson, "iss"));
        jwtToken.setExpirationTime(Long.valueOf(parseJwtField(jwtJson, "exp")));
        jwtToken.setIssuedAtTime(Long.valueOf(parseJwtField(jwtJson, "iat")));
        jwtToken.setJwtUniqueIdentifier(parseJwtField(jwtJson, "jti"));
        return jwtToken;
    }

    private String parseJsonField(JSONObject responseJson, String field) throws JSONException {
        Object parsedField = responseJson.get(field);
        if (parsedField == null) {
            throw new NullPointerException(String.format("The following field was missing from the response JSON: [%s]", field));
        }
        return parsedField.toString();
    }

    private String parseJwtField(net.minidev.json.JSONObject jwtJson, String field) {
        Object parsedField = jwtJson.get(field);
        if (parsedField == null) {
            throw new NullPointerException(String.format("The following field was missing from the JWT token: [%s]", field));
        }
        return parsedField.toString();
    }

    private DefaultOAuth2AccessToken parseAccessToken(JSONObject responseJson, int expireTime) throws JSONException {
        DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken(parseJsonField(responseJson, "access_token"));
        accessToken.setExpiration(getExpirationTime(parseJsonField(responseJson, "expires_in"), expireTime));
        return accessToken;
    }

    private Date getExpirationTime(String expiresIn, int expireTime) {
        return new Date(System.currentTimeMillis() + (Long.valueOf(expiresIn) * expireTime));
    }

}
