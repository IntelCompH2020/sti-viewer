package gr.cite.notification.authorization;

public final class Permission {
	//User
	public static String BrowseUser = "BrowseUser";
	public static String EditUser = "EditUser";
	public static String DeleteUser = "DeleteUser";

	//UserContactInfo
	public static String BrowseUserContactInfo = "BrowseUserContactInfo";
	public static String EditUserContactInfo = "EditUserContactInfo";
	public static String DeleteUserContactInfo = "DeleteUserContactInfo";

	//Tenant
	public static String BrowseTenant = "BrowseTenant";
	public static String EditTenant = "EditTenant";
	public static String DeleteTenant = "DeleteTenant";
	public static String AllowNoTenant = "AllowNoTenant";

	//Notification
	public static final String BrowseNotification = "BrowseNotification";
	public static String EditNotification = "EditNotification";
	public static String DeleteNotification = "DeleteNotification";
	
	public static final String BrowseTenantConfiguration = "BrowseTenantConfiguration";
	public static final String EditTenantConfiguration = "EditTenantConfiguration";

	//Notification
	public static final String BrowseUserNotificationPreference = "BrowseUserNotificationPreference";
	public static final String EditUserNotificationPreference = "EditUserNotificationPreference";

	// UI Pages
	public static String ViewTenantConfigurationPage = "ViewTenantConfigurationPage";
	public static String ViewNotificationPage = "ViewNotificationPage";
	public static String ViewNotificationEventRulePage = "ViewNotificationEventRulePage";
	public static String ViewInAppNotificationPage = "ViewInAppNotificationPage";
	public static String ViewNotificationTemplatePage = "ViewNotificationTemplatePage";

}
