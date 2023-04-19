package gr.cite.intelcomp.stiviewer.model.persist;

import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;
import gr.cite.intelcomp.stiviewer.model.persist.indicator.IndicatorConfigPersist;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@FieldNotNullIfOtherSet(message = "{validation.hashempty}")
public class PageContentPersist {

    @ValidId(message = "{validation.invalidid}")
    private UUID id;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 50, message = "{validation.largerthanmax}")
    private String language;

    @NotNull(message = "{validation.empty}")
    @NotEmpty(message = "{validation.empty}")
    @Size(max = 500, message = "{validation.largerthanmax}")
    private String title;

    @ValidId(message = "{validation.invalidid}")
    private UUID pageId;

    private String content;

    private String hash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public UUID getPageId() {
        return pageId;
    }

    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }
}
