import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ChartShareConfig } from '../indicator-dashboard-config';
import { AuthService } from '@app/core/services/ui/auth.service';
import { AppPermission } from '@app/core/enum/permission.enum';


@Component({
    selector: 'app-value-card',
    templateUrl:'./value-card.component.html',
    styleUrls: [
        './value-card.component.scss'
    ]
})
export class ValueCardComponent{

    @Input() value: string | number;

    @Input() description: string;
    @Input() chartShare: ChartShareConfig;

	@Output() onShare = new EventEmitter<void>();

	public canShare = false;
	constructor(
		public authService: AuthService
	) {
		this.canShare = this.authService.hasPermission(AppPermission.CreateChartExternalToken);
	}

	public shareGraph(): void {
		if (this.canShare) {
			this.onShare.emit();
		}
	}
}
