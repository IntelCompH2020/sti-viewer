package gr.cite.intelcomp.stiviewer.service.indicator;

import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class IndicatorConfigItem {
	private final UUID id;
	private final Map<String, FieldEntity> extraProps;

	public IndicatorConfigItem(UUID id, List<FieldEntity> extraProps) {
		this.id = id;
		this.extraProps = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (FieldEntity field : extraProps) {
			this.extraProps.put(field.getCode(), field);
		}
	}

	public UUID getId() {
		return id;
	}

	public Map<String, FieldEntity> getExtraProps() {
		return extraProps;
	}
}
