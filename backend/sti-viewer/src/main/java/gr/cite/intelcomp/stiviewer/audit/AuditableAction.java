package gr.cite.intelcomp.stiviewer.audit;

import gr.cite.tools.logging.EventId;

public class AuditableAction {
	public static final EventId IdentityTracking_Action = new EventId(1000, "IdentityTracking_Action");
	public static final EventId IdentityTracking_User_Persist = new EventId(1001, "IdentityTracking_User_Persist");
	public static final EventId IdentityTracking_ForgetMe_Request = new EventId(1002, "IdentityTracking_ForgetMe_Request");
	public static final EventId IdentityTracking_ForgetMe_Validate = new EventId(1003, "IdentityTracking_ForgetMe_Validate");
	public static final EventId IdentityTracking_ForgetMe_Stamp = new EventId(1004, "IdentityTracking_ForgetMe_Stamp");
	public static final EventId Tenant_Query = new EventId(2000, "Tenant_Query");
	public static final EventId Tenant_Lookup = new EventId(2001, "Tenant_Lookup");
	public static final EventId Tenant_Persist = new EventId(2002, "Tenant_Persist");
	public static final EventId Tenant_Delete = new EventId(2003, "Tenant_Delete");
	public static final EventId Tenant_Reactivate = new EventId(2004, "Tenant_Reactivate");
	public static final EventId Tenant_Invite = new EventId(2005, "Tenant_Invite");
	public static final EventId Dataset_Query = new EventId(3000, "Dataset_Query");
	public static final EventId Dataset_Lookup = new EventId(3001, "Dataset_Lookup");
	public static final EventId Dataset_Persist = new EventId(3002, "Dataset_Persist");
	public static final EventId Dataset_Delete = new EventId(3003, "Dataset_Delete");
	public static final EventId Dataset_GenerateReport = new EventId(3004, "Dataset_GenerateReport");
	public static final EventId Dataset_GenerateAndDownloadReport = new EventId(3005, "Dataset_GenerateAndDownloadReport");

	public static final EventId Indicator_Query = new EventId(3010, "Indicator_Query");
	public static final EventId Indicator_Lookup = new EventId(3011, "Indicator_Lookup");
	public static final EventId Indicator_Persist = new EventId(3012, "Indicator_Persist");
	public static final EventId Indicator_Delete = new EventId(3013, "Indicator_Delete");
	public static final EventId Indicator_ElasticPersist = new EventId(3012, "Indicator_ElasticPersist");
	public static final EventId Indicator_ElasticDelete = new EventId(3013, "Indicator_ElasticDelete");
	public static final EventId Indicator_ElasticReset = new EventId(3014, "Indicator_ElasticReset");

	public static final EventId Data_Access_Request_Query = new EventId(3020, "Data_Access_Request_Query");
	public static final EventId Data_Access_Request_Lookup = new EventId(3021, "Data_Access_Request_Lookup");
	public static final EventId Data_Access_Request_Persist = new EventId(3022, "Data_Access_Request_Persist");
	public static final EventId Data_Access_Request_Delete = new EventId(3023, "Data_Access_Request_Delete");
	public static final EventId Data_Access_Request_PersistStatus = new EventId(3024, "Data_Access_Request_PersistStatus");
	public static final EventId Data_Access_Request_GetIndicatorGroupAccessConfigView = new EventId(3025, "Data_Access_Request_GetIndicatorGroupAccessConfigView");
	
	public static final EventId MasterItem_Query = new EventId(4000, "MasterItem_Query");
	public static final EventId MasterItem_Lookup = new EventId(4001, "MasterItem_Lookup");
	public static final EventId MasterItem_Persist = new EventId(4002, "MasterItem_Persist");
	public static final EventId MasterItem_Delete = new EventId(4003, "MasterItem_Delete");
	public static final EventId Principal_Lookup = new EventId(6000, "Principal_Lookup");
	public static final EventId Tenants_Lookup = new EventId(6001, "Tenants_Lookup");

	public static final EventId ForgetMe_Persist = new EventId(7000, "ForgetMe_Persist");
	public static final EventId ForgetMe_Query = new EventId(7001, "ForgetMe_Query");
	public static final EventId ForgetMe_Query_Mine = new EventId(7002, "ForgetMe_Query_Mine");
	public static final EventId ForgetMe_Delete = new EventId(7003, "ForgetMe_Delete");

