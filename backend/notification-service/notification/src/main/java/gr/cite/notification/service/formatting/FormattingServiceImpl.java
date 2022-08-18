package gr.cite.notification.service.formatting;

import gr.cite.notification.cache.FormattingUserprofileCacheCacheService;
import gr.cite.notification.config.formatting.FormattingServiceProperties;
import gr.cite.notification.convention.ConventionService;
import gr.cite.notification.data.UserEntity;
import gr.cite.notification.locale.LocaleService;
import gr.cite.notification.model.User;
import gr.cite.notification.query.UserQuery;
import gr.cite.tools.data.query.QueryFactory;
import gr.cite.tools.fieldset.BaseFieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

@Component
@RequestScope
public class FormattingServiceImpl implements FormattingService {

	private final QueryFactory queryFactory;
	private final ConventionService conventionService;
	private final LocaleService localeService;
	private final FormattingServiceProperties properties;
	private final FormattingUserprofileCacheCacheService formattingUserprofileCacheCacheService;

	@Autowired
	public FormattingServiceImpl(QueryFactory queryFactory,
	                             ConventionService conventionService,
	                             LocaleService localeService,
	                             FormattingServiceProperties properties,
	                             FormattingUserprofileCacheCacheService formattingUserprofileCacheCacheService
	) {
		this.queryFactory = queryFactory;
		this.conventionService = conventionService;
		this.localeService = localeService;
		this.properties = properties;
		this.formattingUserprofileCacheCacheService = formattingUserprofileCacheCacheService;
	}

	public String format(int value, UUID userId, String format, Locale locale) {
		return this.formatNonDecimal(value, userId, format, locale);
	}

	private String formatNonDecimal(Number value,UUID userId, String format, Locale locale) {
		Locale localeToUse = this.localeService.culture();
		if (locale != null) {
			localeToUse = locale;
		} else if (userId != null) {
			FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue profile = this.getUserProfile(userId);
			if (profile != null) {
				localeToUse = this.localeService.cultureSafe(profile.getCulture());
			}
		}

		String formatToUse = !this.conventionService.isNullOrEmpty(format) ? format : this.properties.getIntegerFormat();
		return this.formatNonDecimal(value, formatToUse, localeToUse);
	}

	public String format(int value, String format, Locale locale) {
		return this.formatNonDecimal(value, format, locale);
	}

	private String formatNonDecimal(Number value, String format, Locale locale) {
		if (this.conventionService.isNullOrEmpty(format) && locale != null) return NumberFormat.getInstance(locale).format(value);
		else if (!this.conventionService.isNullOrEmpty(format) && locale == null) return String.format(format, value);
		else if (!this.conventionService.isNullOrEmpty(format) && locale != null) return String.format(locale, format, value);

		return NumberFormat.getInstance(Locale.ROOT).format(value);
	}

	public String format(long value, UUID userId, String format, Locale locale) {
		return this.formatNonDecimal(value, userId, format, locale);
	}

	public String format(long value, String format, Locale locale) {
		return this.formatNonDecimal(value, format, locale);
	}

	public String format(double value, UUID userId, Integer decimals, String format, Locale locale) {
		Locale localeToUse = this.localeService.culture();
		if (locale != null) {
			localeToUse = locale;
		} else if (userId != null) {
			FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue profile = this.getUserProfile(userId);
			if (profile != null) {
				localeToUse = this.localeService.cultureSafe(profile.getCulture());
			}
		}

		String formatToUse = !this.conventionService.isNullOrEmpty(format) ? format : this.properties.getDecimalFormat();
		int decimalsToUse = decimals != null ? decimals : this.properties.getDecimalDigitsRound();
		return this.format(value, decimalsToUse, formatToUse, localeToUse);
	}

	public String format(double value, Integer decimals, String format, Locale locale) {
		double val = value;
		if (decimals != null) {
			BigDecimal bd = new BigDecimal(Double.toString(value));
			bd = bd.setScale(decimals, RoundingMode.HALF_UP);
			val = bd.doubleValue();
		}

		if (this.conventionService.isNullOrEmpty(format) && locale != null) return NumberFormat.getInstance(locale).format(val);
		else if (!this.conventionService.isNullOrEmpty(format) && locale == null) return new DecimalFormat(format).format(val);
		else if (!this.conventionService.isNullOrEmpty(format) && locale != null) return new DecimalFormat(format, new DecimalFormatSymbols(locale)).format(val);

		return NumberFormat.getInstance(Locale.ROOT).format(val);
	}

	public String format(Instant value, UUID userId, TimeZone timezone, String format, Locale locale) {
		FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue profile = null;
		if (userId != null && (locale == null || timezone == null)) {
			profile = this.getUserProfile(userId);
		}

		Locale localeToUse = this.localeService.culture();
		if (locale != null) {
			localeToUse = locale;
		} else if (userId != null) {
			localeToUse = this.localeService.cultureSafe(profile.getCulture());
		}

		TimeZone timezoneToUse = this.localeService.timezone();
		if (timezone != null) {
			timezoneToUse = timezone;
		} else if (userId != null) {
			timezoneToUse = this.localeService.timezoneSafe(profile.getZone());
		}

		String formatToUse = !this.conventionService.isNullOrEmpty(format) ? format : this.properties.getDateTimeFormat();
		return this.format(value, timezoneToUse, formatToUse, localeToUse);
	}

	public String format(Instant value, TimeZone timeZone, String format, Locale locale) {
		ZoneId zoneId = ZoneId.from(ZoneOffset.UTC);

		if (timeZone != null) {
			zoneId = timeZone.toZoneId();
		}
		if (this.conventionService.isNullOrEmpty(format) && locale != null) DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM).withLocale(locale).withZone(zoneId).format(value);
		else if (!this.conventionService.isNullOrEmpty(format) && locale == null) return DateTimeFormatter.ofPattern(format).withZone(zoneId).format(value);
		else if (!this.conventionService.isNullOrEmpty(format) && locale != null) return DateTimeFormatter.ofPattern(format, locale).withZone(zoneId).format(value);

		return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM).withZone(zoneId).format(value);
	}

	private FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue getUserProfile(UUID userId) {
		FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue cacheValue = this.formattingUserprofileCacheCacheService.lookup(this.formattingUserprofileCacheCacheService.buildKey(userId));
		if (cacheValue != null) {
			return cacheValue;
		} else {
			UserEntity user = this.queryFactory.query(UserQuery.class).ids(userId).firstAs(new BaseFieldSet().ensure(User._culture).ensure(User._language).ensure(User._timezone).ensure(User._id));
			if (user == null) return null;
			cacheValue = new FormattingUserprofileCacheCacheService.UserFormattingProfileCacheValue(userId, user.getTimezone(), user.getCulture(), user.getLanguage());
			this.formattingUserprofileCacheCacheService.put(cacheValue);

			return cacheValue;
		}
	}
}

