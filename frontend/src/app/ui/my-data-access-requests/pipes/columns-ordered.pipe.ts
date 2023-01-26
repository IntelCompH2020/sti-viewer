import { Pipe, PipeTransform } from '@angular/core';
import { PortofolioColumnConfig } from '@app/core/model/portofolio-config/portofolio-config.model';

@Pipe({ name: 'columsOrdered' })
export class ColumnsOrderedPipe implements PipeTransform {

	public transform(value: {order: number}[], ...params): {order: number}[] {
        if(!value){
            return value;
        }
        return value.map(x => x).sort(
            (a, b) => {

                const aValue = a.order ?? 0;
                const bValue = b.order ?? 0;

                return params?.includes('desc') ? bValue - aValue : aValue - bValue;
            }
        );
	}
}
