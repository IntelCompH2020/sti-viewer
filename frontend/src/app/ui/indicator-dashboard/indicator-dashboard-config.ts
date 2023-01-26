import { Bucket, CompositeBucket, DataHistogramBucket, NestedBucket, TermsBucket } from "@app/core/model/bucket/bucket.model";
import { Metric } from "@app/core/model/metic/metric.model";
import { RawDataRequest } from "@app/core/query/indicator-point-report.lookup";

export interface IndicatorDashboardConfig {
	id: string;
	tabs: IndicatorDashboardTabConfig[];
}

export interface IndicatorDashboardTabConfig {
	name: string;
	chartGroups: TabBlockConfig[];
}


export type TabBlockConfig = IndicatorDashboardChartGroupConfig | GaugesBlock;


export type BaseTabBlock = {
	name: string;
	type?: TabBlockType;
}

export type IndicatorDashboardChartGroupConfig = BaseTabBlock &  {
	type?: TabBlockType.ChartGroup;
	charts: IndicatorDashboardChart [];
}

export enum TabBlockType {
	ChartGroup = 'chartgroup',
	Gauge = 'gauge'
}


export type GaugesBlock = BaseTabBlock & {
	type?: TabBlockType.Gauge;
	gauges: BaseIndicatorDashboardGaugeConfig [];
}

export type IndicatorDashboardChart = IndicatorDashboardLineChartConfig | IndicatorDashboardBarChartConfig | IndicatorDashboardScatterChartConfig | IndicatorDashboardPieChartConfig | IndicatorDashboardPolarBarChartConfig | IndicatorDashboardMapChartConfig | IndicatorDashboardSankeyChartConfig;


export interface IndicatorDashboardSankeyChartConfig extends BaseIndicatorDashboardChartConfig {
	connectionExtractor: ConnectionExtractor;
}


export interface ConnectionLimit{
	type: ConnectionLimitType;
	order: ConnectionLimitOrder;
	count: number;
}

export enum ConnectionLimitType{
	Source = 'source',
	Target = 'target',
	Connection = 'connection'
}

export enum ConnectionLimitOrder{
	ASCENDING = 'asc',
	DESCENDING = 'desc'
}

export interface ConnectionExtractor{
	sourceKeyExtractor: string;//* search  source from group items
	targetKeyExtractor: string //* search target from group items
	valueKeyExtractor: string; // * extract value from values

	valueTests?: Record<string, string>;
	groupTests?: Record<string, string>;

	limit?: ConnectionLimit;
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
	// type: IndicatorDashboardChartType.Pie;
	roseType?:string;
}
export interface IndicatorDashboardPolarBarChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	// type: IndicatorDashboardChartType.PolarBar;
	dataZoom?: DataZoom;
	radiusAxis: PolarBarRadiusAxis;
}
export interface IndicatorDashboardMapChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	// type: IndicatorDashboardChartType.Map;
	mapChartConfig: MapConfig;

	// * <Backend Name (backend response), JSON map config name>
	countryNameMapping?: Record<string, string>;
}
export interface IndicatorDashboardTreeMapChartConfig extends BaseIndicatorDashboardChartConfig, Omit <BaseIndicatorDashboardChartConfig, 'type'>{
	// type: IndicatorDashboardChartType.TreeMap;
	treeName?: string;
	toolTip?: TreeMapToolTip;
	treeColors?: Record<string, string>;
}

export interface TreeMapToolTip{
	name?: string;
	metricName?: string;
}
export interface MapConfig{
	high?:MapConfigLegentItem;
	low?:MapConfigLegentItem;
}

export interface MapConfigLegentItem{
	color?: string;
	text?:string;
}

export interface PolarBarRadiusAxis{
	name: string;
}


export interface ChartDownloadImageConfig{

}
export interface ChartDownloadDataConfig{

}

export interface DashboardChartTagConfig{
	attachedTags?: string[];
}


