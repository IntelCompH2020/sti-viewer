import { Injectable, NgZone } from "@angular/core";
import { Metric } from "@app/core/model/metic/metric.model";
import { BaseIndicatorDashboardChartConfig, FieldFormatterConfig, IndicatorDashboardBarChartConfig, IndicatorDashboardGraphChartConfig, IndicatorDashboardLineChartConfig, IndicatorDashboardMapChartConfig, IndicatorDashboardPieChartConfig, IndicatorDashboardPolarBarChartConfig, IndicatorDashboardScatterChartConfig, LegendConfig } from "@app/ui/indicator-dashboard/indicator-dashboard-config";
import { DataZoomComponentOption, EChartsOption, LegendComponentOption, SeriesOption, ToolboxComponentOption, XAXisComponentOption, YAXisComponentOption } from "echarts";
import { ChartSerie} from "./data-transform.service";


const scatterData = new Array(7).fill(0).map((_,i) => ([
	Math.floor(Math.random() * 1000),
	Math.floor(Math.random() * 1000),
	Math.floor(Math.tan(Math.random() +10) * 10000*10000),
	// `${i}`,
	// 'a'
]))


interface PieDataRecord {
	value: number;
	name: string;
	color?: string; 
}


interface GeoMapData{
	description: string;
	data: {
		name: string, 
		value: number
	}[]
}


@Injectable()
export class ChartBuilderService {

	constructor(private _zone: NgZone){

	}


    // ** BUILDERS


    // * MAP
    public buildGeoMap(args:{config: IndicatorDashboardMapChartConfig, data: GeoMapData[], onDownload?:() => void, onFiltersOpen?:() => void }): EChartsOption{

		const { config, data, onDownload, onFiltersOpen } = args;
		let serie;

		if(!data.length){
			console.warn('Building Geomap no data found ');
		}else{
			serie = data[0];
		}

		if(data.length > 1){
			console.warn('Too many series for map , selecting first serie')
		}


        return {
			title: {
				text: config.chartName
			},

			tooltip: {
				trigger: 'item',
				showDelay: 0,
				transitionDuration: 0.2,

			},

			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen
			}),

