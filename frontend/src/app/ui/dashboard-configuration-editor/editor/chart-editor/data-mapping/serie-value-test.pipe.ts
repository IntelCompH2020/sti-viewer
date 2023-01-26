import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'test' })
export class SerieValueTestPipe implements PipeTransform {
	constructor() { }
    transform(record: Record<string, string>, ...args: ('target' | 'value')[]) {
        try{
            const target = Object.keys(record)?.[0];
            if(args?.includes('value')){
                return record[target];
            }

            return target;
        }catch{
            return ''
        }
    }
}




@Pipe({ name: 'connectionTest' })
export class ConnectionTestPipe implements PipeTransform {
	constructor() { }
    transform(record: Record<string, string>): {target: string, value: string}[] | null {
        if(!record){
            return null
        }

        try{
            return Object.keys(record).map(key =>({target: key, value: record[key]}))
        }catch{
            return null
        }
    }
}
