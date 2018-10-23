package jp.tsubakicraft.easyreport.tradeshift.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("serial")
@Configuration
public class ApplicationProperties implements Serializable {
	
	private static final int DEFAULT_FETCH_LIMIT = 100;

	@Value("${fetchLimit.invoice}")
	private String invoiceFetchLimit;
	
	@Value("${oauthType}")
	private String oauthType;
	
	@Value("${oauth.consumerKey}")
	private String consumerKey;
	
	@Value("${oauth.consumerSecret}")
	private String consumerSecret;
	
	@Value("${oauth.accessToken}")
	private String accessToken;
	
	@Value("${oauth.tokenSecret}")
	private String tokenSecret;
	
	@Value("${oauth.signatureMethod}")
	private String signatureMethod;
	
	@Value("${oauth.nonce}")
	private String nonce;
	
	@Value("${oauth.version}")
	private String version;
	
	@Value("${tradeshift.tradeshiftAppVersion}")
	private String tradeshiftAppVersion;
	
	@Value("${tradeshift.tradeshiftAPIDomainName}")
	private String tradeshiftAPIDomainName;
	
	@Value("${tradeshift.clientID}")
	private String clientID;
	
	@Value("${tradeshift.clientSecret}")
	private String clientSecret;
	
	@Value("${tradeshift.redirectUri}")
	private String redirectUri;

	@Value("${app.domain}")
	private String appDomain;
	
	public int getInvoiceFetchLimit() {
		try {
			int limit = Integer.parseInt(invoiceFetchLimit);
			return limit;
		} catch (Exception e) {
			return DEFAULT_FETCH_LIMIT;
		}
	}
	
	/**
	 * @return the oauthType
	 */
	public String getOauthType() {
		return oauthType;
	}

	/**
	 * @param oauthType the oauthType to set
	 */
	public void setOauthType(String oauthType) {
		this.oauthType = oauthType;
	}

	/**
	 * @return the consumerKey
	 */
	public String getConsumerKey() {
		return consumerKey;
	}

	/**
	 * @param consumerKey the consumerKey to set
	 */
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	/**
	 * @return the consumerSecret
	 */
	public String getConsumerSecret() {
		return consumerSecret;
	}

	/**
	 * @param consumerSecret the consumerSecret to set
	 */
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the tokenSecret
	 */
	public String getTokenSecret() {
		return tokenSecret;
	}

	/**
	 * @param tokenSecret the tokenSecret to set
	 */
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	/**
	 * @return the signatureMethod
	 */
	public String getSignatureMethod() {
		return signatureMethod;
	}

	/**
	 * @param signatureMethod the signatureMethod to set
	 */
	public void setSignatureMethod(String signatureMethod) {
		this.signatureMethod = signatureMethod;
	}

	/**
	 * @return the nonce
	 */
	public String getNonce() {
		return nonce;
	}

	/**
	 * @param nonce the nonce to set
	 */
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the tradeshiftAppVersion
	 */
	public String getTradeshiftAppVersion() {
		return tradeshiftAppVersion;
	}

	/**
	 * @param tradeshiftAppVersion the tradeshiftAppVersion to set
	 */
	public void setTradeshiftAppVersion(String tradeshiftAppVersion) {
		this.tradeshiftAppVersion = tradeshiftAppVersion;
	}

	/**
	 * @return the tradeshiftAPIDomainName
	 */
	public String getTradeshiftAPIDomainName() {
		return tradeshiftAPIDomainName;
	}

	/**
	 * @param tradeshiftAPIDomainName the tradeshiftAPIDomainName to set
	 */
	public void setTradeshiftAPIDomainName(String tradeshiftAPIDomainName) {
		this.tradeshiftAPIDomainName = tradeshiftAPIDomainName;
	}

	/**
	 * @return the clientID
	 */
	public String getClientID() {
		return clientID;
	}

	/**
	 * @param clientID the clientID to set
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @return the redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}

	/**
	 * @param redirectUri the redirectUri to set
	 */
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
	public String getAppDomain() {
		return appDomain;
	}
	
	public void setAppDomain(String domain) {
		this.appDomain = domain;
	}
	
}
