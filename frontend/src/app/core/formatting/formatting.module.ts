import { DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { IsActiveTypePipe } from '@app/core/formatting/pipes/is-active-type.pipe';
import { CommonFormattingModule } from '@common/formatting/common-formatting.module';
import { PipeService } from '@common/formatting/pipe.service';
import { DashboardTabBlockToChartGroupBlockPipe } from './pipes/dashboard-chart-group.pipe';
import { SafeHtmlPipe } from './pipes/safe-html.pipe';

//
//
// This is shared module that provides all formatting utils. Its imported only once on the AppModule.
//
//
@NgModule({
	imports: [CommonFormattingModule],
	declarations: [
		IsActiveTypePipe,
		SafeHtmlPipe,
		DashboardTabBlockToChartGroupBlockPipe,
	],
	exports: [
		DashboardTabBlockToChartGroupBlockPipe,
		CommonFormattingModule,
		IsActiveTypePipe,
		SafeHtmlPipe,
	],
	providers: [
		DashboardTabBlockToChartGroupBlockPipe,
		AppEnumUtils,
		PipeService,
		SafeHtmlPipe,
		DatePipe,
		IsActiveTypePipe,
	],
})
export class FormattingModule {}
