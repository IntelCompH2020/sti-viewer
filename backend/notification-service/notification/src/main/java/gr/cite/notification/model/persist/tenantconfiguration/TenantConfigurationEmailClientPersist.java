package gr.cite.notification.model.persist.tenantconfiguration;

import gr.cite.notification.common.validation.ValidId;

import java.util.UUID;

public class TenantConfigurationEmailClientPersist {
    @ValidId(message = "{validation.invalidid}")
    private UUID id;
    private Boolean requireCredentials;
    private Boolean enableSSL;
    private String certificatePath;
    private String hostServer;
    private Integer hostPortNo;
    private String emailAddress;
    private String emailUserName;
    private String emailPassword;
    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getRequireCredentials() {
        return requireCredentials;
    }

    public void setRequireCredentials(Boolean requireCredentials) {
        this.requireCredentials = requireCredentials;
    }

    public Boolean getEnableSSL() {
        return enableSSL;
    }

    public void setEnableSSL(Boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getHostServer() {
        return hostServer;
    }

    public void setHostServer(String hostServer) {
        this.hostServer = hostServer;
    }

    public Integer getHostPortNo() {
        return hostPortNo;
    }

    public void setHostPortNo(Integer hostPortNo) {
        this.hostPortNo = hostPortNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public void setEmailUserName(String emailUserName) {
        this.emailUserName = emailUserName;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
