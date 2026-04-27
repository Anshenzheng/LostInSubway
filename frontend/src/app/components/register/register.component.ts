import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  error = '';
  success = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      confirmPassword: ['', Validators.required],
      realName: ['', [Validators.maxLength(50)]],
      phone: ['', [Validators.pattern(/^1[3-9]\d{9}$/)]],
      email: ['', [Validators.email, Validators.maxLength(100)]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(formGroup: FormGroup): void {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    }
  }

  get f() { return this.registerForm.controls; }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const request: RegisterRequest = {
      username: this.f['username'].value,
      password: this.f['password'].value,
      realName: this.f['realName'].value || undefined,
      phone: this.f['phone'].value || undefined,
      email: this.f['email'].value || undefined
    };

    this.authService.register(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = '注册成功，正在跳转到首页...';
          setTimeout(() => {
            this.router.navigate(['/']);
          }, 1500);
        } else {
          this.error = response.message || '注册失败';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message || '注册失败，请稍后重试';
        this.loading = false;
      }
    });
  }
}
