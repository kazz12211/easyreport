package jp.tsubakicraft.tradeshift.domain.dto;

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
