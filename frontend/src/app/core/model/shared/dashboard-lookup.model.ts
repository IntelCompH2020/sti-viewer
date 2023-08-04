import { IndicatorPointReportLookupFilter } from "@app/core/query/indicator-point-report.lookup";
import { Guid } from "@common/types/guid";

export interface DashboardLookup{
	token: string;
	dashboardId: string;
}

export interface PublicIndicatorPointReportLookup extends IndicatorPointReportLookupFilter{
	token: string;
	dashboardId: string;
	chartId: string;
	indicatorId: Guid;
}

