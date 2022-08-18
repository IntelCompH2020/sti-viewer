package gr.cite.intelcomp.stiviewer.model.persist.dataaccessrequest;


import gr.cite.intelcomp.stiviewer.common.enums.DataAccessRequestStatus;
import gr.cite.intelcomp.stiviewer.common.validation.FieldNotNullIfOtherSet;
import gr.cite.intelcomp.stiviewer.common.validation.ValidId;

import javax.validation.constraints.NotNull;
import java.util.UUID;


@FieldNotNullIfOtherSet(notNullField = DataAccessRequestStatusPersist._id, otherSetField = DataAccessRequestStatusPersist._hash, failOn = DataAccessRequestStatusPersist._hash, message = "{validation.hashempty}")
public class DataAccessRequestStatusPersist {

	@ValidId(message = "{validation.invalidid}")
	private UUID id;
	public final static String _id = "id";

	@NotNull(message = "{validation.empty}")
	private DataAccessRequestStatus status;

	private String hash;
	public final static String _hash = "hash";

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public DataAccessRequestStatus getStatus() {
		return status;
	}

	public void setStatus(DataAccessRequestStatus status) {
		this.status = status;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
