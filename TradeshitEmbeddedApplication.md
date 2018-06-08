## Tradeshift Embedded Application with OAuth2 authentication

## Development environment

- Eclipse Neon version 4.6.3 with Spring IDE version 3.9.4
- Java 1.8
- Spring Boot 1.5.12 or 1.5.13
- AngularJS 1.6.2
- Maven
- etc.

See pom.xml for appropriate libraries for the project

Caution: This application template does not work with Spring Boot 2.

### Typical file layout of Spring Boot Maven project (Eclipse IDE)

Spring IDE generates source tree automatically. You should add your application source files under src/main/java and src/main/resources.

~~~
YourApplication/
+ src
  + main
    + java
      + yournamespace
        + yourapplication
          + tradeshift
            + config
              + authentication
                CustomAuthenticationProvider.java
              + filters
                CsrfHeaderFilter.java
              PropertySources.java
              SecurityConfig.java
            + controllers
              TokenController.java
              ... your application controller classes
            + domain
              + dto
                JwtDTO.java
                ... your application data classes
              + enums
                DocState.java
                DocType.java
              SessionData.java
            + services
              + Impl
                TokenServiceImpl.java
              TokenService.java
              ... your application serviec classes
          YourApplication.java
    + resources
      + static
        + images
          ... icons, gaphics, logos
        + scripts
          app.js (AngularJS application module)
          controller.js (AngularJS controller)
        + strings
          en.json
          ja.json
          ... other i18n strings
        + styles
          main.css (Your custom styles)
        index.html
      application.properties
  + test
    + java
      + yournamespace
        + yourapplication
          YourApplicationTests.java
+ target
+ .mvn
+ .settings
Procfile (Runner for heroku)
mvnw
mvnw.cmd
pom.xml
.classpath
.project

~~~

### pom.xml

~~~
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>yournamespace</groupId>
	<artifactId>yourapplication</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>yourapplication</name>
	<description>Tradesfhit application template</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.12.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<version>1.5.7.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security.oauth</groupId>
			<artifactId>spring-security-oauth2</artifactId>
			<version>2.0.14.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jetty</artifactId>
			<version>1.5.1.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-actuator</artifactId>
			<version>1.5.7.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.json</groupId>
			<artifactId>json</artifactId>
			<version>20140107</version>
		</dependency>
		<dependency>
			<groupId>com.jayway.jsonpath</groupId>
			<artifactId>json-path</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>com.jayway.jsonpath</groupId>
			<artifactId>json-path-assert</artifactId>
			<version>2.0.0</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>com.nimbusds</groupId>
			<artifactId>nimbus-jose-jwt</artifactId>
			<version>4.34.2</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>log4j-over-slf4j</artifactId>
		</dependency>
		<dependency>
			<groupId>org.scribe</groupId>
			<artifactId>scribe</artifactId>
			<version>1.3.7</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>angularjs</artifactId>
			<version>1.6.2</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>angular-translate</artifactId>
			<version>2.13.1</version>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>
				angular-translate-loader-static-files
			</artifactId>
			<version>2.13.1</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>


</project>
~~~

### YourApplication.java

Spring Boot application class

~~~
package yournamespace.yourapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("yournamespace.yourapplication")
public class YourApplication {

	public static void main(String[] args) {
		SpringApplication.run(YourApplication.class, args);
	}
}

~~~

### PropertySources.java

When deployed on heroku, the application settings are read from system environment variables which you set heroku admin console. (DO NOT CHANGE)

~~~
package yournamespace.yourapplication.tradeshift.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration(value = "propertySources")
public class PropertySources {

    private String tradeshiftAPIDomainName;
    private String clientID;
    private String clientSecret;
    private String redirectUri;
    private String tradeshiftAppVersion;

    @PostConstruct
    public void PropertySourcesInit() {
        this.tradeshiftAppVersion = System.getenv("tradeshiftAppVersion");
        this.tradeshiftAPIDomainName = System.getenv("tradeshiftAPIDomainName");
        this.clientID = System.getenv("clientID");
        this.clientSecret = System.getenv("clientSecret");
        this.redirectUri = System.getenv("redirectUri");
    }

    public String getTradeshiftAppVersion() {
        return tradeshiftAppVersion;
    }

    public String getTradeshiftAPIDomainName() {
        return tradeshiftAPIDomainName;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

}
~~~

### SecurityConfig.java

~~~
package yournamespace.yourapplication.tradeshift.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Allow to change security configuration
     *
     * Disable default security configuration
     * Disable default header frame options
     * Disable csrf protection
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .headers().frameOptions().disable()
                .and().csrf().disable();

    }

}
~~~

