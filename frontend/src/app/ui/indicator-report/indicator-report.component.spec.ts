import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicatorReportComponent } from './indicator-report.component';

describe('IndicatorReportComponent', () => {
  let component: IndicatorReportComponent;
  let fixture: ComponentFixture<IndicatorReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [IndicatorReportComponent],
    teardown: { destroyAfterEach: false }
})
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicatorReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
