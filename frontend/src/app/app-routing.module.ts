import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';

const appRoutes: Routes = [

	{ path: '', redirectTo: 'home', pathMatch: 'full' },
	{
		path: 'embedded-dashboard',
		loadChildren: () => import('@app/ui/embedded-dashboard/embedded-dashboard.module').then(m => m.EmbeddedDashboardModule)
	},
	{
		path: 'shared-graph',
		loadChildren: () => import('@app/ui/shared-graph/shared-graph.module').then(m => m.SharedGraphModule)
	},
	{
		path: 'shared-dashboard',
		loadChildren: () => import('@app/ui/shared-dashboard/shared-dashboard.module').then(m => m.SharedDashboardModule)
	},
	{
		path: 'home',
		canLoad: [AuthGuard],
		loadChildren: () => import('@app/ui/home/home.module').then(m => m.HomeModule)
	},
	{
		path: 'login',
		loadChildren: () => import('@idp-service/ui/login/login.module').then(m => m.LoginModule)
	},
	{
		path: 'users',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewUsersPage]
			}
		},
		loadChildren: () => import('@user-service/ui/users/user.module').then(m => m.UserModule)
	},
	{
		path: 'search',
		canLoad: [AuthGuard],
		loadChildren: () => import('@app/ui/search/search.module').then(m => m.SearchModule)
	},
	{
		path: 'config-editor',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.EditUserSettings] // todo new permission
			}
		},
		loadChildren: () => import('@app/ui/dashboard-configuration-editor/dashboard-configuration-editor.module').then(m => m.DashboardConfigurationModule)
	},
	{
		path: 'datasets',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewDatasetPage]
			}
		},
		loadChildren: () => import('@app/ui/dataset/dataset.module').then(m => m.DatasetModule)
	},
	{
		path: 'indicators',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewIndicatorPage]
			}
		},
		loadChildren: () => import('@app/ui/indicator/indicator.module').then(m => m.IndicatorModule)
	},
	{
		path: 'indicator-report',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewIndicatorReportsPage]
			}
		},
		loadChildren: () => import('@app/ui/indicator-report/indicator-report.module').then(m => m.IndicatorReportModule)
	},
	// {
	// 	path: 'indicator-dashboard',
	// 	canLoad: [AuthGuard],
	// 	data: {
	// 		authContext: {
	// 			permissions: [AppPermission.ViewIndicatorReportsDashboardPage]
	// 		}
	// 	},
	// 	loadChildren: () => import('@app/ui/indicator-dashboard/indicator-dashboard.module').then(m => m.IndicatorDashboardModule)
	// },
	{
		path: 'data-access-requests',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewDataAccessRequestPage]
			}
		},
		loadChildren: () => import('@app/ui/data-access-request/data-access-request.module').then(m => m.DataAccessRequestModule)
	},
	{
		path: 'my-data-access-requests',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewMyDataAccessRequestPage]
			}
		},
		loadChildren: () => import('@app/ui/my-data-access-requests/my-data-access-request.module').then(m => m.MyDataAccessRequestModule)
	},
	{
		path: 'tenants',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewTenantPage]
			}
		},
		loadChildren: () => import('@app/ui/tenant/tenant.module').then(m => m.TenantModule)
	}, {
		path: 'tenant-requests',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewTenantRequestPage]
			}
		},
		loadChildren: () => import('@app/ui/tenant-request/tenant-request.module').then(m => m.TenantRequestModule)
	},
	{
		path: 'external-tokens',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewExternalTokenPage]
			}
		},
		loadChildren: () => import('@app/ui/external-token/external-token.module').then(m => m.ExternalTokenModule)
	},
	{
		path: "simple-page",
		canLoad: [AuthGuard],
		data: {},
		loadChildren: () =>
			import("@app/ui/simple-page/simple-page.module").then(
				(m) => m.SimplePageModule
			),
	},
	{
		path: 'download-report',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.DownloadReport]
			}
		},
		loadChildren: () => import('@app/ui/download-report/download-report.module').then(m => m.DownloadReportModule)
	},
	{
		path: 'master-items',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewMasterItemPage]
			}
		},
		loadChildren: () => import('@app/ui/master-item/master-item.module').then(m => m.MasterItemModule)
	},
	{
		path: 'dynamic-pages',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewDynamicPage]
			}
		},
		loadChildren: () => import('@app/ui/dynamic-page/dynamic-page.module').then(m => m.DynamicPageModule)
	},
	{
		path: 'notifications',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewNotificationPage]
			}
		},
		loadChildren: () => import('@notification-service/ui/notification/notification.module').then(m => m.NotificationModule)
	},
	{
		path: 'inapp-notifications',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewInAppNotificationPage]
			},
			breadcrumb: {
				languageKey: 'APP.NAVIGATION.INAPP-NOTIFICATIONS',
				permissions: [AppPermission.ViewInAppNotificationPage],
			}
		},
		loadChildren: () => import('@notification-service/ui/inapp-notification/inapp-notification.module').then(m => m.InAppNotificationModule)
	},
	{
		path: 'notification-templates',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewNotificationTemplatePage]
			}
		},
		loadChildren: () => import('@notification-service/ui/notification-template/notification-template.module').then(m => m.NotificationTemplateModule)
	},
	{
		path: 'user-profile',
		canLoad: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewUserProfilePage]
			}
		},
		loadChildren: () => import('@user-service/ui/use-profile/user-profile.module').then(m => m.UserProfileModule)
	},


	{ path: 'logout', loadChildren: () => import('@idp-service/ui/logout/logout.module').then(m => m.LogoutModule) },
	{ path: 'unauthorized', loadChildren: () => import('@common/unauthorized/unauthorized.module').then(m => m.UnauthorizedModule) },
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forRoot(appRoutes, { relativeLinkResolution: 'legacy' })],
	exports: [RouterModule],
})
export class AppRoutingModule { }
