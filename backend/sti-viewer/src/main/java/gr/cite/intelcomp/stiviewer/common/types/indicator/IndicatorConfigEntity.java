package gr.cite.intelcomp.stiviewer.common.types.indicator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorConfigEntity {
	private AccessRequestConfigEntity accessRequestConfig;

	public AccessRequestConfigEntity getAccessRequestConfig() {
		return accessRequestConfig;
	}

	public void setAccessRequestConfig(AccessRequestConfigEntity accessRequestConfig) {
		this.accessRequestConfig = accessRequestConfig;
	}
}
