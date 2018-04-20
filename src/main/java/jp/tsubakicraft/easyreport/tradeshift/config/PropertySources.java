package jp.tsubakicraft.easyreport.tradeshift.config;

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
