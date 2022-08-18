package gr.cite.intelcomp.stiviewer.model.elasticreport;

import java.util.List;

public class AggregateResponseItemModel {
	public AggregateResponseGroupModel group;
	public List<AggregateResponseValueModel> values;


	public AggregateResponseGroupModel getGroup() {
		return group;
	}

	public List<AggregateResponseValueModel> getValues() {
		return values;
	}

	public void setGroup(AggregateResponseGroupModel group) {
		this.group = group;
	}

	public void setValues(List<AggregateResponseValueModel> values) {
		this.values = values;
	}
}
