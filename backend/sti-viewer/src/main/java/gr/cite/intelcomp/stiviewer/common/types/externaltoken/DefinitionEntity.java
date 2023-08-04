package gr.cite.intelcomp.stiviewer.common.types.externaltoken;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DefinitionEntity {
    private Map<UUID, IndicatorPointQueryDefinitionEntity> indicatorPointQueryMap;
    private List<DefinitionMapperEntity> mappers;

    public Map<UUID, IndicatorPointQueryDefinitionEntity> getIndicatorPointQueryMap() {
        return indicatorPointQueryMap;
    }

    public void setIndicatorPointQueryMap(Map<UUID, IndicatorPointQueryDefinitionEntity> indicatorPointQueryMap) {
        this.indicatorPointQueryMap = indicatorPointQueryMap;
    }

    public List<DefinitionMapperEntity> getMappers() {
        return mappers;
    }

    public void setMappers(List<DefinitionMapperEntity> mappers) {
        this.mappers = mappers;
    }
    
    public IndicatorPointQueryDefinitionEntity getIndicatorPointQuery(List<String> keys){
        if (this.getIndicatorPointQueryMap() == null || this.getMappers() == null || keys == null) return null;

        DefinitionMapperEntity mapper = this.getMappers().stream().filter(x-> x.isExternalIdsEqual(keys)).findFirst().orElse(null );
        if (mapper == null || !this.getIndicatorPointQueryMap().containsKey(mapper.getIndicatorPointQueryId())) return null;
        return this.getIndicatorPointQueryMap().get(mapper.getIndicatorPointQueryId());
    }
}
