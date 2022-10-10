import { Bucket, CompositeBucket, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { Metric } from "@app/core/model/metic/metric.model";
import { RawDataRequest } from "@app/core/query/indicator-point-report.lookup";

export interface IndicatorDashboardConfig {
	id: string;
	tabs: IndicatorDashboardTabConfig[];
}

export interface IndicatorDashboardTabConfig {
	name: string;
	chartGroups: IndicatorDashboardChartGroupConfig[];
}

export interface IndicatorDashboardChartGroupConfig {
	name: string;
	charts: IndicatorDashboardChart [];
}

export type IndicatorDashboardChart = IndicatorDashboardLineChartConfig | IndicatorDashboardBarChartConfig | IndicatorDashboardScatterChartConfig | IndicatorDashboardPieChartConfig | IndicatorDashboardPolarBarChartConfig | IndicatorDashboardMapChartConfig | IndicatorDashboardSankeyChartConfig;


//TODO LINECHART BARCHART ETC UI MORE PRESENTATION CONFIGURATION
export interface IndicatorDashboardSankeyChartConfig extends BaseIndicatorDashboardChartConfig {
	connectionExtractor: ConnectionExtractor;
}


export interface ConnectionExtractor{
	sourceKeyExtractor: string;//* search  source from group items
	targetKeyExtractor: string //* search target from group items
	valueKeyExtractor: string; // * extract value from values

	valueTests?: Record<string, string>;
	groupTests?: Record<string, string>;
}
export interface IndicatorDashboardLineChartConfig extends BaseIndicatorDashboardChartConfig {
	xAxis:IndicatorDashboardChartXAxisConfig;
	yAxis: IndicatorDashboardChartYAxisConfig;
	stack?: boolean;
	horizontal?: boolean;
	areaStyle?: object;
	dataZoom?: DataZoom;
}

export interface IndicatorDashboardBarChartConfig extends IndicatorDashboardLineChartConfig, Omit <IndicatorDashboardLineChartConfig, 'type'> {
	type: IndicatorDashboardChartType.Bar;
}
export interface IndicatorDashboardScatterChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.Scatter;
	
}
export interface IndicatorDashboardGraphChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.Graph;
	
}
export interface IndicatorDashboardPieChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.Pie;
	roseType?:string;
}
export interface IndicatorDashboardPolarBarChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.PolarBar;
	dataZoom?: DataZoom;
	radiusAxis: PolarBarRadiusAxis;
}
export interface IndicatorDashboardMapChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.Map;
	mapChartConfig: MapConfig;

	// * <Backend Name (backend response), JSON map config name>
	countryNameMapping?: Record<string, string>;
}
export interface IndicatorDashboardTreeMapChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	type: IndicatorDashboardChartType.TreeMap;
	treeName?: string;
	toolTip?:{
		name?: string;
		metricName?: string;
	},
	treeColors?: Record<string, string>;
}
export interface MapConfig{
	high?:{
		color?: string
		text?:string
	}
	low?:{
		color?: string
		text?:string
	}
}

export interface PolarBarRadiusAxis{
	name: string;
}

export interface BaseIndicatorDashboardChartConfig{
	indicatorId: string | 'inherited';
	type: IndicatorDashboardChartType;
	name?: string;
	chartName?: string;
	chartSubtitle?: string;
	reverseValues?: boolean;
	description: string;
	metrics?: IndicatorConfigMetric[];
	bucket?: IndicatorConfigBucket;
	legend?: LegendConfig;
	filters?: ChartFilter[];
	chartDownloadImage?:{};
	chartDownloadData?:{};
	staticFilters?:{
		keywordsFilters?:{
			field: string;
			value:  string[];
		}[];
	}
	rawDataRequest?:RawDataRequest;

	// * extract data from response

