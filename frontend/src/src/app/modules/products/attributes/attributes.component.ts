import { Component, OnInit } from '@angular/core';
import { AttributeService } from 'src/app/services/attributes.service';

@Component({
  selector: 'app-attributes',
  templateUrl: './attributes.component.html',
  styleUrls: ['./attributes.component.css']
})
export class AttributesComponent implements OnInit {

  attributes: any[] = [];
  showForm = false;
  isLoading = false;

  attributeName = '';
  attributeValuesArray: string[] = []; 
  newValue = '';

  constructor(private attributeService: AttributeService) {}

  ngOnInit(): void { this.loadAttributes(); }

  loadAttributes(): void {
    this.isLoading = true;
    this.attributeService.getAttributes().subscribe({
      next: (res: any) => {
        this.attributes = res?.data ?? res;
        this.isLoading = false;
      },
      error: (err: any) => { console.error('Load error:', err); this.isLoading = false; }
    });
  }

  addValue(): void {
    const val = this.newValue.replace(',', '').trim();
    if (val && !this.attributeValuesArray.includes(val)) {
      this.attributeValuesArray.push(val);
    }
    this.newValue = '';
  }

  removeValue(index: number): void {
    this.attributeValuesArray.splice(index, 1);
  }

  onSubmit(): void {
    if (!this.attributeName.trim() || this.attributeValuesArray.length === 0) return;

    const payload = {
      name: this.attributeName.trim(),
      values: this.attributeValuesArray
    };

    this.attributeService.createAttribute(payload).subscribe({
      next: () => {
        this.loadAttributes();
        this.cancelForm();
      },
      error: (err: any) => console.error('Create error:', err)
    });
  }

  deleteAttribute(id: string): void {
    if (!confirm('এই attribute টি delete করবেন?')) return;
    this.attributeService.deleteAttribute(id).subscribe({
      next: () => this.loadAttributes(),
      error: (err: any) => console.error('Delete error:', err)
    });
  }

  cancelForm(): void {
    this.showForm = false;
    this.attributeName = '';
    this.attributeValuesArray = [];
    this.newValue = '';
  }
}
