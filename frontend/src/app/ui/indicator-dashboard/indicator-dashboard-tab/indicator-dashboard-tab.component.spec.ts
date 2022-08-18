import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IndicatorDashboardTabComponent } from './indicator-dashboard-tab.component';

describe('IndicatorDashboardTabComponent', () => {
  let component: IndicatorDashboardTabComponent;
  let fixture: ComponentFixture<IndicatorDashboardTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [IndicatorDashboardTabComponent],
    teardown: { destroyAfterEach: false }
})
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndicatorDashboardTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
