package gr.cite.notification.web.model;

import gr.cite.notification.common.validation.FieldNotNullIfOtherSet;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@FieldNotNullIfOtherSet(notNullField = "id", otherSetField = "hash", failOn = "hash", message = "{validation.hashempty}")
public class ParentClassTest {

	@NotNull(message = "{validation.empty}")
	private UUID id;
	private String hash;
	@Min(value = 1, message = "{validation.lowerthanmin}")
	@NotNull(message = "{validation.empty}")
	private Integer number;
	@Max(value = 5, message = "{validation.largerthanmax}")
	@NotEmpty(message = "{validation.empty}")
	private String text;

//	private Boolean hashNotIssue;
//	public void setHashNotIssue(Boolean hashNotIssue){this.hashNotIssue = hashNotIssue;}
//	@AssertTrue(message = "method error", groups = BaseValidationGroup.class)
//	public Boolean isHashNotIssue(){
//		if(text == null) return false;
//		if(text.length() > 200) return true;
//		return false;
//	}

	@Valid
	private ChildClassTest child;
	@Valid
	private List<ChildClassTest> childs;

	public List<ChildClassTest> getChilds() {
		return childs;
	}

	public void setChilds(List<ChildClassTest> childs) {
		this.childs = childs;
	}

	public ChildClassTest getChild() {
		return child;
	}

	public void setChild(ChildClassTest child) {
		this.child = child;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
