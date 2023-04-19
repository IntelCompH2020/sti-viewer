package gr.cite.notification.audit;

import gr.cite.tools.logging.EventId;

public class AuditableAction {
	public static final EventId Tenant_Available_Notifiers_Query = new EventId(2006, "Tenant_Available_Notifiers_Query");
	public static final EventId Principal_Lookup = new EventId(6000, "Principal_Lookup");
	public static final EventId Tenants_Lookup = new EventId(6001, "Tenants_Lookup");

	public static final EventId User_Available_Notifiers_Query = new EventId(10004, "User_Available_Notifiers_Query");

	public static final EventId Notification_Query = new EventId(19000, "Notification_Query");
	public static final EventId Notification_Lookup = new EventId(19001, "Notification_Lookup");
	public static final EventId Notification_Persist = new EventId(19002, "Notification_Persist");
	public static final EventId Notification_Delete = new EventId(19003, "Notification_Delete");
	
	public static final EventId InApp_Notification_Query = new EventId(20000, "InApp_Notification_Query");
	public static final EventId InApp_Notification_Lookup = new EventId(20001, "InApp_Notification_Lookup");
	public static final EventId InApp_Notification_Persist = new EventId(20002, "InApp_Notification_Persist");
	public static final EventId InApp_Notification_Delete = new EventId(20003, "InApp_Notification_Delete");
	public static final EventId InApp_Notification_Read = new EventId(20003, "InApp_Notification_Read");
	public static final EventId InApp_Notification_Read_All = new EventId(20003, "InApp_Notification_Read_All");
	public static final EventId Tenant_Configuration_Query = new EventId(21000, "Tenant_Configuration_Query");
	public static final EventId Tenant_Configuration_Lookup = new EventId(21001, "Tenant_Configuration_Lookup");
	public static final EventId Tenant_Configuration_Persist = new EventId(21002, "Tenant_Configuration_Persist");
	public static final EventId Tenant_Configuration_Delete = new EventId(21003, "Tenant_Configuration_Delete");
	public static final EventId User_Notification_Preference_Query = new EventId(22000, "User_Notification_Preference_Query");
	public static final EventId User_Notification_Preference_Lookup = new EventId(22001, "User_Notification_Preference_Lookup");
	public static final EventId User_Notification_Preference_Persist = new EventId(22002, "User_Notification_Preference_Persist");
	public static final EventId User_Notification_Preference_Delete = new EventId(22003, "User_Notification_Preference_Delete");
}
