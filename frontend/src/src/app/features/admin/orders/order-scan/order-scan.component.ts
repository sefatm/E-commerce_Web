import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Order } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';

@Component({ selector:'app-order-scan', templateUrl:'./order-scan.component.html', styleUrls:['./order-scan.component.css'] })
export class OrderScanComponent implements OnInit {
  order: Order | null = null; loading = true; status = ''; note = ''; message = ''; error = ''; token = '';
  steps = ['ACCEPTED','PROCESSING','PACKED','READY_FOR_PICKUP','SHIPPED','OUT_FOR_DELIVERY','DELIVERED','COD_COLLECTED'];
  constructor(private route:ActivatedRoute, private service:OrderService) {}
  ngOnInit():void { const id=Number(this.route.snapshot.paramMap.get('id')); this.token=this.route.snapshot.queryParamMap.get('token') || ''; this.service.getById(id).subscribe({next:o=>{this.order=o;this.loading=false},error:()=>{this.error='Order not found';this.loading=false}}); }
  save():void { if(!this.order?.id || !this.status) return; this.message='';this.error=''; this.service.scanUpdate(this.order.id,this.status,this.note,this.token).subscribe({next:m=>{this.message=m;this.order!.status=this.status},error:e=>this.error=e?.error || 'Update failed'}); }
}
