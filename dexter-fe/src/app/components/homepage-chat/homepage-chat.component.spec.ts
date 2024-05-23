import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomepageChatComponent } from './homepage-chat.component';

describe('HomepageChatComponent', () => {
  let component: HomepageChatComponent;
  let fixture: ComponentFixture<HomepageChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomepageChatComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HomepageChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