			visualMap: {
				left: 'right',
				min: 500000,
				max: 38000000,
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
				name:serie.description,
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


    // * POLAR BAR
    public buildPolarBar(args:{config : IndicatorDashboardPolarBarChartConfig, labels: string[], inputSeries: ChartSerie, onDownload?:() => void, onFiltersOpen?:() => void}): EChartsOption{
		const {config, labels, inputSeries, onDownload, onFiltersOpen} = args;
		const series = Object.keys(inputSeries).map((key,_index) => {
			// return this._buildPolarBarSerie(config as any, inputSeries[key].name ,inputSeries[key].data, (index) => index * 10 + (index * 10) )
			return this._buildPolarBarSerie({
				animationDelay:(index) => index * 10 + (index * 10),
				config: config, 
				data: inputSeries[key].data,
				name: inputSeries[key].name,
				color: inputSeries[key].color
			})
		});
		const dataZoom = this._buildDataZoom(config);

		return {
			title:{
				text: config.chartName,
			},
			angleAxis: {
			  type: 'category',
			  data: labels
			},
			toolbox: this._buildToolbox({config, onFiltersOpen, onDownload}),
			radiusAxis: {name: config?.radiusAxis?.name},
			polar: {},
			series,
			legend: this._buildLegend(config.legend),
			dataZoom,
        }
    }


    // * SCATTER OPTIONS
	public buildScatterOptions(args:{config: IndicatorDashboardScatterChartConfig, onDownload?:() => void, onFiltersOpen?:() => void}): EChartsOption{
		const {config , onDownload, onFiltersOpen} = args;
        return{
			
			title:{
				text: config.chartName
			},
			legend: this._buildLegend(config.legend),
			xAxis: {
				// type: 'value',
				// data: new Array(7).fill(0).map((_, index) => Math.floor(Math.random()*1000))
			},
			yAxis: { 
				// type:'value'
			},
			toolbox: this._buildToolbox({
				onDownload,
				config, 
				onFiltersOpen
			}),
			series: [
				{
					name: '1967',
					data: scatterData,
					type: 'scatter',
					symbolSize: function (data) {
					  return Math.sqrt(data[2]) / 5e2;
					},
					emphasis: {
					  focus: 'series',
					  label: {
						show: true,
						formatter: function (param) {
						  return param.data[2];
						},
						position: 'top'
					  }
					},
					itemStyle: {
					  shadowBlur: 10,
					  shadowColor: 'rgba(120, 36, 50, 0.5)',
					  shadowOffsetY: 5,
					}
				  },
				{
					name: '1980',
					data: scatterData,
					type: 'scatter',
					symbolSize: function (data) {
					  return Math.sqrt(data[2]) / 3e2;
					},
					emphasis: {
					  focus: 'series',
					  label: {
						show: true,
						formatter: function (param) {
						  return param.data[2];
						},
						position: 'top'
					  }
					},
					itemStyle: {
					  shadowBlur: 10,
					  shadowColor: 'rgba(120, 36, 50, 0.5)',
					  shadowOffsetY: 5,
					}
				  },
			]
		}
    }


	public  buildGraphOptions(args:{config: IndicatorDashboardGraphChartConfig, graph: GraphOptions, onDownload?:() => void, onFiltersOpen?:() => void}): EChartsOption{
		const {config, graph, onDownload, onFiltersOpen} = args;
		return {
			tooltip: {},
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen
			}),
			legend: [ // TODO SPECIFIC GRAPH BUILDER
			  {
				orient: 'vertical',
				right:10,
				top:'center',
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
	public buildPie(args:{config:IndicatorDashboardPieChartConfig, pieData: PieDataRecord[], onDownload?:() => void, onFiltersOpen?:() => void}):EChartsOption{
		const {config, pieData, onDownload, onFiltersOpen} = args;
        return {
            title: {
                text: config.chartName,
			},
			legend: this._buildLegend(config.legend),
			tooltip:{
				trigger: 'item'
			},
			toolbox:this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen
			}),
            series:[
                this._buildPie(config as IndicatorDashboardPieChartConfig, pieData)
            ]
        }
    }


    // *LINECHART
    public buildLine(params: {config: IndicatorDashboardLineChartConfig, labels:string[], inputSeries: ChartSerie, onFiltersOpen?: () => void, onDownload?: () => void}): EChartsOption{
		const {config,labels, inputSeries, onFiltersOpen, onDownload } = params;
		const dataZoom  = this._buildDataZoom(config);
		

		const series = Object.keys(inputSeries).map((key,_index) => {
			return this._buildSerie({
				animationDelay: index => index * 10 + (index * 10),
				config: config,
				data: inputSeries[key].data,
				name: inputSeries[key].name,
				color: inputSeries[key].color
			});
		});


        return {
			title: {
				text: config.chartName
			},
			legend: this._buildLegend(config.legend),
			tooltip: {
				trigger: 'axis',
				
			},
			xAxis: this._buildXAxis(config, labels),
			yAxis: this._buildYAxis(config, labels),
			dataZoom,
			series,
			toolbox:this._buildToolbox({config, onDownload, onFiltersOpen}),
			animationEasing: 'elasticOut',
			animationDelayUpdate: (idx) => idx * 5,
		};
    }



    // *BARCHART
    public buildBar(args:{config: IndicatorDashboardBarChartConfig, labels:string[], inputSeries: ChartSerie, onDownload?:() => void, onFiltersOpen?:() => void}): EChartsOption{
		const {config, labels, inputSeries, onDownload, onFiltersOpen} = args;
		
		const series = Object.keys(inputSeries).map((key,_index) => {
			return this._buildSerie({
				animationDelay: index => index * 10 + (index * 10),
				config: config,
				data: inputSeries[key].data,
				name: inputSeries[key].name,
				color: inputSeries[key].color
			})
		});
		const dataZoom = this._buildDataZoom(config);
        return {
			title: {
				text: config.chartName
			},
			legend: this._buildLegend(config.legend),
			tooltip: {},
			toolbox: this._buildToolbox({
				config,
				onDownload,
				onFiltersOpen
			}),
			xAxis: this._buildXAxis(config, labels),
			yAxis: this._buildYAxis(config, labels),
			dataZoom,
			series,
			animationEasing: 'elasticOut',
			animationDelayUpdate: (idx) => idx * 5,
		};
    }

	
	
    //* PRIVATES


	private _buildDataZoom(config:IndicatorDashboardBarChartConfig |  IndicatorDashboardLineChartConfig | IndicatorDashboardPolarBarChartConfig): DataZoomComponentOption[]{
		let dataZoom = [];
		if(config.dataZoom){
			if(config.dataZoom.inside){
				dataZoom.push({
					type: 'inside',
				})
			}
			if(config.dataZoom.slider){
				dataZoom.push({
					type: 'slider',
				})
			}
		}
		return dataZoom;
	}





    private _buildPie(config:IndicatorDashboardPieChartConfig, data?: PieDataRecord[]):SeriesOption{

		const colors =  data?.map(d => d.color);
		const color = colors.filter(x => x).length ? colors : [];


		const chart :SeriesOption  =  {
			type: 'pie',
			data: data ?? [],
			radius: ['40%', '70%'],
			color
		}

		if(config.roseType){
			chart.roseType = config.roseType as any;
		}


		return chart;
	}

    private _buildSerie( params:{config: IndicatorDashboardLineChartConfig, name:string, data: number[], animationDelay: (index: number) => number, color?: string}):SeriesOption{
		const {config, name, data, animationDelay, color} = params;
		const serie:SeriesOption  = {
			name,
			type: config.type as any,
			data,
			color,
			animationDelay,
		};

		if(config.stack){
			(serie as any).stack = config.stack;
		}

		if(config.areaStyle){
			(serie as any).areaStyle = config.areaStyle;
		}

		return serie;
	}

    private _buildPolarBarSerie(params: {config: IndicatorDashboardPolarBarChartConfig, name:string, data: number[], animationDelay: (index: number) => number, color?:string}):SeriesOption{
		const { name, data, animationDelay, color} = params;
		const serie:SeriesOption  = {
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

	private _buildXAxis(config: IndicatorDashboardLineChartConfig, data:string[] = [] ):XAXisComponentOption{
		if(config.horizontal) return {
			name: config?.yAxis?.name,
		};
		return {
			name: config?.xAxis?.name,
			data,
			type: 'category',
			boundaryGap: config?.xAxis?.boundaryGap ?? true,
			silent: false,
			splitLine: {
				show: false,
			},
		}
	}

	private _buildYAxis(config:IndicatorDashboardLineChartConfig, data: string[]):YAXisComponentOption{
		if(!config.horizontal) return {
			name: config?.yAxis?.name
		};


		return {
			name: config?.xAxis?.name,
			boundaryGap: config?.xAxis?.boundaryGap ?? true,
			data,
			silent: false,
			splitLine: {
				show: false,
			},
		};
	}


	private _buildLegend(legendConfig: LegendConfig): null | LegendComponentOption {
		if(legendConfig) return {
			orient: 'vertical',
			right:10,
			top:'center'			
		};

		return null
	}

	// TODO SAVE AS IMAGE AND MYDOWNLOAD VIA CONFIGURATION
	private _buildToolbox(args:{config: BaseIndicatorDashboardChartConfig, onFiltersOpen?:() => void, onDownload?:() => void }): ToolboxComponentOption{
		
		const {config, onDownload, onFiltersOpen} = args;

		const feature: any = {};
		if(config.filters?.length){
			feature.myOnFilterChange = {
				show: true,
				title: 'Open filters',
				icon: 'path://M3.853 54.87C10.47 40.9 24.54 32 40 32H472C487.5 32 501.5 40.9 508.1 54.87C514.8 68.84 512.7 85.37 502.1 97.33L320 320.9V448C320 460.1 313.2 471.2 302.3 476.6C291.5 482 278.5 480.9 268.8 473.6L204.8 425.6C196.7 419.6 192 410.1 192 400V320.9L9.042 97.33C-.745 85.37-2.765 68.84 3.854 54.87L3.853 54.87z',
				onclick:() =>{
					this._zone.run(() =>{
						onFiltersOpen?.();
					})
				}
			}
		}


		if(config.chartDownloadImage){
			feature.saveAsImage = {
				title:'Download image',
				type: 'png',
			}
		}

		if(config.chartDownloadData){
			feature.myOnDownload = {
				show: true,
					title: 'Download Data',
					icon: 'path://M224 0V128C224 145.7 238.3 160 256 160H384V448C384 483.3 355.3 512 320 512H64C28.65 512 0 483.3 0 448V64C0 28.65 28.65 0 64 0H224zM80 224C57.91 224 40 241.9 40 264V344C40 366.1 57.91 384 80 384H96C118.1 384 136 366.1 136 344V336C136 327.2 128.8 320 120 320C111.2 320 104 327.2 104 336V344C104 348.4 100.4 352 96 352H80C75.58 352 72 348.4 72 344V264C72 259.6 75.58 256 80 256H96C100.4 256 104 259.6 104 264V272C104 280.8 111.2 288 120 288C128.8 288 136 280.8 136 272V264C136 241.9 118.1 224 96 224H80zM175.4 310.6L200.8 325.1C205.2 327.7 208 332.5 208 337.6C208 345.6 201.6 352 193.6 352H168C159.2 352 152 359.2 152 368C152 376.8 159.2 384 168 384H193.6C219.2 384 240 363.2 240 337.6C240 320.1 231.1 305.6 216.6 297.4L191.2 282.9C186.8 280.3 184 275.5 184 270.4C184 262.4 190.4 256 198.4 256H216C224.8 256 232 248.8 232 240C232 231.2 224.8 224 216 224H198.4C172.8 224 152 244.8 152 270.4C152 287 160.9 302.4 175.4 310.6zM280 240C280 231.2 272.8 224 264 224C255.2 224 248 231.2 248 240V271.6C248 306.3 258.3 340.3 277.6 369.2L282.7 376.9C285.7 381.3 290.6 384 296 384C301.4 384 306.3 381.3 309.3 376.9L314.4 369.2C333.7 340.3 344 306.3 344 271.6V240C344 231.2 336.8 224 328 224C319.2 224 312 231.2 312 240V271.6C312 294.6 306.5 317.2 296 337.5C285.5 317.2 280 294.6 280 271.6V240zM256 0L384 128H256V0z',
					onclick:() =>{
						this._zone.run(() =>{
							onDownload?.();
						})
					}
			}
		}
		return {
			feature
		};
	}
}



interface SeriesConfiguration{
	fieldCode: string;
	metrics: ExtendedMetric[];
	fieldFormatter?: FieldFormatterConfig;
}

interface StandAloneConfiguration{
	metrics: ExtendedMetric[];
	fieldCode: string;
}

export interface ConfigurationMetrics{
	seriesConfiguration?:SeriesConfiguration;
	standAloneConfiguration?:StandAloneConfiguration;
}

export interface ExtendedMetric extends Metric{
	_id: string;
	description:string;
	color?: string;
}


export interface GraphOptions{
	nodes: GraphNode[];
	links: GraphLink[];
	categories: GraphCategory[];
}


export interface GraphNode{
	id: string,
	name: string,
	symbolSize: number;
	x: number;
	y: number;
	value: number;
	category: number
}

export interface GraphLink{
	source: string;
	target: string;
}

export interface GraphCategory{
	name: string;
}