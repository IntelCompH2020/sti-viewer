package gr.cite.intelcomp.stiviewer.model.elasticindicator;

import java.time.Instant;
import java.util.List;

public class Metadata {

	public final static String _label = "label";
	private String label;

	public final static String _description = "description";
	private String description;

	public final static String _url = "url";
	private String url;

	public final static String _code = "code";
	private String code;

	public final static String _semanticLabels = "semanticLabels";
	private List<SemanticsLabel> semanticLabels;

	public final static String _altLabels = "altLabels";
	private List<AltText> altLabels;

	public final static String _altDescriptions = "altDescriptions";
	private List<AltText> altDescriptions;

	public final static String _date = "date";
	private Instant date;

	public final static String _coverage = "coverage";
	private List<Coverage> coverage;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<SemanticsLabel> getSemanticLabels() {
		return semanticLabels;
	}

	public void setSemanticLabels(List<SemanticsLabel> semanticLabels) {
		this.semanticLabels = semanticLabels;
	}

	public List<AltText> getAltLabels() {
		return altLabels;
	}

	public void setAltLabels(List<AltText> altLabels) {
		this.altLabels = altLabels;
	}

	public List<AltText> getAltDescriptions() {
		return altDescriptions;
	}

	public void setAltDescriptions(List<AltText> altDescriptions) {
		this.altDescriptions = altDescriptions;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<Coverage> getCoverage() {
		return coverage;
	}

	public void setCoverage(List<Coverage> coverage) {
		this.coverage = coverage;
	}
}