	public static final EventId WhatYouKnowAboutMe_Persist = new EventId(8000, "WhatYouKnowAboutMe_Persist");
	public static final EventId WhatYouKnowAboutMe_Query = new EventId(8001, "WhatYouKnowAboutMe_Query");
	public static final EventId WhatYouKnowAboutMe_Query_Mine = new EventId(8002, "WhatYouKnowAboutMe_Query_Mine");
	public static final EventId WhatYouKnowAboutMe_Delete = new EventId(8003, "WhatYouKnowAboutMe_Delete");
	public static final EventId WhatYouKnowAboutMe_Download = new EventId(8004, "WhatYouKnowAboutMe_Download");

	public static final EventId BlueprintRequest_Generation = new EventId(9000, "BlueprintRequest_Generation");
	public static final EventId BlueprintRequest_Query = new EventId(9001, "BlueprintRequest_Query");
	public static final EventId BlueprintRequest_Delete = new EventId(9002, "BlueprintRequest_Delete");


	public static final EventId User_Query = new EventId(10000, "User_Query");
	public static final EventId User_Lookup = new EventId(10001, "User_Lookup");
	public static final EventId User_Persist = new EventId(10002, "User_Persist");
	public static final EventId User_Delete = new EventId(10003, "User_Delete");

	public static final EventId TenantRequest_Query = new EventId(11001, "TenantRequest_Query");
	public static final EventId TenantRequest_Lookup = new EventId(11002, "TenantRequest_Lookup");
	public static final EventId TenantRequest_Persist = new EventId(11003, "TenantRequest_Persist");
	public static final EventId TenantRequest_Delete = new EventId(11004, "TTenantRequest_Delete");
	public static final EventId TenantRequest_PersistStatus = new EventId(11004, "TenantRequest_PersistStatus");

	public static final EventId User_Contact_Info_Query = new EventId(12000, "User_Contact_Info_Query");
	public static final EventId User_Contact_Info_Lookup = new EventId(12001, "User_Contact_Info_Lookup");
	public static final EventId User_Contact_Info_Persist = new EventId(12002, "User_Contact_Info_Persist");
	public static final EventId User_Contact_Info_Delete = new EventId(12003, "User_Contact_Info_Delete");

	public static final EventId User_Invitation_Query = new EventId(13000, "User_Invitation_Query");
	public static final EventId User_Invitation_Lookup = new EventId(13001, "User_Invitation_Lookup");
	public static final EventId User_Invitation_Persist = new EventId(13002, "User_Invitation_Persist");
	public static final EventId User_Invitation_Delete = new EventId(13003, "User_Invitation_Delete");

	public static final EventId User_Settings_Query = new EventId(14000, "User_Settings_Query");
	public static final EventId User_Settings_Lookup = new EventId(14001, "User_Settings_Lookup");
	public static final EventId User_Settings_Persist = new EventId(14002, "User_Settings_Persist");
	public static final EventId User_Settings_Delete = new EventId(14003, "User_Settings_Delete");

	public static final EventId Indicator_Point_Query = new EventId(15000, "Indicator_Point_Query");
	public static final EventId Indicator_Point_Lookup = new EventId(15001, "Indicator_Point_Lookup");
	public static final EventId Indicator_Point_Persist = new EventId(15002, "Indicator_Point_Persist");
	public static final EventId Indicator_Point_Delete = new EventId(15003, "Indicator_Point_Delete");
	public static final EventId Indicator_Point_Bulk_Persist = new EventId(15004, "Indicator_Point_Bulk_Persist");
	public static final EventId Indicator_Point_Report = new EventId(15005, "Indicator_Point_Report");
	public static final EventId Indicator_Point_QueryDistinct = new EventId(15006, "Indicator_Point_QueryDistinct");
	public static final EventId Indicator_Point_ExportXlsx = new EventId(15007, "Indicator_Point_ExportXlsx");
	public static final EventId Indicator_Point_GetGlobalSearchConfig = new EventId(15008, "Indicator_Point_GetGlobalSearchConfig");
	public static final EventId Indicator_Point_ExportJson = new EventId(15009, "Indicator_Point_ExportJson");

