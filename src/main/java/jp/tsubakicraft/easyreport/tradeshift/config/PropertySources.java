package jp.tsubakicraft.easyreport.tradeshift.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "propertySources")
public class PropertySources {

	@Autowired
	ApplicationProperties applicationProperties;
	
    private String tradeshiftAPIDomainName;
    private String clientID;
    private String clientSecret;
    private String redirectUri;
    private String tradeshiftAppVersion;

    @PostConstruct
    public void PropertySourcesInit() {
        this.tradeshiftAppVersion = applicationProperties.getTradeshiftAppVersion();
        this.tradeshiftAPIDomainName = applicationProperties.getTradeshiftAPIDomainName();
        this.clientID = applicationProperties.getClientID();
        this.clientSecret = applicationProperties.getClientSecret();
        this.redirectUri = applicationProperties.getRedirectUri();
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
