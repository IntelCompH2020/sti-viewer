package gr.cite.intelcomp.stiviewer.common.types.datalastview;

import java.time.Instant;
import java.util.ArrayList;

public class DataTreeLastViewConfigEntity {
	private Instant lastAccess;
	public Instant getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(Instant lastAccess) {
		this.lastAccess = lastAccess;
	}
}

