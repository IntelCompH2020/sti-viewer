export enum AppPermission {
	DeferredAffiliation = "DeferredAffiliation",
	//DataAccessRequest
	BrowseDataAccessRequest = "BrowseDataAccessRequest",
	EditDataAccessRequest = "EditDataAccessRequest",
	CreateDataAccessRequest = "CreateTenantRequest",
	ApproveDataAccessRequest = "ApproveTenantRequest",
	RejectDataAccessRequest = "RejectTenantRequest",
	DeleteDataAccessRequest = "DeleteDataAccessRequest",

	//Indicator
	BrowseIndicator = "BrowseIndicator",
	EditIndicator = "EditIndicator",
	ResetIndicator = "ResetIndicator",
	DeleteIndicator = "DeleteIndicator",
	BrowseIndicatorReportConfig = "BrowseIndicatorReportConfig",

	//Bookmark
	BrowseBookmark = "BrowseBookmark",
	EditBookmark = "EditBookmark",
	DeleteBookmark = "DeleteBookmark",

	//Indicator Elastic
	BrowseIndicatorElastic = "BrowseIndicatorElastic",


	//IndicatorAccess
	BrowseIndicatorAccess = "BrowseIndicatorAccess",
	EditIndicatorAccess = "EditIndicatorAccess",
	DeleteIndicatorAccess = "DeleteIndicatorAccess",

	//User
	BrowseUser = "BrowseUser",
	EditUser = "EditUser",
	DeleteUser = "DeleteUser",

	//UserContactInfo
	BrowseUserContactInfo = "BrowseUserContactInfo",
	EditUserContactInfo = "EditUserContactInfo",
	DeleteUserContactInfo = "DeleteUserContactInfo",

	//UserInvitation
	BrowseUserInvitation = "BrowseUserInvitation",
	EditUserInvitation = "EditUserInvitation",
	DeleteUserInvitation = "DeleteUserInvitation",

	//BrowseDataTree
	BrowseBrowseDataTreeConfig = "BrowseBrowseDataTreeConfig",
	BrowseBrowseDataTree = "BrowseBrowseDataTree",

	//PortofolioConfig
	BrowsePortofolioConfig = "BrowsePortofolioConfig",

	//Dashboard
	GetDashboard = "GetDashboard",


	//User
	BrowseUserSettings = "BrowseUserSettings",
	EditUserSettings = "EditUserSettings",
	DeleteUserSettings = "DeleteUserSettings",


	//DynamicPage
	BrowseDynamicPage = "BrowseDynamicPage",
	EditDynamicPage = "EditDynamicPage",
	DeleteDynamicPage = "DeleteDynamicPage",

	//ExternalToken
	BrowseExternalToken = "BrowseExternalToken",
	EditExternalToken = "EditExternalToken",
	DeleteExternalToken = "DeleteExternalToken",
	CreateChartExternalToken = "CreateChartExternalToken",
	CreateDashboardExternalToken = "CreateDashboardExternalToken",

	//Tenant
	BrowseTenant = "BrowseTenant",
	EditTenant = "EditTenant",
	DeleteTenant = "DeleteTenant",
	AllowNoTenant = "AllowNoTenant",

	//TenantUser
	BrowseTenantUser = "BrowseTenantUser",
	EditTenantUser = "EditTenantUser",
	DeleteTenantUser = "DeleteTenantUser",
	//Tenant Request
	BrowseTenantRequest = "BrowseTenantRequest",
	EditTenantRequest = "EditTenantRequest",
	CreateTenantRequest = "CreateTenantRequest",
	DeleteTenantRequest = "DeleteTenantRequest",
	ApproveTenantRequest = "ApproveTenantRequest",
	RejectTenantRequest = "RejectTenantRequest",

	//Dataset
	BrowseDataset = "BrowseDataset",
	EditDataset = "EditDataset",
	DeleteDataset = "DeleteDataset",
	//DetailItem
	BrowseDetailItem = "BrowseDetailItem",
	EditDetailItem = "EditDetailItem",
	DeleteDetailItem = "DeleteDetailItem",
	//MasterItem
	BrowseMasterItem = "BrowseMasterItem",
	EditMasterItem = "EditMasterItem",
	DeleteMasterItem = "DeleteMasterItem",
	//IndicatorPoint
	BrowseIndicatorPoint = "BrowseIndicatorPoint",
	EditIndicatorPoint = "EditIndicatorPoint",
	DeleteIndicatorPoint = "DeleteIndicatorPoint",
	//DataGroupRequest
	BrowseDataGroupRequest = "BrowseDataGroupRequest",
	EditDataGroupRequest = "EditDataGroupRequest",
	DeleteDataGroupRequest = "DeleteDataGroupRequest",
	CreateDataGroupRequest = "CreateDataGroupRequest",


	//Blueprint Request
	BrowseBlueprintRequest = "BrowseBlueprintRequest",
	CreateBlueprintRequest = "CreateBlueprintRequest",
	UpdateBlueprintRequest = "UpdateBlueprintRequest",
	DeleteBlueprintRequest = "DeleteBlueprintRequest",

	//Create Blueprint
	CreateBlueprint = "CreateBlueprint",

	//Notification
	EditNotification = "EditNotification",

	// UI Pages
	ViewDatasetPage = "ViewDatasetPage",
	ViewIndicatorPage = "ViewIndicatorPage",
	ViewIndicatorReportsPage = "ViewIndicatorReportsPage",
	ViewIndicatorReportsDashboardPage = "ViewIndicatorReportsDashboardPage",
	ViewDataAccessRequestPage = "ViewDataAccessRequestPage",
	ViewMyDataAccessRequestPage = "ViewMyDataAccessRequestPage",
	ViewTenantPage = "ViewTenantPage",
	ViewTenantRequestPage = "ViewTenantRequestPage",
	ViewUsersPage = "ViewUsersPage",
	ViewApiClientsPage = "ViewApiClientsPage",
	ViewRoleAssignmentPage = "ViewRoleAssignmentPage",
	ViewAccessTokensPage = "ViewAccessTokensPage",
	ViewUserInvitationPage = "ViewUserInvitationPage",
	ViewUserProfilePage = "ViewUserProfilePage",
	ViewForgetMeRequestPage = "ViewForgetMeRequestPage",
	ViewWhatYouKnowAboutMeRequestPage = "ViewWhatYouKnowAboutMeRequestPage",
	ViewMasterItemPage = "ViewMasterItemPage",
	ViewTenantConfigurationPage = "ViewTenantConfigurationPage",
	ViewNotificationPage = "ViewNotificationPage",
	ViewNotificationEventRulePage = "ViewNotificationEventRulePage",
	ViewInAppNotificationPage = "ViewInAppNotificationPage",
	ViewNotificationTemplatePage = "ViewNotificationTemplatePage",
	ViewBlueprintRequestPage = "ViewBlueprintRequestPage",
	DownloadReport = "DownloadReport",
	ViewBlueprintTemplatePage = "ViewBlueprintTemplatePage",
	ViewDynamicPage = "ViewDynamicPage",
	ViewExternalTokenPage = "ViewExternalTokenPage",
}

