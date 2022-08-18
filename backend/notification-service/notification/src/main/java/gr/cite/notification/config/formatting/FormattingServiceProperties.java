package gr.cite.notification.config.formatting;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "formatting.options")
public class FormattingServiceProperties {

	private final String integerFormat;
	private final String decimalFormat;
	private final String dateTimeFormat;
	private final Integer decimalDigitsRound;

	public FormattingServiceProperties(String integerFormat, String decimalFormat, String dateTimeFormat, Integer decimalDigitsRound) {
		this.integerFormat = integerFormat;
		this.decimalFormat = decimalFormat;
		this.dateTimeFormat = dateTimeFormat;
		this.decimalDigitsRound = decimalDigitsRound;
	}

	public String getIntegerFormat() {
		return integerFormat;
	}

	public String getDecimalFormat() {
		return decimalFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public Integer getDecimalDigitsRound() {
		return decimalDigitsRound;
	}
}