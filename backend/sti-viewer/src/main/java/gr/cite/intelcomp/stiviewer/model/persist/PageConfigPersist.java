package gr.cite.intelcomp.stiviewer.model.persist;

import java.util.List;

public class PageConfigPersist {
    private List<String> allowedRoles;
    private String externalUrl;
    private String matIcon;

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
