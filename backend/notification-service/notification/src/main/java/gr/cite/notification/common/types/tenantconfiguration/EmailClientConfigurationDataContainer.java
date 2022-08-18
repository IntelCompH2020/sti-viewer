package gr.cite.notification.common.types.tenantconfiguration;

public class EmailClientConfigurationDataContainer {

    public static class Field {
        public static final String REQUIRE_CREDENTIALS = "requireCredentials";
        public static final String ENABLE_SSL = "enableSSL";
        public static final String CERTIFICATE_PATH = "certificatePath";
        public static final String HOST_SERVER = "hostServer";
        public static final String HOST_PORT_NO = "hostPortNo";
        public static final String EMAIL_ADDRESS = "emailAddress";
        public static final String EMAIL_USER_NAME = "emailUserName";
        public static final String EMAIL_PASSWORD = "emailPassword";
    }
    private Boolean requireCredentials;
    private Boolean enableSSL;
    private String certificatePath;
    private String hostServer;
    private Integer hostPortNo;
    private String emailAddress;
    private String emailUserName;
    private String emailPassword;

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
}
