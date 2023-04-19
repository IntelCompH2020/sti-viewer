package gr.cite.user.authorization;

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

	//TenantUser
	public static String BrowseTenantUser = "BrowseTenantUser";
	public static String EditTenantUser = "EditTenantUser";
	public static String DeleteTenantUser = "DeleteTenantUser";

	// UI Pages
	public static String ViewTenantPage = "ViewTenantPage";
	public static String ViewUsersPage = "ViewUsersPage";
	public static String ViewApiClientsPage = "ViewApiClientsPage";
	public static String ViewRoleAssignmentPage = "ViewRoleAssignmentPage";
	public static String ViewUserProfilePage = "ViewUserProfilePage";
	public static String ViewForgetMeRequestPage = "ViewForgetMeRequestPage";
	public static String ViewWhatYouKnowAboutMeRequestPage = "ViewWhatYouKnowAboutMeRequestPage";
	public static String ViewTenantConfigurationPage = "ViewTenantConfigurationPage";
}
