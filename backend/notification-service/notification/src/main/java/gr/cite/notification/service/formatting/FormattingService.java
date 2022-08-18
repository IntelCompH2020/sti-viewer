package gr.cite.notification.service.formatting;

import java.time.Instant;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public interface FormattingService {

	String format(int value, UUID userId, String format, Locale locale);

	String format(int value, String format, Locale locale);

	String format(long value, UUID userId, String format, Locale locale);

	String format(long value, String format, Locale locale);

	String format(double value, UUID userId, Integer decimals, String format, Locale locale);

	String format(double value, Integer decimals, String format, Locale locale);

	String format(Instant value, UUID userId, TimeZone timezone, String format, Locale locale);

	String format(Instant value, TimeZone timeZone, String format, Locale locale);
}