export interface BaseTagConfiguration{
	tags?: DashboardChartTagConfig;
}

export interface BaseServerFetchConfiguration{
	indicatorId: string | 'inherited';
	metrics?: IndicatorConfigMetric[];
	bucket?: IndicatorConfigBucket;
	staticFilters?:IndicatorDashboardStaticFilters;
	rawDataRequest?:RawDataRequest;
}

export interface BaseSeriesConfiguration{
	//* describe how to extract series from results
	series?: DashBoardSerieConfiguration[];
}

export interface BaseTransformConfiguration{
	// * extract data from response
	labelSortKey:string; //* sort results based on a value found in group.items (picked by labelSorkKey) !!required
	labelsTransform?: FieldFormatterConfig; //*  provide transformation configuration for label axis (x axis) if needed
	reverseValues?: boolean;
}

export interface CommonDashboardItemConfiguration extends 
	BaseSeriesConfiguration,
	BaseServerFetchConfiguration,
	BaseTagConfiguration,
	BaseTransformConfiguration 
{

}

export interface BaseIndicatorDashboardChartConfig extends CommonDashboardItemConfiguration{
	type: IndicatorDashboardChartType;
	chartName?: string;
	chartSubtitle?: string;
	
	description: string;
	legend?: LegendConfig;
	filters?: ChartFilter[];
	chartDownloadImage?:ChartDownloadImageConfig;
	chartDownloadData?:ChartDownloadDataConfig;
}



export enum GaugeType{
	ValueCard = 'value_card'
}
export interface BaseIndicatorDashboardGaugeConfig extends CommonDashboardItemConfiguration{
	type: GaugeType,
	name: string,
	labelOverride?: string,
	description: string,
}

export interface IndicatorDashboardStaticFilters{
	keywordsFilters?:IndicatorKeywordFilter[];
}

export interface IndicatorKeywordFilter{
	field: string;
	value:  string[];
}


export enum DashBoardSerieConfigurationNestedType{
	Line = 'line',
	Bar = 'bar'
}
export interface DashBoardSerieConfigurationNested{
	type?: DashBoardSerieConfigurationNestedType
}

export interface DashBoardSerieConfiguration{
	nested?:DashBoardSerieConfigurationNested;
	splitSeries?: DashBoardSerieSplitSerie[];

	// * describe where to find x axis data (labels) 
	label:DashBoardSerieLabel;

	// * describe how to extract value from values
	values:DashBoardSerieValues
}

export interface DashBoardSerieSplitSerie{
	key: string
}
export interface DashBoardSerieLabel{
	color?:string; //* color for the serie produced (not taken into account if there are split series)
	name: string; // * name of serie produced (not taken into account if there is splitseries defined)
	labelKey: string; //* key with which we are extracting label from group.items
}

export interface DashBoardSerieValues{
	formatter?: SeriesValueFormatter;
	valueKey: string; //* which field we consider as a value from values[x] object
	tests?:Record<string, string>[] //* a list of tests that need to be validated (AND LOGIC only). the perfect match object which we exctract the value must have key-value pair that matches extact with the tests
	groupTests?:Record<string, string>[]// * Testing group values (provided in labels object)
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

export interface DataZoom{
	inside?: boolean;
	slider?: boolean;
	areaZoom?: AreaZoom;
}
export interface AreaZoom{
	start :number; //percentage
	end: number; //percentage
}

export interface BaseIndicatorDashboardChartAxisConfig{
	name: string;
	boundaryGap?:boolean;
}
export interface IndicatorDashboardChartYAxisConfig extends BaseIndicatorDashboardChartAxisConfig {

}
export interface IndicatorDashboardChartXAxisConfig extends BaseIndicatorDashboardChartAxisConfig {
	axisLabel?: AxisLabel;
}
export interface AxisLabel{
	rotate?: number;
	width?: number;
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
