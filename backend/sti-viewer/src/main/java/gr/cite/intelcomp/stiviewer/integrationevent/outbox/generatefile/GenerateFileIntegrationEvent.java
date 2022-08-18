package gr.cite.intelcomp.stiviewer.integrationevent.outbox.generatefile;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.UUID;

public class GenerateFileIntegrationEvent extends TrackedEvent {

	private UUID id;
	private UUID userId;
	private String data;
	private UUID templateId;
	private UUID templateKey;
	private String language;
	private UUID tenant;
	private String fileName;
	private Boolean asPdf;
	private UUID fileId;
	private int lifetime;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public UUID getTemplateId() {
		return templateId;
	}

	public void setTemplateId(UUID templateId) {
		this.templateId = templateId;
	}

	public UUID getTemplateKey() {
		return templateKey;
	}

	public void setTemplateKey(UUID templateKey) {
		this.templateKey = templateKey;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public UUID getTenant() {
		return tenant;
	}

	public void setTenant(UUID tenant) {
		this.tenant = tenant;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getAsPdf() {
		return asPdf;
	}

	public void setAsPdf(Boolean asPdf) {
		this.asPdf = asPdf;
	}

	public UUID getFileId() {
		return fileId;
	}

	public void setFileId(UUID fileId) {
		this.fileId = fileId;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}
}
