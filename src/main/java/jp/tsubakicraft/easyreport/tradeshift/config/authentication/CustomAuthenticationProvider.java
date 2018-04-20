package jp.tsubakicraft.easyreport.tradeshift.config.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jp.tsubakicraft.easyreport.tradeshift.services.TokenService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!authentication.isAuthenticated()) {
            if (authentication.getCredentials() != null) {
                List<GrantedAuthority> grantedAuths = new ArrayList<>();
                grantedAuths.add(new SimpleGrantedAuthority("BASE_USER"));
                Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getName(), authentication
                        .getCredentials().toString(), grantedAuths);
                SecurityContextHolder.getContext().setAuthentication(auth);

                return auth;
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        tokenService.setAccessToken(null);
    }

}
