package gr.cite.notification.common.types.tenantconfiguration;

public class DefaultUserLocaleConfigurationDataContainer {

    public static class Field {
        public static final String LANGUAGE = "language";
        public static final String TIME_ZONE = "timeZone";
        public static final String CULTURE = "culture";
    }
    private String language;
    private String timeZone;
    private String culture;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }
}
