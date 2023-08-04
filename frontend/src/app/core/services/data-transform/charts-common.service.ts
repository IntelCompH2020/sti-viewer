import { Injectable, NgZone } from "@angular/core";
import { Metric } from "@app/core/model/metic/metric.model";
import { BaseIndicatorDashboardChartConfig, FieldFormatterConfig, IndicatorDashboardBarChartConfig, IndicatorDashboardGraphChartConfig, IndicatorDashboardLineChartConfig, IndicatorDashboardMapChartConfig, IndicatorDashboardPieChartConfig, IndicatorDashboardPolarBarChartConfig, IndicatorDashboardRadarChartConfig, IndicatorDashboardSankeyChartConfig, IndicatorDashboardScatterChartConfig, IndicatorDashboardTreeMapChartConfig, LegendConfig } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { DataZoomComponentOption, EChartsOption, LegendComponentOption, SeriesOption, TitleComponentOption, ToolboxComponentOption, TreemapSeriesOption, XAXisComponentOption, YAXisComponentOption } from "echarts";
import { ChartSerie } from "./data-transform.service";
import { AuthService } from "../ui/auth.service";
import { AppPermission } from "@app/core/enum/permission.enum";
// import { sankeyData } from "./sankey-data";


const scatterData = new Array(7).fill(0).map((_, i) => ([
	Math.floor(Math.random() * 1000),
	Math.floor(Math.random() * 1000),
	Math.floor(Math.tan(Math.random() + 10) * 10000 * 10000),
	// `${i}`,
	// 'a'
]))


interface PieDataRecord {
	value: number;
	name: string;
	color?: string;
}


interface GeoMapData {
	description: string;
	data: {
		name: string,
		value: number
	}[]
}


@Injectable()
export class ChartBuilderService {

	constructor(
		private _zone: NgZone,
		private authService: AuthService
	) {

	}


	// ** BUILDERS


