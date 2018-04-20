package jp.tsubakicraft.easyreport.tradeshift.services;

import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import jp.tsubakicraft.easyreport.tradeshift.domain.dto.JwtDTO;

@Service
public interface TokenService {

    String getAuthorizationCodeURL();

    OAuth2AccessToken getAccessTokenByAuthCode(String authorizationCode) throws ParseException, JSONException;

    void refreshToken() throws JSONException;

    OAuth2AccessToken getAccessTokenFromContext();

    HttpEntity getRequestHttpEntityWithAccessToken() throws JSONException;

    HttpEntity getRequestHttpEntityWithAccessToken(String acceptType) throws JSONException;

    String getCurrentUserId() throws ParserConfigurationException;

    String getCurrentCompanyId();

    JwtDTO JwtDTO();

    void setAccessToken(OAuth2AccessToken accessToken);

    HttpEntity getRequestHttpEntityWithAccessToken(MediaType mediaType) throws JSONException;

}
