import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Guid } from '@common/types/guid';
import { DownloadReportService } from '@app/core/services/http/download-report.service';
import * as FileSaver from 'file-saver';

@Component({
	selector: 'app-download-report',
	templateUrl: './download-report.component.html',
	styleUrls: ['./download-report.component.scss']
})
export class DownloadReportComponent implements OnInit {

	constructor(
		private route: ActivatedRoute,
		private downloadReportService: DownloadReportService
	) {
	}

	ngOnInit(): void {
		this.route.paramMap.subscribe((paramMap: ParamMap) => {
			const itemId = paramMap.get('id');
			this.downloadReportService.downloadReport(Guid.parse(itemId))
			.subscribe(
				data => {
					this.saveFile(data);
				},
				error => console.log(error)
			);
		});
	}

	private saveFile(data: any) {
		FileSaver.saveAs(data);
	}

}
