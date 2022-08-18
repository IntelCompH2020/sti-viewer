export interface ElasticValuesResponse<T>{
    count: number;
	items: T[];
    afterKey: Map<string, object>;
}