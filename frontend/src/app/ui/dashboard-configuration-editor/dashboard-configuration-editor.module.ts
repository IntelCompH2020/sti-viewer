import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { DashboardConfigurationEditorRoutingModule } from './dashboard-configuration-editor-routing.module';
import {MatPaginatorModule} from '@angular/material/paginator';
import { DashboardConfigurationEditorComponent } from './editor/dashboard-configuration-editor.component';
import { DashboardConfigurationTabEditorComponent } from './editor/tab-editor/dashboard-configuration-tab-editor.component';
import { DashboardConfigurationChartGroupEditorComponent } from './editor/chart-group-editor/dashboard-configuration-chart-group-editor.component';
import { DashboardConfigurationChartEditorComponent } from './editor/chart-editor/dashboard-configuration-chart-editor.component';
import { DashboardConfigurationChartDisplayEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-configuration-display-editor.component';
import { DashboardConfigurationDataFetchingEditorComponent } from './editor/chart-editor/data-fetching/dashboard-configuration-data-fetching-editor.component';
import { DashboardConfigurationChartGeneralInfoEditorComponent } from './editor/chart-editor/general-info/dashboard-configuration-chart-general-info-editor.component';
import { FilterDialogCompoennt } from './editor/chart-editor/chart-display-configuration/filter-dialog/filter-dialog.component';
import { DashboardConfigurationDataMappingEditorComponent } from './editor/chart-editor/data-mapping/dashboard-configuration-data-mapping-editor.component';
import { ConnectionTestPipe, SerieValueTestPipe } from './editor/chart-editor/data-mapping/serie-value-test.pipe';
import { ValueTestDialogComponent } from './editor/chart-editor/data-mapping/value-test-dialog/value-test-dialog.component';
import { SerieValueNumberFormattter } from './editor/chart-editor/data-mapping/value-formatter-types/number-formattter/value-number-formattter.component';
import { SerieValueFormatterTypesComponent } from './editor/chart-editor/data-mapping/value-formatter-types/value-formatter-types.component';
import { DashboardConfigChartTypeEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/chart-type-editor.component';
import { DashboardConfigurationLineChartComponent } from './editor/chart-editor/chart-display-configuration/chart-type/line-chart/line-chart-configuration.component';
import { XaxisEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/x-axis-config/x-axis-editor.component';
import { YaxisEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/y-axis-config/y-axis-editor.component';
import { DashboardConfigurationBarChartComponent } from './editor/chart-editor/chart-display-configuration/chart-type/bar-chart/bar-chart-configuration.component';
import { PieChartConfigurationComponent } from './editor/chart-editor/chart-display-configuration/chart-type/pie-chart/pie-chart-configuration.component';
import { PolarBarConfigurationEditor } from './editor/chart-editor/chart-display-configuration/chart-type/polar-bar/polar-bar-configuration.component';
import { DataZoomEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/data-zoom/data-zoom-editor.component';
import { TreeMapConfigurationEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/tree-map/tree-map-configuration.component';
import { MapConfigurationEditorComponent } from './editor/chart-editor/chart-display-configuration/chart-type/map-chart/map-chart-configuration.component';
import { EditMapMappingDialogComponent } from './editor/chart-editor/chart-display-configuration/chart-type/map-chart/edit-map-mapping-dialog/edit-map-mapping-dialog.component';
import { ChartIconPipe } from './editor/chart-group-editor/chart-icon.pipe';
import {DragDropModule} from '@angular/cdk/drag-drop';
import { DashboardConfigurationChartPreviewEditorComponent } from './editor/chart-previewer/dashboard-configuration-chart-preview.component';
// import { NgxEchartsModule } from 'ngx-echarts';
import { BucketConfigEditorComponent } from './editor/chart-editor/data-fetching/bucket-editor/bucket-config-editor.component';
import { MetricConfigEditorComponent } from './editor/chart-editor/data-fetching/metric-editor/metric-config-editor.component';
import { TermsBucketConfigEditorComponent } from './editor/chart-editor/data-fetching/bucket-editor/terms-bucket/terms-bucket-config-editor.component';
import { DataHistogramBucketConfigEditorComponent } from './editor/chart-editor/data-fetching/bucket-editor/data-histogram/data-histogram-bucket-config-editor.component';
import { CompositeBucketConfigEditorComponent } from './editor/chart-editor/data-fetching/bucket-editor/composite-bucket/composite-bucket-config-editor.component';
import { AfterKeyEditorDialogComponent } from './editor/chart-editor/data-fetching/bucket-editor/composite-bucket/after-key-editor-dialog/after-key-editor-dialog.component';
import { StaticFiltersConfigEditorComponent } from './editor/chart-editor/data-fetching/static-filters-editor/static-filters-config-editor.component';
import { RawDataRequestConfigEditorComponent } from './editor/chart-editor/data-fetching/raw-data-request-editor/raw-data-request-config-editor.component';
import { DashboardConfigurationListingComponent } from './listing/dashboard-configuration-listing.component';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { DashboardConfigurationListingFiltersComponent } from './listing/filters/dashboard-configuration-listing-filters.component';
import { UserSettingsService } from '@app/core/services/http/user-settings.service';
import { BrowseDataTreeEditorComponent } from './browse-data-tree-editor/browse-data-tree-editor.component';
import { FieldModelEditorComponent } from './browse-data-tree-editor/field-model-editor/field-editor-model.component';
import { AltTextEditorComponent } from './browse-data-tree-editor/alt-text-editor/alt-text-editor.component';
import { ValueRangeEditorComponent } from './browse-data-tree-editor/value-range-editor/value-range-editor.component';
import { OperationEditorComponent } from './browse-data-tree-editor/operations-editor/operation-editor.component';
import { LevelConfigEditorComponent } from './browse-data-tree-editor/level-configs-editor/level-config-editor.component';
import { DataTreeConfigurationEditorWrapperComponent } from './browse-data-tree-editor/editor-wrapper/data-tree-configuration-editor-wrapper.component';
import { SearchConfigurationEditorComponent } from './search-configuration-editor/search-configuration-editor.component';
import { SearchConfigurationStaticFiltersEditorComponent } from './search-configuration-editor/static-filters-editor/static-filters-editor.component';
import { ViewConfigEditorComponent } from './search-configuration-editor/view-config-editor/view-config-editor.component';
import { SearchConfigurationDashboardFiltersEditorComponent } from './search-configuration-editor/dashboard-filters/dashboard-filters-editor.component';
import { RawEditorDialogComponent } from './raw-editor/raw-editor-dialog.component';
import { SankeyConfigurationEditor } from './editor/chart-editor/chart-display-configuration/chart-type/sankey/sankey-configuration.component';
import { IndicatorDashboardModule } from '../indicator-dashboard/indicator-dashboard.module';
import { ChartPreviewService } from './editor/chart-preview.service';
import { FormattingModule } from '@app/core/formatting/formatting.module';
import { GaugeGroupEditorComponent } from './editor/gauge-group-editor/gauge-group-editor.component';
import { GaugeEditorComponent } from './editor/gauge-editor/gauge-editor.component';
import { GaugeGeneralInfoEditorComponent } from './editor/gauge-editor/general-info/gauge-general-info-editor.component';
import { GaugeDisplayEditorConfigurationComponent } from './editor/gauge-editor/gauge-display/gauge-display-editor.component';
import { GaugeTypeEditorComponent } from './editor/gauge-editor/gauge-display/gauge-type-editor/gauge-type-editor.component';

@NgModule({
	imports: [
		CommonUiModule,
		CommonFormsModule,
		DashboardConfigurationEditorRoutingModule,
		MatPaginatorModule,
		DragDropModule,
		// NgxEchartsModule.forRoot({
        //     echarts: () => import('echarts')
        // }),

		// * Listing
		ListingModule,
		TextFilterModule,
		UserSettingsModule,
		IndicatorDashboardModule,
		FormattingModule
	],
	declarations: [
		DashboardConfigurationEditorComponent,
		DashboardConfigurationTabEditorComponent,
		DashboardConfigurationChartGroupEditorComponent,
		DashboardConfigurationChartEditorComponent,
		DashboardConfigurationChartDisplayEditorComponent,
		DashboardConfigurationDataFetchingEditorComponent,
		DashboardConfigurationChartGeneralInfoEditorComponent,
		DashboardConfigurationDataMappingEditorComponent,
		DashboardConfigChartTypeEditorComponent,
		FilterDialogCompoennt,
		SerieValueFormatterTypesComponent,
		SerieValueNumberFormattter,
		ValueTestDialogComponent,
		DashboardConfigurationLineChartComponent,
		XaxisEditorComponent,
		DashboardConfigurationBarChartComponent,
		YaxisEditorComponent,
		SerieValueTestPipe,
		ConnectionTestPipe,
		PieChartConfigurationComponent,
		PolarBarConfigurationEditor,
		DataZoomEditorComponent,
		TreeMapConfigurationEditorComponent,
		MapConfigurationEditorComponent,
		EditMapMappingDialogComponent,
		ChartIconPipe,
		DashboardConfigurationChartPreviewEditorComponent,
		BucketConfigEditorComponent,
		MetricConfigEditorComponent,
		TermsBucketConfigEditorComponent,
		DataHistogramBucketConfigEditorComponent,
		CompositeBucketConfigEditorComponent,
		AfterKeyEditorDialogComponent,
		StaticFiltersConfigEditorComponent,
		RawDataRequestConfigEditorComponent,
		BrowseDataTreeEditorComponent,
		FieldModelEditorComponent,
		AltTextEditorComponent,
		ValueRangeEditorComponent,
		OperationEditorComponent,
		LevelConfigEditorComponent,
		DataTreeConfigurationEditorWrapperComponent,
		SearchConfigurationEditorComponent,
		ViewConfigEditorComponent,
		SearchConfigurationDashboardFiltersEditorComponent,
		RawEditorDialogComponent,
		SankeyConfigurationEditor,
		SearchConfigurationStaticFiltersEditorComponent,
		GaugeGroupEditorComponent,
		GaugeEditorComponent,
		GaugeGeneralInfoEditorComponent,
		GaugeDisplayEditorConfigurationComponent,
		GaugeTypeEditorComponent,

		//* listing

		DashboardConfigurationListingComponent,
		DashboardConfigurationListingFiltersComponent
	],
	providers:[
		UserSettingsService,
		ChartPreviewService
	]
})
export class DashboardConfigurationModule { }
