package gr.cite.user.web.model;

import gr.cite.user.common.validation.FieldNotNullIfOtherSet;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class ChildClassTest {

	public ChildClassTest(){}
	public ChildClassTest(UUID id, String name){
		this.id = id;
		this.name = name;
	}
	@NotNull(message = "{validation.empty}")
	private UUID id;

	@NotNull(message = "{validation.empty}")
	private String name;

	private String hash;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
