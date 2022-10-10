package gr.cite.intelcomp.stiviewer.config.elastic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "app-elastic")
public class AppElasticProperties {
	private String indicatorIndexName;
	private String indicatorPointIndexNamePattern;
	private String indicatorCodeKey;

	public String getIndicatorIndexName() {
		return indicatorIndexName;
	}

	public void setIndicatorIndexName(String indicatorIndexName) {
		this.indicatorIndexName = indicatorIndexName;
	}

	public String getIndicatorPointIndexNamePattern() {
		return indicatorPointIndexNamePattern;
	}

	public void setIndicatorPointIndexNamePattern(String indicatorPointIndexNamePattern) {
		this.indicatorPointIndexNamePattern = indicatorPointIndexNamePattern;
	}

	public String getIndicatorCodeKey() {
		return indicatorCodeKey;
	}

	public void setIndicatorCodeKey(String indicatorCodeKey) {
		this.indicatorCodeKey = indicatorCodeKey;
	}
}
