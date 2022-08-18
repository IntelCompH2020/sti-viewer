package gr.cite.intelcomp.stiviewer.elastic.data.indicator;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class AltTextEntity {
	public static final class Fields {
		public static final String lang = "lang";
		public static final String text = "text";
	}

	@Field(value = Fields.lang, type = FieldType.Keyword)
	private String lang;

	@Field(value = Fields.text, type = FieldType.Text)
	private String text;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