	labelSortKey:string; //* sort results based on a value found in group.items (picked by labelSorkKey) !!required
	labelsTransform?: FieldFormatterConfig; //*  provide transformation configuration for label axis (x axis) if needed
	//* describe how to extract series from results
	series?: {
		nested?:{
			type?: 'line' | 'bar';
		};
		splitSeries?: {key: string}[]

		// * describe where to find x axis data (labels) 
		label:{
			color?:string; //* color for the serie produced (not taken into account if there are split series)
			name: string; // * name of serie produced (not taken into account if there is splitseries defined)
			labelKey: string; //* key with which we are extracting label from group.items
		}

		// * describe how to extract value from values
		values:{
			formatter?: SeriesValueFormatter;
			valueKey: string; //* which field we consider as a value from values[x] object
			tests?:Record<string, string>[] //* a list of tests that need to be validated (AND LOGIC only). the perfect match object which we exctract the value must have key-value pair that matches extact with the tests
			groupTests?:Record<string, string>[]// * Testing group values (provided in labels object)
		}
	}[];

}


export interface SeriesValueFormatter{
	type: SeriesValueFormatterType;
}

export enum SeriesValueFormatterType{
	Number = 'number'
}


export interface SeriesValueNumberFormatter extends SeriesValueFormatter, Omit<SeriesValueFormatter, 'type'>{
	type: SeriesValueFormatterType.Number;
	decimalAccuracy?: number;
}


export type ChartFilter = ChartFilterSlider | ChartFilterSelect;

export interface ChartFilterSelect extends ChartFilterBase, Omit<ChartFilterBase, 'type'>{
	type: ChartFilterType.Select;
	multiple?: boolean;
}
export interface ChartFilterSlider extends ChartFilterBase, Omit<ChartFilterBase, 'type'>{
	type: ChartFilterType.Slider;
	range?: boolean;
	// options: {
	// 	floor: number;
	// 	ceil: number;
	// }
}

export interface ChartFilterBase{
	type: ChartFilterType;
	name: string;
	fieldCode: string;
	values: ChartFilterValue[];
	indicatorFilterType: IndicatorFilterType;
}

export enum IndicatorFilterType{
	KeywordFilter = 'keyword',
	DateFilter = 'date',
	IntegerFilter = 'integer',
	DoubleFilter = 'double',
	DateRangeFilter = 'date_range',
	IntegerRangeFilter = 'integer_range',
	DoubleRangeFilter = 'double_range',
	LikeFilter = 'like'
}


export enum ChartFilterType{
	Select = 'select',
	Slider = 'slider'
}


export interface ChartFilterValue{
	name: string;
	value: any;
}
export interface LegendConfig{ 

	//TODO IMPLEMENT

}


export interface FieldFormatterConfig{
	type: FieldFormatterType;
}

export enum FieldFormatterType{
	Date = 'date'
}


export interface DateFieldFormatterConfig extends FieldFormatterConfig, Omit<FieldFormatterConfig, 'type'>{
	type: FieldFormatterType.Date;
	params: string;
}

export enum IndicatorDashboardChartType {
	Line = 'line',
	Bar = 'bar',
	Scatter = 'scatter',
	Pie = 'pie',
	PolarBar = 'polar_bar',
	Map = 'map',
	Graph = 'graph',
	TreeMap="treemap",
	Sankey='sankey'
}

interface DataZoom{
	inside?: boolean;
	slider?: boolean;
	areaZoom?:{
		start :number; //percentage
		end: number; //percentage
	}
}

export interface BaseIndicatorDashboardChartAxisConfig{
	name: string;
	boundaryGap?:boolean;
}
export interface IndicatorDashboardChartYAxisConfig extends BaseIndicatorDashboardChartAxisConfig {

}
export interface IndicatorDashboardChartXAxisConfig extends BaseIndicatorDashboardChartAxisConfig {
	axisLabel?:{
		rotate?: number;
		width?: number;
	}
}


export interface IndicatorConfigMetric extends Metric{
}


export interface IndicatorConfigBucket extends Bucket, Omit<Bucket, 'metrics'>{
	metrics: IndicatorConfigMetric[];
}


export interface IndicatorConfigNestedBucket extends NestedBucket {
	metrics: IndicatorConfigMetric[];

	bucket: IndicatorConfigBucket;

}
export interface IndicatorConfigCompositeBucket extends CompositeBucket {
	metrics: IndicatorConfigMetric[];
	
}
export interface IndicatorConfigDataHistogramBucket extends DataHistogramBucket {
	metrics: IndicatorConfigMetric[];
}
export interface IndicatorConfigTermsBucket extends TermsBucket  {
	metrics: IndicatorConfigMetric[];
}
