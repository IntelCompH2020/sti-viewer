package gr.cite.notification.config.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "email")
public class EmailProperties {
    private final String address;

    public EmailProperties(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
