package gr.cite.intelcomp.stiviewer.integrationevent.outbox.tenantuserinvited;

import gr.cite.intelcomp.stiviewer.integrationevent.TrackedEvent;

import java.util.List;
import java.util.UUID;

public class TenantUserInvitedIntegrationEvent extends TrackedEvent {
	public class ClaimValue {
		private String claim;
		private String value;

		public String getClaim() {
			return claim;
		}

		public void setClaim(String claim) {
			this.claim = claim;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private UUID user;
	private UUID tenant;
	private List<ClaimValue> claims;
	private String mobilePhone;
	private String email;

	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}

	public UUID getTenant() {
		return tenant;
	}

	public void setTenant(UUID tenant) {
		this.tenant = tenant;
	}

	public List<ClaimValue> getClaims() {
		return claims;
	}

	public void setClaims(List<ClaimValue> claims) {
		this.claims = claims;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
