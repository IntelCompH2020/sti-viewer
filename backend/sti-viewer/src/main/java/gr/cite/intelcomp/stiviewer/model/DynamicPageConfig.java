package gr.cite.intelcomp.stiviewer.model;

import java.util.List;

public class DynamicPageConfig {
	private List<String> allowedRoles;
	public final static String _allowedRoles = "allowedRoles";
	private String externalUrl;
	public final static String _externalUrl = "externalUrl";
	private String matIcon;
	public final static String _matIcon = "matIcon";

	public List<String> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(List<String> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public String getMatIcon() {
		return matIcon;
	}

	public void setMatIcon(String matIcon) {
		this.matIcon = matIcon;
	}
}
