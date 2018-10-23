package jp.tsubakicraft.tradeshift.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

import jp.tsubakicraft.tradeshift.domain.dto.JwtDTO;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData {
	
    private OAuth2AccessToken accessToken;
    private JwtDTO jwtDTO;
    private OAuth2RefreshToken refreshToken;

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public JwtDTO getJwtDTO() {
        return jwtDTO;
    }

    public void setJwtDTO(JwtDTO jwtDTO) {
        this.jwtDTO = jwtDTO;
    }

    public OAuth2RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(OAuth2RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

}
