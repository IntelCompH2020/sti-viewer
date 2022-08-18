import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicatorDashboardComponent } from './indicator-dashboard.component';

describe('IndicatorDashboardComponent', () => {
  let component: IndicatorDashboardComponent;
  let fixture: ComponentFixture<IndicatorDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [IndicatorDashboardComponent],
    teardown: { destroyAfterEach: false }
})
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicatorDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