	// * MAP
	public buildGeoMap(args: {
		config: IndicatorDashboardMapChartConfig,
		data: GeoMapData[], onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {

		const { config, data, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		let serie: GeoMapData;

		if (!data.length) {
			console.warn('Building Geomap no data found ');
		} else {
			serie = data[0];
		}

		if (data.length > 1) {
			console.warn('Too many series for map , selecting first serie')
		}

		//* get MinMax values

		const allValues = serie.data.map(record => record.value);
		const minValue = allValues && allValues.length > 0 ? Math.min(...allValues) : 0;
		const maxValue = allValues && allValues.length > 0 ? Math.max(...allValues) : 0;


		return {
			title: this._buildTitle(config),

			tooltip: {
				trigger: 'item',
				showDelay: 0,
				transitionDuration: 0.2,

			},

			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),

			visualMap: {
				left: 'right',
				min: Math.floor(minValue * 0.8) - 1,
				max: Math.floor(maxValue * 1.2) + 1,
				inRange: {
					color: [
						config.mapChartConfig?.low?.color ?? '#dfeced',
						config.mapChartConfig?.high?.color ?? '#61a0a8'
					]
				},
				text: [
					config.mapChartConfig?.high?.text ?? 'High',
					config.mapChartConfig?.low?.text ?? 'Low',
				],
				calculable: true
			},
			series: [{
				name: serie.description,
				type: 'map',
				roam: true,
				map: 'worldmap',
				emphasis: {
					label: {
						show: true
					}
				},
				data: serie.data
			}],
		}
	}



	public buildTreeMap(args: {
		config: IndicatorDashboardTreeMapChartConfig,
		data: TreeMapData[],
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {

		const { data, config, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		function getLevelOption(isNested?: boolean) {
			return [
				{
					itemStyle: {
						borderWidth: 0,
						gapWidth: 5
					},
					upperLabel:{
						show: false
					}
				},

				isNested ?
				{
					itemStyle: {
					  borderColor: '#555',
					  borderWidth: 5,
					  gapWidth: 1
					},
					emphasis: {
					  itemStyle: {
						borderColor: '#ddd'
					  }
					}
				} : {
					itemStyle: {
						gapWidth: 1
					}
				},
				//   {
				// 	colorSaturation: [0.35, 0.5],
				// 	itemStyle: {
				// 	  gapWidth: 1,
				// 	  borderColorSaturation: 0.6
				// 	}
				//   }
			];
		};

		function getValueString(value: any, name?: string, metricName?: string): string {

			let valueString = '';
			if (name) {
				valueString += name + ': ';
			}

			valueString += value; // TODO MAYBE FORMATTING HERE

			if (config.toolTip.metricName) {
				valueString += ' ' + metricName;
			}

			return valueString;
		}

		const isNested = data?.some(x => x.children?.length);

		return {
			title: this._buildTitle(config),
			toolbox: this._buildToolbox({ config, onDownload, onFiltersOpen, onDownloadJSON, onShare, }),
			tooltip: config.toolTip ? {
				formatter: function (info) {
					var value = info.value;
					var treePathInfo = info.treePathInfo;
					var treePath = [];
					for (let i = 1; i < treePathInfo.length; i++) {
						treePath.push(treePathInfo[i].name);
					}
					return [
						//*  title
						`
						<div class="tooltip-title">
							${treePath.join('&#47;')}
						</div>
					`,

						getValueString(value, config.toolTip.name, config.toolTip.metricName)

					].join('');
				}
			} : null,
			series: [
				{
					id: 'treemap',
					name: config.treeName ?? '',
					type: 'treemap',
					roam: 'move',
					visibleMin: 300,
					label: {
						show: true,
						formatter: '{b}',
					},
					itemStyle: {
						borderColor: '#fff',
					},

					...(isNested ? {
						upperLabel:{
							show:true,
							height: 30
						}
					}: {})



					,
					levels: getLevelOption(isNested),
					data: this._buildTreeMapData(data, config)
				}
			]
		}
	}

	private _buildTreeMapData(data: TreeMapData[], config: IndicatorDashboardTreeMapChartConfig): TreeMapData[] {

		return data.map(node => {

			const color = config.treeColors?.[node.name];
			const itemStyle = color ? {
				itemStyle: { color }
			} : {};

			return {
				...node,
				children: node?.children?.length ?  this._buildTreeMapData( node.children,config) : null,
				...itemStyle
			}
		});
	}

	// itemStyle:{color: 'blue'}}

	// * POLAR BAR
	public buildPolarBar(args: {
		config: IndicatorDashboardPolarBarChartConfig,
		labels: string[],
		inputSeries: ChartSerie,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, labels, inputSeries, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		const series = Object.keys(inputSeries).map((key, _index) => {
			// return this._buildPolarBarSerie(config as any, inputSeries[key].name ,inputSeries[key].data, (index) => index * 10 + (index * 10) )
			return this._buildPolarBarSerie({
				animationDelay: (index) => index * 10 + (index * 10),
				config: config,
				data: inputSeries[key].data,
				name: inputSeries[key].name,
				color: inputSeries[key].color
			})
		});
		const dataZoom = this._buildDataZoom(config);

		return {
			title: this._buildTitle(config),
			angleAxis: {
				type: 'category',
				data: labels
			},
			tooltip: {},
			toolbox: this._buildToolbox({ config, onFiltersOpen, onDownload, onDownloadJSON, onShare }),
			radiusAxis: { name: config?.radiusAxis?.name },
			polar: {},
			series,
			legend: this._buildLegend(config.legend),
			dataZoom,
		}
	}


	// * SCATTER OPTIONS
	public buildScatterOptions(args: {
		config: IndicatorDashboardScatterChartConfig,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		labels: string[],
		onShare?: () => void
		// xAxis: number[],
		// yAxis:number[],
		// bubbleSize: number[], // todo any

		series: ChartSerie
	}): EChartsOption {
		const { config, onDownload, onFiltersOpen, labels, series, onDownloadJSON, onShare } = args;


		const bubbleSizeKeyExtractor = config?.bubble?.seriesKeyExtractor;
		const xAxisKeyExtractor = config?.xAxis?.seriesKeyExtractor;
		const yAxisKeyExtractor = config?.yAxis?.seriesKeyExtractor;

		if (
			[
				bubbleSizeKeyExtractor,
				yAxisKeyExtractor,
				xAxisKeyExtractor
			].some(x => !x)
		) {
			console.warn("ScatterConfigurationError: No exctractor found for either xAxis, yAxis or Bubble")
			throw "Invalid configuration"
		}


		const yAxis = series[yAxisKeyExtractor]?.data;
		const xAxis = series[xAxisKeyExtractor]?.data;
		const bubbleSize = series?.[bubbleSizeKeyExtractor]?.data;
		const maxBubbleValue = bubbleSize && bubbleSize.length > 0 ? Math.max(...bubbleSize) : 0
		const colorMap = config.colorMap ?? {};


		return {

			title: this._buildTitle(config),
			legend: this._buildLegend(config.legend),
			xAxis: {
				name: config?.xAxis?.name,
				// type: 'value',
				// data: new Array(7).fill(0).map((_, index) => Math.floor(Math.random()*1000))
			},
			yAxis: {
				name: config?.yAxis?.name,
				nameLocation: 'middle',
				nameGap: 50
				// type:'value'
			},
			tooltip: {
				backgroundColor: 'rgba(255,255,255,0.7)',
				formatter: function (param) {
					var value = param.value;
					return `
				  	<div style="border-bottom: 1px solid rgba(255,255,255); font-size: 18px;padding-bottom: 7px;margin-bottom: 7px">
						${param.seriesName} ${(value[2] as number)?.toLocaleString?.() ?? value[2]}
					</div>
					<div>
						${config?.yAxis?.name ?? ''}: ${(value[1] as number)?.toLocaleString?.() ?? value[1]}
						<br>
						${config?.xAxis?.name ?? ''}: ${(value[0] as number)?.toLocaleString?.() ?? value[0]}
					</div>
				  `
				}
			},
			toolbox: this._buildToolbox({
				onDownload,
				config,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			series: labels.map(
				(label, labelIndex) => ({
					name: label,
					data: [
						[
							xAxis[labelIndex],//x
							yAxis[labelIndex],//y
							bubbleSize?.[labelIndex] ?? 0//bublesize
						]
					],

					type: 'scatter',
					symbolSize: function (data) {
						return ((data[2] / maxBubbleValue) * 100) + 10
					},
					emphasis: {
						focus: 'series',
					},
					itemStyle: {
						shadowBlur: 10,
						shadowColor: 'rgba(120, 36, 50, 0.5)',
						shadowOffsetY: 5,
						color: colorMap[label]
					}
				})
			)
		}
	}
	public buildRadarOptions(args: {
		config: IndicatorDashboardRadarChartConfig,
		// radarIndicatorOptions: RadarIndicatorGraphOption[],
		labels: string[],
		inputSeries: ChartSerie,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, onDownload, onFiltersOpen, inputSeries, labels, onDownloadJSON, onShare } = args;


		const dimensions = Object.keys(inputSeries).filter(x => !!x);

		let maxValue = dimensions.reduce(
			(previousMaxValue, currentDimension) => {

				const currentMax = Math.max(...inputSeries[currentDimension].data);
				return Math.max(previousMaxValue, currentMax)
			}
			, 0
		)
		const radarIndicatorOptions = dimensions.map(dimension => ({
			name: dimension,
			max: maxValue
		}))
		// const radarIndicatorOptions = dimensions.map(dimension =>({
		// 	name: dimension,
		// 	max: Math.max(...inputSeries[dimension].data)
		// }))


		const radarSeries = labels.map((label, labelIndex) => ({
			type: "radar",
			// symbol:"none",
			emphasis: {
				areaStyle: {
					color: config?.radarConfig?.emphasisColor //"rgba(0,250,0,0.3)"
				}
			},
			// lineStyle:{
			// 	width: 1
			// },
			data: [
				{
					name: label,
					value: dimensions.map(dimension => inputSeries[dimension].data[labelIndex])
				}
			]
		}))



		const highColor = config?.radarConfig?.high?.color ?? '#61a0a8';
		const lowColor = config?.radarConfig?.low?.color ?? '#dfeced';
		const min = config?.radarConfig?.low?.value ?? 0;
		const max = config?.radarConfig?.high?.value ?? 200;
		return {
			title: this._buildTitle(config),
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			tooltip: {
				//   trigger: 'item'
			},
			legend: {
				type: 'scroll',
				bottom: 10,
			},
			visualMap: {
				max,
				min,
				type: 'continuous',
				top: 'middle',
				right: 10,
				color: [
					highColor,
					lowColor
				],
				calculable: true,
				text: [
					config?.radarConfig?.high?.text ?? '',
					config?.radarConfig?.low?.text ?? '',
				]
			},
			radar: {
				indicator: radarIndicatorOptions
			},
			series: radarSeries as any,
		}
	}


	public buildGraphOptions(args: {
		config: IndicatorDashboardGraphChartConfig,
		graph: GraphOptions,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, graph, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		return {
			tooltip: {},
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			legend: [ // TODO SPECIFIC GRAPH BUILDER
				{
					orient: 'vertical',
					right: 10,
					top: 'center',
					data: graph.categories.map(function (a) {
						return a.name;
					})
				}
			],
			series: [
				{
					name: config.chartName,
					type: 'graph',
					layout: 'none',
					data: graph.nodes.map(node => ({
						id: node.id,
						name: node.name,
						value: node.value,
						category: node.category,
						symbolSize: node.symbolSize,
						x: node.x,
						y: node.y
					})),
					links: graph.links,
					categories: graph.categories,
					roam: true,
					label: {
						show: true,
						position: 'right',
						formatter: '{b}'
					},
					labelLayout: {
						hideOverlap: true
					},
					scaleLimit: {
						min: 0.4,
						max: 2
					},
					lineStyle: {
						color: 'source',
						curveness: 0.3
					}
				}
			]
		}
	}

	// * PIE
	public buildPie(args: {
		config: IndicatorDashboardPieChartConfig,
		pieData: PieDataRecord[],
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, pieData, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		return {
			title: this._buildTitle(config),
			legend: this._buildLegend(config.legend),
			tooltip: {
				trigger: 'item',
				// formatter: (info) => {
				// 	const value = info.value;
				// 	const stringVal = value.toString();
				// 	const [integer, decimal] = stringVal.split('.');

				// 	return `<span>${integer}</span><small>${decimal ? '.' + decimal: ''}</small>`

				// },
			},
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			series: [
				this._buildPie(config as IndicatorDashboardPieChartConfig, pieData)
			]
		}
	}


	public buildSankeyOptions(args: {
		data: SankeyData,
		config: IndicatorDashboardSankeyChartConfig,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {

		const { config, onDownload, onFiltersOpen, data, onDownloadJSON, onShare } = args;

		return {
			title: this._buildTitle(config),
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			tooltip: {},
			series: {
				type: 'sankey',
				layout: 'none',
				emphasis: {
					focus: 'adjacency'
				},
				...data
			}
		}
	}


	// *LINECHART
	public buildLine(params: {
		config: IndicatorDashboardLineChartConfig,
		labels: string[],
		inputSeries: ChartSerie,
		onFiltersOpen?: () => void,
		onDownload?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, labels, inputSeries, onFiltersOpen, onDownload, onDownloadJSON, onShare } = params;
		const dataZoom = this._buildDataZoom(config);


		const series = Object.keys(inputSeries).map((key, _index) => {
			return this._buildSerie({
				animationDelay: index => index * 10 + (index * 10),
				config: config,
				data: inputSeries[key].data,
				name: inputSeries[key].name,
				color: inputSeries[key].color,
				type: inputSeries[key].type
			});
		});


		return {
			title: this._buildTitle(config),
			legend: this._buildLegend(config.legend),
			tooltip: {
				trigger: 'axis',
				confine: true
			},
			xAxis: this._buildXAxis(config, labels),
			yAxis: this._buildYAxis(config, labels),
			dataZoom,
			series,
			toolbox: this._buildToolbox({ config, onDownload, onFiltersOpen, onDownloadJSON, onShare }),
			animationEasing: 'elasticOut',
			animationDelayUpdate: (idx) => idx * 5,
		};
	}



	// *BARCHART
	public buildBar(args: {
		config: IndicatorDashboardBarChartConfig,
		labels: string[],
		inputSeries: ChartSerie,
		onDownload?: () => void,
		onFiltersOpen?: () => void,
		onDownloadJSON?: () => void,
		onShare?: () => void
	}): EChartsOption {
		const { config, inputSeries, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;
		let { labels } = args;

		if (config?.horizontal) {
			labels = labels.map(x => x).reverse();
		}

		const series = Object.keys(inputSeries).map((key, _index) => {

			let data = inputSeries[key].data;
			if (config?.horizontal) {
				data = data.map(x => x).reverse();
			}
			return this._buildSerie({
				animationDelay: index => index * 10 + (index * 10),
				config: config,
				data: data,
				name: inputSeries[key].name,
				color: inputSeries[key].color,
				type: inputSeries[key].type
			})
		});
		const dataZoom = this._buildDataZoom(config);
		return {
			title: this._buildTitle(config),
			legend: this._buildLegend(config.legend),
			tooltip: { trigger: 'axis' },
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen,
				onDownloadJSON,
				onShare
			}),
			xAxis: this._buildXAxis(config, labels),
			yAxis: this._buildYAxis(config, labels),
			dataZoom,
			series,
			animationEasing: 'elasticOut',
			grid: {
				containLabel: true
			},
			animationDelayUpdate: (idx) => idx * 5,
		};
	}



	//* PRIVATES

	private _buildTitle(config: BaseIndicatorDashboardChartConfig): TitleComponentOption {
		return {

			text: config.chartName,
			subtext: config.chartSubtitle,

		}
	}


	private _buildDataZoom(config: IndicatorDashboardBarChartConfig | IndicatorDashboardLineChartConfig | IndicatorDashboardPolarBarChartConfig): DataZoomComponentOption[] {
		let dataZoom = [];
		if (config.dataZoom) {
			if (config.dataZoom.areaZoom) {


				const start = config.dataZoom.areaZoom.start ?? 0;
				const end = config.dataZoom.areaZoom.end ?? 100;

				const dataZ = {
					id: 'areaZoom',
					show: true,
					start,
					end,
				} as any;

				if ((config as any).horizontal) {
					dataZ.yAxisIndex = [0];
					dataZ.start = 100 - dataZ.start;
					dataZ.end = 100 - dataZ.end;
				} else {
					// dataZ.xAxisIndex = [0];
				}

				dataZoom.push(dataZ);
			}
			if (config.dataZoom.inside) {
				const zoomItem = {
					id: 'insideZoom',
					type: 'inside'
				} as any;
				if ((config as any).horizontal) {
					zoomItem.yAxisIndex = 0;
				}
				dataZoom.push(zoomItem);
			}
			if (config.dataZoom.slider) {

				const zoomItem = { type: 'slider' } as any;
				if ((config as any).horizontal) {
					zoomItem.yAxisIndex = 0;
				}
				dataZoom.push(zoomItem);
			}
		}
		return dataZoom;
	}


	public buildLineChartZoomLock(
		params: {
			dataZoom: DataZoomComponentOption[] | DataZoomComponentOption,
			lock: boolean
		}
	): DataZoomComponentOption[] {
		const { dataZoom, lock = true } = params;

		if (!dataZoom) {
			return null;
		}

		const dz: DataZoomComponentOption[] = Array.isArray(dataZoom) ? dataZoom : [dataZoom];

		const insideZoom = dz?.find(dzItem => dzItem.type === 'inside');

		if (!insideZoom) {
			return null;
		}

		return [{
			...insideZoom,
			zoomLock: lock
		}]

	}


	public buildGeoMapZoomLock(
		params: {
			series: SeriesOption | SeriesOption[],
			lock: boolean
		}
	): SeriesOption[] {
		const { series, lock } = params;

		if (!series) {
			return null;
		}

		const seriesArray = Array.isArray(series) ? series : [series];

		if (!seriesArray?.length) {
			return;
		}

		return seriesArray.map(
			serie => ({
				...serie,
				roam: !lock
			})
		)
	}





	private _buildPie(config: IndicatorDashboardPieChartConfig, data?: PieDataRecord[]): SeriesOption {

		const colors = data?.map(d => d.color);
		const color = colors.filter(x => x).length ? colors : [];


		const chart: SeriesOption = {
			type: 'pie',
			data: data ?? [],
			radius: config?.radius ? config?.radius : (config?.doughnut ? [50, 250] : "85%"),
			itemStyle: config?.doughnut ? {
				borderRadius: 8
			} : null,
			color
		}

		if (config.roseType) {
			chart.roseType = config.roseType as any;
		}


		return chart;
	}

	private _buildSerie(params: { type: any, config: IndicatorDashboardLineChartConfig, name: string, data: number[], animationDelay: (index: number) => number, color?: string }): SeriesOption {
		const { config, name, data, animationDelay, color, type } = params;
		const serie: SeriesOption = {
			name,
			type: type ?? config.type as any,
			data,
			color,
			animationDelay,
		};

		if (config.stack) {
			(serie as any).stack = 'x';// todo configuration has it stack chart requires string
		}

		if (config.areaStyle) {
			(serie as any).areaStyle = config.areaStyle;
		}

		return serie;
	}

	private _buildPolarBarSerie(params: { config: IndicatorDashboardPolarBarChartConfig, name: string, data: number[], animationDelay: (index: number) => number, color?: string }): SeriesOption {
		const { name, data, animationDelay, color } = params;
		const serie: SeriesOption = {
			name,
			type: 'bar',
			coordinateSystem: 'polar',
			data,
			color,
			animationDelay,
			stack: 'x',
		};

		return serie;
	}

	private _buildXAxis(config: IndicatorDashboardLineChartConfig, data: string[] = []): XAXisComponentOption {
		let xAxis: XAXisComponentOption;
		if (config.horizontal) {
			xAxis = {
				name: config?.yAxis?.name,
				nameLocation: 'middle',
				nameGap: 50
			};
		} else {

			const axisLabel = config?.xAxis?.axisLabel ? {
				width: config.xAxis.axisLabel.width,
				rotate: config.xAxis.axisLabel.rotate ?? 0,
				overflow: 'truncate'
			} : {};

			xAxis = {
				name: config?.xAxis?.name,
				data,
				type: 'category',
				boundaryGap: config?.xAxis?.boundaryGap ?? true,
				silent: false,
				splitLine: {
					show: false,
				},
				axisLabel
			}
		}

		return xAxis;
	}

	private _buildYAxis(config: IndicatorDashboardLineChartConfig, data: string[]): YAXisComponentOption {
		let yAxis: YAXisComponentOption;

		if (!config.horizontal) {
			yAxis = {
				name: config?.yAxis?.name,
				nameLocation: 'middle',
				nameGap: 50
			}
		} else {
			yAxis = {
				name: config?.xAxis?.name,
				boundaryGap: config?.xAxis?.boundaryGap ?? true,
				data,
				silent: false,
				splitLine: {
					show: false,
				},
				axisLabel: {
					width: 100,
					overflow: 'truncate',
				} as any
			}
		}
		return yAxis;
	}


	private _buildLegend(legendConfig: LegendConfig): null | LegendComponentOption {
		if (legendConfig) return {
			orient: 'vertical',
			right: 10,
			top: 30,
			backgroundColor: '#fff',
			textStyle: {
				width: 120,
				overflow: 'break'
			},
			type: "scroll"
		};

		return null
	}

	// TODO SAVE AS IMAGE AND MYDOWNLOAD VIA CONFIGURATION
	private _buildToolbox(args: {
		config: BaseIndicatorDashboardChartConfig,
		onFiltersOpen?: () => void,
		onDownload?: () => void,
		onDownloadJSON?: () => void
		onShare?: () => void
	}): ToolboxComponentOption {

		const { config, onDownload, onFiltersOpen, onDownloadJSON, onShare } = args;

		const feature: any = {};
		if (config.filters?.length) {
			feature.myOnFilterChange = {
				show: true,
				title: 'Open filters',
				icon: 'path://M3.853 54.87C10.47 40.9 24.54 32 40 32H472C487.5 32 501.5 40.9 508.1 54.87C514.8 68.84 512.7 85.37 502.1 97.33L320 320.9V448C320 460.1 313.2 471.2 302.3 476.6C291.5 482 278.5 480.9 268.8 473.6L204.8 425.6C196.7 419.6 192 410.1 192 400V320.9L9.042 97.33C-.745 85.37-2.765 68.84 3.854 54.87L3.853 54.87z',
				onclick: () => {
					this._zone.run(() => {
						onFiltersOpen?.();
					})
				}
			}
		}

		if (config.chartId && config.chartShare && this.authService.hasPermission(AppPermission.CreateChartExternalToken)) {
			feature.myOnShare = {
				show: true,
				title: 'Share',
				icon: 'path://M 23 3 A 4 4 0 0 0 19 7 A 4 4 0 0 0 19.09375 7.8359375 L 10.011719 12.376953 A 4 4 0 0 0 7 11 A 4 4 0 0 0 3 15 A 4 4 0 0 0 7 19 A 4 4 0 0 0 10.013672 17.625 L 19.089844 22.164062 A 4 4 0 0 0 19 23 A 4 4 0 0 0 23 27 A 4 4 0 0 0 27 23 A 4 4 0 0 0 23 19 A 4 4 0 0 0 19.986328 20.375 L 10.910156 15.835938 A 4 4 0 0 0 11 15 A 4 4 0 0 0 10.90625 14.166016 L 19.988281 9.625 A 4 4 0 0 0 23 11 A 4 4 0 0 0 27 7 A 4 4 0 0 0 23 3 z',
				onclick: () => {
					this._zone.run(() => {
						onShare?.();
					})
				}
			}
		}



		if (config.chartDownloadImage) {
			feature.saveAsImage = {
				title: 'Download image',
				type: 'png',
			}
		}

		if (config.chartDownloadData) {
			feature.myOnDownload = {
				show: true,
				title: 'Download Data',
				icon: 'path://M224 0V128C224 145.7 238.3 160 256 160H384V448C384 483.3 355.3 512 320 512H64C28.65 512 0 483.3 0 448V64C0 28.65 28.65 0 64 0H224zM80 224C57.91 224 40 241.9 40 264V344C40 366.1 57.91 384 80 384H96C118.1 384 136 366.1 136 344V336C136 327.2 128.8 320 120 320C111.2 320 104 327.2 104 336V344C104 348.4 100.4 352 96 352H80C75.58 352 72 348.4 72 344V264C72 259.6 75.58 256 80 256H96C100.4 256 104 259.6 104 264V272C104 280.8 111.2 288 120 288C128.8 288 136 280.8 136 272V264C136 241.9 118.1 224 96 224H80zM175.4 310.6L200.8 325.1C205.2 327.7 208 332.5 208 337.6C208 345.6 201.6 352 193.6 352H168C159.2 352 152 359.2 152 368C152 376.8 159.2 384 168 384H193.6C219.2 384 240 363.2 240 337.6C240 320.1 231.1 305.6 216.6 297.4L191.2 282.9C186.8 280.3 184 275.5 184 270.4C184 262.4 190.4 256 198.4 256H216C224.8 256 232 248.8 232 240C232 231.2 224.8 224 216 224H198.4C172.8 224 152 244.8 152 270.4C152 287 160.9 302.4 175.4 310.6zM280 240C280 231.2 272.8 224 264 224C255.2 224 248 231.2 248 240V271.6C248 306.3 258.3 340.3 277.6 369.2L282.7 376.9C285.7 381.3 290.6 384 296 384C301.4 384 306.3 381.3 309.3 376.9L314.4 369.2C333.7 340.3 344 306.3 344 271.6V240C344 231.2 336.8 224 328 224C319.2 224 312 231.2 312 240V271.6C312 294.6 306.5 317.2 296 337.5C285.5 317.2 280 294.6 280 271.6V240zM256 0L384 128H256V0z',
				onclick: () => {
					this._zone.run(() => {
						onDownload?.();
					})
				}
			}
		}


		if (config.chartDownloadJson) {
			feature.myOnDownloadJson = {
				show: true,
				title: 'Download JSON Data',
				icon: 'path://M221.37 618.44h757.94V405.15H755.14c-23.5 0-56.32-12.74-71.82-28.24-15.5-15.5-25-43.47-25-66.97V82.89H88.39c-1.99 0-3.49 1-4.49 2-1.5 1-2 2.5-2 4.5v1155.04c0 1.5 1 3.5 2 4.5 1 1.49 3 1.99 4.49 1.99H972.8c2 0 1.89-.99 2.89-1.99 1.5-1 3.61-3 3.61-4.5v-121.09H221.36c-44.96 0-82-36.9-82-81.99V700.44c0-45.1 36.9-82 82-82zm126.51 117.47h75.24v146.61c0 30.79-2.44 54.23-7.33 70.31-4.92 16.03-14.8 29.67-29.65 40.85-14.86 11.12-33.91 16.72-57.05 16.72-24.53 0-43.51-3.71-56.94-11.06-13.5-7.36-23.89-18.1-31.23-32.3-7.35-14.14-11.69-31.67-12.99-52.53l71.5-10.81c.11 11.81 1.07 20.61 2.81 26.33 1.76 5.78 4.75 10.37 9 13.95 2.87 2.33 6.94 3.46 12.25 3.46 8.4 0 14.58-3.46 18.53-10.37 3.9-6.92 5.87-18.6 5.87-35V735.92zm112.77 180.67l71.17-4.97c1.54 12.81 4.69 22.62 9.44 29.28 7.74 10.88 18.74 16.34 33.09 16.34 10.68 0 18.93-2.76 24.68-8.36 5.81-5.58 8.7-12.07 8.7-19.41 0-6.97-2.71-13.26-8.2-18.79-5.47-5.53-18.23-10.68-38.28-15.65-32.89-8.17-56.27-19.1-70.26-32.74-14.12-13.57-21.18-30.92-21.18-52.03 0-13.83 3.61-26.89 10.85-39.21 7.22-12.38 18.07-22.06 32.59-29.09 14.52-7.04 34.4-10.56 59.65-10.56 31 0 54.62 6.41 70.88 19.29 16.28 12.81 25.92 33.24 29.04 61.27l-70.5 4.65c-1.87-12.25-5.81-21.17-11.81-26.7-6.05-5.6-14.35-8.36-24.9-8.36-8.71 0-15.31 2.07-19.73 6.16-4.4 4.09-6.59 9.12-6.59 15.02 0 4.27 1.81 8.11 5.37 11.57 3.45 3.59 11.8 6.85 25.02 9.93 32.75 7.86 56.2 15.84 70.31 23.87 14.18 8.05 24.52 17.98 30.96 29.92 6.44 11.88 9.66 25.2 9.66 39.96 0 17.29-4.3 33.24-12.88 47.89-8.63 14.58-20.61 25.7-36.08 33.24-15.41 7.54-34.85 11.31-58.33 11.31-41.24 0-69.81-8.86-85.68-26.52-15.88-17.65-24.85-40.09-26.96-67.3zm248.74-45.5c0-44.05 11.02-78.36 33.09-102.87 22.09-24.57 52.82-36.82 92.24-36.82 40.38 0 71.5 12.07 93.34 36.13 21.86 24.13 32.77 57.94 32.77 101.37 0 31.54-4.75 57.36-14.3 77.54-9.54 20.18-23.37 35.89-41.4 47.13-18.07 11.24-40.55 16.84-67.48 16.84-27.33 0-49.99-4.83-67.94-14.52-17.92-9.74-32.49-25.07-43.62-46.06-11.13-20.92-16.72-47.19-16.72-78.74zm74.89.19c0 27.21 4.57 46.81 13.68 58.68 9.13 11.88 21.57 17.85 37.26 17.85 16.1 0 28.65-5.84 37.45-17.47 8.87-11.68 13.28-32.54 13.28-62.77 0-25.39-4.63-43.92-13.84-55.61-9.26-11.76-21.75-17.6-37.56-17.6-15.13 0-27.34 5.97-36.49 17.85-9.21 11.88-13.78 31.61-13.78 59.07zm209.08-135.36h69.99l90.98 149.05V735.91h70.83v269.96h-70.83l-90.48-148.24v148.24h-70.49V735.91zm67.71-117.47h178.37c45.1 0 82 37.04 82 82v340.91c0 44.96-37.03 81.99-82 81.99h-178.37v147c0 17.5-6.99 32.99-18.5 44.5-11.5 11.49-27 18.5-44.5 18.5H62.97c-17.5 0-32.99-7-44.5-18.5-11.49-11.5-18.5-27-18.5-44.5V63.49c0-17.5 7-33 18.5-44.5S45.97.49 62.97.49H700.1c1.5-.5 3-.5 4.5-.5 7 0 14 3 19 7.49h1c1 .5 1.5 1 2.5 2l325.46 329.47c5.5 5.5 9.5 13 9.5 21.5 0 2.5-.5 4.5-1 7v250.98zM732.61 303.47V96.99l232.48 235.47H761.6c-7.99 0-14.99-3.5-20.5-8.49-4.99-5-8.49-12.5-8.49-20.5z',
				onclick: () => {
					this._zone.run(() => {
						onDownloadJSON?.();
					})
				}
			}
		}
		return {
			feature
		};
	}
}



interface SeriesConfiguration {
	fieldCode: string;
	metrics: ExtendedMetric[];
	fieldFormatter?: FieldFormatterConfig;
}

interface StandAloneConfiguration {
	metrics: ExtendedMetric[];
	fieldCode: string;
}

export interface ConfigurationMetrics {
	seriesConfiguration?: SeriesConfiguration;
	standAloneConfiguration?: StandAloneConfiguration;
}

export interface ExtendedMetric extends Metric {
	_id: string;
	description: string;
	color?: string;
}


export interface GraphOptions {
	nodes: GraphNode[];
	links: GraphLink[];
	categories: GraphCategory[];
}


export interface GraphNode {
	id: string,
	name: string,
	symbolSize: number;
	x: number;
	y: number;
	value: number;
	category: number
}

export interface GraphLink {
	source: string;
	target: string;
}

export interface GraphCategory {
	name: string;
}

export interface TreeMapData {
	value: number;
	name: string;
	path?: string;
	children?: TreeMapData[];
}

interface SankeyData {
	data: { name: string }[];
	links: {
		source: string,
		target: string,
		value: number
	}[]
};

interface RadarIndicatorGraphOption {
	name?: string;
	max?: number;
}
