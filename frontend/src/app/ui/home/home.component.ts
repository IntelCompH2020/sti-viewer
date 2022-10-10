import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { IsActive } from '@app/core/enum/is-active.enum';
import { Bookmark } from '@app/core/model/bookmark/bookmark.model';
import { BookmarkLookup } from '@app/core/query/bookmark.lookup';
import { BookmarkService } from '@app/core/services/http/bookmark.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseComponent } from '@common/base/base.component';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { TranslateService } from '@ngx-translate/core';
import { filter, map,switchMap,takeUntil, tap } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { IndicatorQueryParams } from '../indicator-dashboard/indicator-dashboard.component';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss'],
	animations: GENERAL_ANIMATIONS
})
export class HomeComponent extends BaseComponent implements OnInit{


	// bookmarks = Array(12).fill(0).map(() =>({
	// 	description: 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Cum ipsa iusto natus exercitationem voluptas blanditiis! Voluptates, esse. Voluptatibus, nihil, explicabo facilis dolorum et nobis officia dolorem neque quos ab exercitationem.',
	// 	img: [
	// 		'../../../assets/images/back-pink.png',
	// 		'../../../assets/images/back-basic.png',
	// 		'../../../assets/images/back-primary.png']
	// 		[Math.floor((Math.random() * 10) % 3)]
	// }))
	bookmarks:ComponentBookmark[] = [];

	PAGE_SIZE = 5;

	totalBookmarks: number;

	get hasMoreBookmarks (): boolean{
		return this.totalBookmarks > this.bookmarks?.length;
	}
	

	constructor(
		private router: Router,
		private bookmarkService: BookmarkService,
		private queryParamsService: QueryParamsService,
		private dialog: MatDialog,
		protected language: TranslateService,
		protected uiNotificationService: UiNotificationService,
	) {
		super();
	}

	ngOnInit(): void {
		this._refreshBookmarks();
	}

	navigateToIndicators():void{
		this.router.navigate(['indicator-report']);
	}


	navigateToBookmark(bookmark: {value: string}){
		const params = JSON.parse(bookmark.value);

		this.router.navigate(['indicator-report', 'dashboard'], {
			queryParams: {
				params: this.queryParamsService.serializeObject<IndicatorQueryParams>(params)
			  }
		})
	}

	search(searchTerm: string): void{
		this.router.navigate(['search'], {queryParams:{
			q: searchTerm
		}})
	}

	removeBookmark(bookmark: ComponentBookmark): void{

		this.dialog.open(ConfirmationDialogComponent,{
			data:{
				message: this.language.instant('APP.HOME-COMPONENT.BOOKMARKS.DELETE-BOOKMARK-CONFIRMATION-MESSAGE'),
				confirmButton: this.language.instant('APP.HOME-COMPONENT.BOOKMARKS.CONFIRM-YES'),
				cancelButton: this.language.instant('APP.HOME-COMPONENT.BOOKMARKS.CANCEL')
			}
		})
		.afterClosed()
		.pipe(
			filter(x => x),
			switchMap(() =>{

				return this.bookmarkService.delete(bookmark.id)
			}),
			takeUntil(this._destroyed)
		)
		.subscribe(() => {
			this._refreshBookmarks();
			this.uiNotificationService.snackBarNotification(this.language.instant('APP.HOME-COMPONENT.BOOKMARKS.BOOKMARK-DELETED'), SnackBarNotificationLevel.Success);
		});
	}


	private _refreshBookmarks(){
		this.loadMore(true);
	}


	loadMore(emptyBookmarks: boolean = false): void{
		const lookup = this._buildLookup(emptyBookmarks)

		this.bookmarkService.query(lookup)
			.pipe(
				tap(bookmarks => this.totalBookmarks = bookmarks.count),
				map(
					bookmarks => bookmarks?.items.map(bookmark => this._bookmarkModelToComponentBookmark(bookmark))
				),
				takeUntil(
					this._destroyed
				)
			).subscribe(bookmaks => {
				if(emptyBookmarks) {
					this.bookmarks = [];
				}
				this.bookmarks.push(...bookmaks)
			});
	}


	private _buildLookup(skipPaging:boolean  = true): BookmarkLookup {
		const lookup = new BookmarkLookup();
		lookup.project = {
			fields:[
				nameof<Bookmark>(x => x.id),
				nameof<Bookmark>(x => x.name),
				nameof<Bookmark>(x => x.value),
			]
		}
		lookup.isActive = [IsActive.Active];
		lookup.page = {
			offset: skipPaging ? 0 : this.bookmarks.length,
			size: this.PAGE_SIZE
		}
		lookup.order = {
			items:[
				'-' + nameof<Bookmark>(x => x.updatedAt)
			]
		}

		lookup.metadata = {
			countAll: true
		}

		return lookup;
	}

	private _bookmarkModelToComponentBookmark(bookmark:Bookmark):ComponentBookmark{
		return {
				id: bookmark.id,
				img:[
						'../../../assets/images/back-pink.png',
						'../../../assets/images/back-basic.png',
						'../../../assets/images/back-primary.png'
					]
					[Math.floor((Math.random() * 10) % 3)],
				description: bookmark.name,
				value: bookmark.value

			}
	} 
}


interface ComponentBookmark{
	value: string,
	id: Guid;
	img: string;
	description: string;
}