import { Pipe, PipeTransform } from '@angular/core';
import { EChartsOption, TitleComponentOption } from 'echarts';
import { ExtraFilterField } from './indicator-dashboard-chart.component';

@Pipe({ name: 'mergeEchartOption' })
export class MergeEchartOptionsPipe implements PipeTransform {


	transform(baseMerge: EChartsOption, extraFilterFields: ExtraFilterField[] ): EChartsOption {


        let subtitleMergeOption: EChartsOption = {};
        
        updateSubtitle:{

            if(!extraFilterFields?.length){
                break updateSubtitle;
            }

            const title = baseMerge.title;
            if(title && Array.isArray(title)){
                break updateSubtitle;
            }

            const previousSubtitle = (title as TitleComponentOption)?.subtext;
            const subtextAppend = extraFilterFields.map(x => x.displayName).filter(x => !!x).join(', ');

            if(!subtextAppend){
                break updateSubtitle
            }
            const subtext  = `${previousSubtitle ?? ''} (${subtextAppend})`

            subtitleMergeOption = {
                title:{
                    subtext
                }
            }

        }

        return {
            ...subtitleMergeOption,
            ...baseMerge
        }
	}
}
