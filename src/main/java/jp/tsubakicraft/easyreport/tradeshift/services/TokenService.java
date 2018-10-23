package jp.tsubakicraft.easyreport.tradeshift.services;

import java.text.ParseException;

import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import jp.tsubakicraft.easyreport.tradeshift.domain.SessionData;
import jp.tsubakicraft.easyreport.tradeshift.domain.dto.JwtDTO;

@Service
public interface TokenService {

    String getAuthorizationCodeURL();

    OAuth2AccessToken getAccessTokenByAuthCode(String authorizationCode) throws ParseException, JSONException;

    void refreshToken() throws JSONException;

    OAuth2AccessToken refreshToken(OAuth2AccessToken accessToken) throws JSONException;

    OAuth2AccessToken getAccessTokenFromContext();

    HttpEntity<?> getRequestHttpEntityWithAccessToken() throws JSONException;

    HttpEntity<?> getRequestHttpEntityWithAccessToken(String acceptType) throws JSONException;

    HttpEntity<?> getRequestHttpEntityWithAccessToken(OAuth2AccessToken accessToken, String contentType, String acceptType) throws JSONException;

    String getCurrentUserId();

    String getCurrentCompanyId();

    JwtDTO JwtDTO();

    void setAccessToken(OAuth2AccessToken accessToken);

    void setJwtDTO(JwtDTO jwtDTO);

    void logout();

    void setSessionData(SessionData sessionData);

    HttpEntity<?> getRequestHttpEntityWithAccessToken(MediaType mediaType) throws JSONException;
}
