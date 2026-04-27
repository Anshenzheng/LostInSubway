import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ItemListComponent } from './components/item-list/item-list.component';
import { ItemDetailComponent } from './components/item-detail/item-detail.component';
import { ItemCreateComponent } from './components/item-create/item-create.component';
import { MyItemsComponent } from './components/my-items/my-items.component';
import { MyClaimsComponent } from './components/my-claims/my-claims.component';
import { AdminItemsComponent } from './components/admin-items/admin-items.component';
import { AdminClaimsComponent } from './components/admin-claims/admin-claims.component';
import { StatisticsComponent } from './components/statistics/statistics.component';
import { AuthGuard } from './services/auth.guard';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'lost', 
    component: ItemListComponent,
    data: { itemType: null }
  },
  { 
    path: 'found', 
    component: ItemListComponent,
    data: { itemType: 'FOUND' as const }
  },
  { path: 'item/:id', component: ItemDetailComponent },
  { 
    path: 'publish', 
    component: ItemCreateComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'my-items', 
    component: MyItemsComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'my-claims', 
    component: MyClaimsComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'admin', 
    redirectTo: 'admin/items',
    pathMatch: 'full'
  },
  { 
    path: 'admin/items', 
    component: AdminItemsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'admin/claims', 
    component: AdminClaimsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'admin/statistics', 
    component: StatisticsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
