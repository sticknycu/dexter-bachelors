import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelcomeHomepageComponent } from './welcome-homepage.component';

describe('WelcomeHomepageComponent', () => {
  let component: WelcomeHomepageComponent;
  let fixture: ComponentFixture<WelcomeHomepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WelcomeHomepageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(WelcomeHomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
