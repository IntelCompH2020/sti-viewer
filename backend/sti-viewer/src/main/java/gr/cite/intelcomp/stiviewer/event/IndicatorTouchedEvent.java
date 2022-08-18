package gr.cite.intelcomp.stiviewer.event;

import java.util.UUID;

public class IndicatorTouchedEvent {
	public IndicatorTouchedEvent() {
	}

	public IndicatorTouchedEvent(UUID id) {
		this.id = id;
	}

	private UUID id;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