	public static final EventId Indicator_Elastic_Query = new EventId(16000, "Indicator_Elastic_Query");

	public static final EventId DataTreeConfig_MyConfigs = new EventId(17000, "DataTreeConfig_MyConfigs");
	public static final EventId DataTreeConfig_QueryLevel = new EventId(17001, "DataTreeConfig_QueryLevel");
	public static final EventId DataTreeConfig_GetMyConfigByKey = new EventId(17002, "DataTreeConfig_GetMyConfigByKey");
	public static final EventId DataTreeConfig_UpdateLastAccess = new EventId(17003, "DataTreeConfig_UpdateLastAccess");
	
	public static final EventId Bookmark_QueryMine = new EventId(18000, "Bookmark_QueryMine");
	public static final EventId Bookmark_LookupMine = new EventId(18001, "Bookmark_LookupMine");
	public static final EventId Bookmark_DeleteMine = new EventId(18003, "Bookmark_DeleteMine");
	public static final EventId Bookmark_PersistMine = new EventId(18004, "Bookmark_PersistMine");
	public static final EventId Bookmark_GetBookmarkByHash = new EventId(18005, "Bookmark_GetBookmarkByHash");


	public static final EventId Indicator_Group_Query = new EventId(19000, "Indicator_Group_Query");
	public static final EventId Indicator_Group_Lookup = new EventId(19001, "Indicator_Group_Lookup");
	public static final EventId Indicator_Group_Persist = new EventId(19002, "Indicator_Group_Persist");
	public static final EventId Indicator_Group_Delete = new EventId(19003, "Indicator_Group_Delete");
	public static final EventId Indicator_Group_Get_All = new EventId(19004, "Indicator_Group_Get_All");
	public static final EventId Indicator_Group_LookupByCode = new EventId(19005, "Indicator_Group_LookupByCode");


	public static final EventId Data_Group_Request_Query = new EventId(20000, "Data_Group_Request_Query");
	public static final EventId Data_Group_Request_Lookup = new EventId(20001, "Data_Group_Request_Lookup");
	public static final EventId Data_Group_Request_Persist = new EventId(20002, "Data_Group_Request_Persist");
	public static final EventId Data_Group_Request_Delete = new EventId(20003, "Data_Group_Request_Delete");
	public static final EventId Data_Group_Request_PersistStatus = new EventId(20004, "Data_Group_Request_PersistStatus");
	public static final EventId Data_Group_Request_PersistName = new EventId(20005, "Data_Group_Request_PersistName");

	public static final EventId Notification_Persist = new EventId(21001, "Notification_Persist");


	public static final EventId Scheduled_Event_Query = new EventId(22000, "Scheduled_Event_Query");
	public static final EventId Scheduled_Event_Lookup = new EventId(22001, "Scheduled_Event_Lookup");
	public static final EventId Scheduled_Event_Persist = new EventId(22002, "Scheduled_Event_Persist");
	public static final EventId Scheduled_Event_Delete = new EventId(22003, "Scheduled_Event_Delete");
	public static final EventId Scheduled_Event_Canceled = new EventId(22004, "Scheduled_Event_Canceled");
	public static final EventId Scheduled_Event_Run = new EventId(22005, "Scheduled_Event_Run");

	public static final EventId PortofolioConfig_GetMyConfigs = new EventId(230000, "PortofolioConfig_GetMyConfigs");
	public static final EventId PortofolioConfig_GetMyConfigByKey = new EventId(230001, "PortofolioConfig_GetMyConfigByKey");
	
	public static final EventId DynamicPage_Query = new EventId(240000, "Page_Query");
	public static final EventId DynamicPage_Lookup = new EventId(240001, "Page_Lookup");
	public static final EventId DynamicPage_Persist = new EventId(240002, "Page_Persist");
	public static final EventId DynamicPage_Delete = new EventId(240003, "Page_Delete");
	public static final EventId DynamicPage_AllowedPageMenuItems = new EventId(240004, "Page_AllowedPageMenuItems");
	public static final EventId DynamicPage_GetPageContent = new EventId(240005, "Page_GetPageContent");

}
