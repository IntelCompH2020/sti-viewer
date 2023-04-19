package gr.cite.user.audit;

import gr.cite.tools.logging.EventId;

public class AuditableAction {
	public static final EventId Tenant_Query = new EventId(2000, "Tenant_Query");
	public static final EventId Tenant_Lookup = new EventId(2001, "Tenant_Lookup");
	public static final EventId Tenant_Persist = new EventId(2002, "Tenant_Persist");
	public static final EventId Tenant_Delete = new EventId(2003, "Tenant_Delete");
	public static final EventId Tenant_Reactivate = new EventId(2004, "Tenant_Reactivate");

	public static final EventId Principal_Lookup = new EventId(6000, "Principal_Lookup");
	public static final EventId Tenants_Lookup = new EventId(6001, "Tenants_Lookup");


	public static final EventId User_Query = new EventId(10000, "User_Query");
	public static final EventId User_Lookup = new EventId(10001, "User_Lookup");
	public static final EventId User_Persist = new EventId(10002, "User_Persist");
	public static final EventId User_Delete = new EventId(10003, "User_Delete");


	public static final EventId User_Settings_Query = new EventId(14000, "User_Settings_Query");
	public static final EventId User_Settings_Lookup = new EventId(14001, "User_Settings_Lookup");
	public static final EventId User_Settings_Persist = new EventId(14002, "User_Settings_Persist");
	public static final EventId User_Settings_Delete = new EventId(14003, "User_Settings_Delete");



}