### CustomAuthenticationProvider.java

~~~
package yournamespace.yourapplication.tradeshift.config.authentication;

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

import yournamespace.yourapplication.tradeshift.services.TokenService;

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
~~~

### CsrfHeaderFilter.java

~~~
package yournamespace.yourapplication.tradeshift.config.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

public class CsrfHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
            String token = csrf.getToken();
            if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                cookie = new Cookie("XSRF-TOKEN", token);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }

}
~~~

### TokenController.java

This controller handles Tradeshift OAuth2 authentication. (DO NOT CHANGE)

~~~
package yournamespace.yourapplication.tradeshift.controllers;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import yournamespace.yourapplication.tradeshift.services.TokenService;

@Controller
public class TokenController {

    static Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    TokenService tokenService;


    /**
     * If Access Token dos't exist in session context,
     * redirecting to authorization server to get authorization code for Access Token
     *
     * @return Return to home page
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAuthorizationCode() throws IOException {
        if (tokenService.getAccessTokenFromContext() == null) {
            LOGGER.info("redirect to the authorization server", TokenController.class);

            return "redirect:" + tokenService.getAuthorizationCodeURL();
        }
        LOGGER.info("return to index.html", TokenController.class);

        return "index.html";
    }

    /**
     * Receive authorization code from authorization server for get Access Token from authorization server
     * and redirect to the home page after that
     *
     * @param code authorization code from authorization server
     * @return Return to home page
     * @throws IOException
     */
    @RequestMapping(value = "/oauth2/code", method = RequestMethod.GET)
    public String codeResponse(@RequestParam(value = "code", required = true) String code) throws ParseException {
        LOGGER.info("get authorization token by authorization code", TokenController.class);

        OAuth2AccessToken accessToken = null;
		try {
			accessToken = tokenService.getAccessTokenByAuthCode(code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (accessToken != null) {
            LOGGER.info("succeed in to get authorization token by authorization code", TokenController.class);
        } else {
            LOGGER.warn("failed to get authorization token by authorization code", TokenController.class);
        }

        return "redirect:/";
    }

}
~~~

### SessionData.java
 (DO NOT CHANGE)
~~~
package yournamespace.yourapplication.tradeshift.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

import yournamespace.yourapplication.tradeshift.domain.dto.JwtDTO;

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
~~~

### JwtDTO.java
 (DO NOT CHANGE)
~~~
package yournamespace.yourapplication.tradeshift.domain.dto;

public class JwtDTO {

    private String originalJWTToken;
    private Long expirationTime;
    private String tradeshiftUserEmail;
    private String appName;
    private String iss;
    private String jwtUniqueIdentifier;
    private Long issuedAtTime;
    private String companyId;
    private String userId;

    public JwtDTO() {
        super();
    }


    public String getTradeshiftUserEmail() {
        return tradeshiftUserEmail;
    }

    public void setTradeshiftUserEmail(String tradeshiftUserEmail) {
        this.tradeshiftUserEmail = tradeshiftUserEmail;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getJwtUniqueIdentifier() {
        return jwtUniqueIdentifier;
    }

    public void setJwtUniqueIdentifier(String jwtUniqueIdentifier) {
        this.jwtUniqueIdentifier = jwtUniqueIdentifier;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginalJWTToken() {
        return originalJWTToken;
    }

    public void setOriginalJWTToken(String originalJWTToken) {
        this.originalJWTToken = originalJWTToken;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getIssuedAtTime() {
        return issuedAtTime;
    }

    public void setIssuedAtTime(Long issuedAtTime) {
        this.issuedAtTime = issuedAtTime;
    }

}
~~~

### DocState.java

Convenient enums for Tradeshift document states

~~~
package yournamespace.yourapplication.tradeshift.domain.enums;

public enum DocState {
    ACCEPTED, REJECTED_BY_RECEIVER, DELIVERED, OVERDUE, PAID_CONFIRMED, PAID_UNCONFIRMED, REJECTED_BY_SENDER, DISPUTED_BY_RECEIVER, FAILED_DELIVERY, PENDING_NOT_A_CONTACT
}
~~~


### DocType.java

Convenient enums for Tradeshift document types
~~~
package yournamespace.yourapplication.tradeshift.domain.enums;

public enum DocType {
    order, invoice, quote, creditnote
}
~~~

### TokenService.java

Tradeshift OAuth2 handler interface. (DO NOT CHANGE)

~~~
package yournamespace.yourapplication.tradeshift.services;

import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import yournamespace.yourapplication.tradeshift.domain.dto.JwtDTO;

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
~~~

### TokenServiceImpl.java

This service implementation handles Tradeshift OAuth2 process. (DO NOT CHANGE)

~~~
package yournamespace.yourapplication.tradeshift.services.Impl;

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

import yournamespace.yourapplication.tradeshift.config.PropertySources;
import yournamespace.yourapplication.tradeshift.config.authentication.CustomAuthenticationProvider;
import yournamespace.yourapplication.tradeshift.domain.SessionData;
import yournamespace.yourapplication.tradeshift.domain.dto.JwtDTO;
import yournamespace.yourapplication.tradeshift.services.TokenService;

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
~~~

### application.properties
~~~

~~~

### app.js

AngularJS application module

~~~
var app = angular.module("app", [ 'pascalprecht.translate' ]);

// setting up html mode
app.config(function($locationProvider) {
	$locationProvider.html5Mode({
		enabled : true,
		requireBase : false
	})
});


// setting up translation for localization
app.config(function($translateProvider) {
	$translateProvider
    .useStaticFilesLoader({ // load our locales
        prefix: 'strings/',
        suffix: '.json'
    })
    .useSanitizeValueStrategy('escape')
    .registerAvailableLanguageKeys(['ja', 'en'])
    .determinePreferredLanguage(function () { // choose the best language based on browser languages
        var translationKeys = $translateProvider.registerAvailableLanguageKeys(),
            browserKeys = navigator.languages,
            preferredLanguage;

        label: for (var i = 0; i < browserKeys.length; i++) {
            for (var j = 0; j < translationKeys.length; j++) {
                if (browserKeys[i] == translationKeys[j]) {
                    preferredLanguage = browserKeys[i];
                    break label;
                }
            }
        }
        return preferredLanguage;
	});
});
~~~


### controller.js

AngularJS controller (example)

~~~
app.controller("controller", function($scope, $http, $req, $q, $filter, $window, $timeout, $translate) {
	// Tradeshift UI Component library
	$scope.ui = ts.ui;

	// Begin rendering UI
	$scope.ui.ready(function() {

		... render top bar
		... render form and table

		// localized strings
		$scope.locale;

		$q.all([
		    $translate(["Label.Label1", "Label.Label2", "Button.Button1"])        
		])
		.then(function(response) {
			var locale = response[0];
			$scope.locale = locale;

			... your code
		});

		... your functions, etc.

	});
});
~~~

### en.json

Sample localized strings

~~~
{
	"Label": { "Label1": "Label 1", "Label2": "Label 2" },
	"Button": { "Button1" : "Button 1" }
}
~~~

### ja.json

Sample localized strings

~~~
{
	"Label": { "Label1": "ラベル１", "Label2": "ラベル２" },
	"Button": { "Button1" : "ボタン１" }
}
~~~


### index.html


~~~
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width" />
<meta charset="utf-8">
<link rel="stylesheet" href="styles/main.css">
<link rel="stylesheet" href="//d5wfroyti11sa.cloudfront.net/prod/client/ts-10.0.21.min.css"/>
<script
	src="//d5wfroyti11sa.cloudfront.net/prod/client/ts-10.0.21.min.js"></script>
<script src="webjars/angularjs/1.6.2/angular.js"></script>
<script src="webjars/angular-translate/2.13.1/angular-translate.js"></script>
<script
	src="webjars/angular-translate-loader-static-files/2.13.1/angular-translate-loader-static-files.js"></script>
<script src="scripts/app.js"></script>
<script src="scripts/controller.js"></script>
</head>
<body ng-app="app" ng-controller="controller">
	<main data-ts="Main">
	<div data-ts="MainContent">
		...
		...
		Your page content
		...
		...
	</div>
	</main>
</body>
</html>
~~~

### Procfile
Heroku runner.
~~~
web: java -Dserver.port=$PORT -jar target/yourapplication-0.0.1-SNAPSHOT.jar
~~~

### References

- [Spring Boot 1.5.13](https://docs.spring.io/spring-boot/docs/1.5.13.RELEASE/reference/html/)
- [Tradeshift UI Component v10](http://ui.tradeshift.com/)
- [AngularJS 1.6.2](https://code.angularjs.org/1.6.2/docs/api)
