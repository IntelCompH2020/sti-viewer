import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder } from '@angular/forms';
import { IndicatorElasticBaseType } from '@app/core/enum/indicator-elastic-base-type.enum';
import { Field } from '@app/core/model/indicator-elastic/indicator-elastic';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IndicatorEditorModel } from '../indicator-editor.model';

@Component({
  selector: 'app-access-request-config',
  templateUrl: './access-request-config.component.html',
  styleUrls: ['./access-request-config.component.css']
})
export class AccessRequestConfigComponent implements OnInit {

  @Input() form: UntypedFormArray;
  @Input() editorModel: IndicatorEditorModel;
  selectedColumns: string[] = [];
  constructor() { }

  ngOnInit(): void {

  }
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['form']) {
      this.selectedColumns = this.form.value.map(x => x.code);
      this.form.clear();
    }
  }
  onSelectColumn(changes) {

    if (!changes) { return; }

    if (!changes.source._selected) {
      this.form.value.forEach((element, key) => {
        if (changes.source.value == element.code)
          this.form.removeAt(key);
      });
    } else {
      this.form.push(new UntypedFormBuilder().group({ code: changes.source.value }));
    }

  }
  clearFormArray = (formArray: UntypedFormArray) => {
    while (formArray.length !== 0) {
      formArray.removeAt(0)
    }
  }

}
