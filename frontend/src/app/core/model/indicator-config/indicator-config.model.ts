import { Indicator } from '../indicator/indicator.model';


export interface IndicatorConfig {
  indicator: Indicator;
  viewConfigs: ViewConfig[];
}

export interface ViewConfig {
  id: string;
  name: string;

}
