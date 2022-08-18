package gr.cite.user.audit;

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


	public static final EventId User_Contact_Info_Query = new EventId(12000, "User_Contact_Info_Query");
	public static final EventId User_Contact_Info_Lookup = new EventId(12001, "User_Contact_Info_Lookup");
	public static final EventId User_Contact_Info_Persist = new EventId(12002, "User_Contact_Info_Persist");
	public static final EventId User_Contact_Info_Delete = new EventId(12003, "User_Contact_Info_Delete");


	public static final EventId User_Settings_Query = new EventId(14000, "User_Settings_Query");
	public static final EventId User_Settings_Lookup = new EventId(14001, "User_Settings_Lookup");
	public static final EventId User_Settings_Persist = new EventId(14002, "User_Settings_Persist");
	public static final EventId User_Settings_Delete = new EventId(14003, "User_Settings_Delete");



}
