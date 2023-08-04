package gr.cite.intelcomp.stiviewer.common.types.externaltoken;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefinitionMapperEntity {
    private List<String> externalIds;
    private UUID indicatorPointQueryId;

    public List<String> getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(List<String> externalIds) {
        this.externalIds = externalIds.stream().filter(x -> x != null && !x.isBlank()).map(x -> x.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
    }

    public UUID getIndicatorPointQueryId() {
        return indicatorPointQueryId;
    }

    public void setIndicatorPointQueryId(UUID indicatorPointQueryId) {
        this.indicatorPointQueryId = indicatorPointQueryId;
    }

    public boolean isExternalIdsEqual(List<String> ids) {
        if (ids == null && this.externalIds == null) return true;
        if (ids == null || this.externalIds == null) return false;
        
        ids = ids.stream().filter(x -> x != null && !x.isBlank()).map(x -> x.toLowerCase(Locale.ROOT)).distinct().collect(Collectors.toList());
        if (ids.size() != this.externalIds.size()) return false;

        for (String id : this.externalIds) {
            if (ids.stream().noneMatch(x -> x.equalsIgnoreCase(id))) return false;
        }
        return true;
    }

    public boolean containsExternalId(String id) {
        if (id == null || id.isBlank() || this.externalIds == null) return false;

        for (String externalId : this.externalIds) {
            if (id.toLowerCase(Locale.ROOT).equalsIgnoreCase(externalId)) return true;
        }
        return false;
    }
}
