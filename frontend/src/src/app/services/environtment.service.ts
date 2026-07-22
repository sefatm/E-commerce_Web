import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  get env() {
    return environment;
  }

  get isProduction(): boolean {
    return environment.production;
  }

  get isDevelopment(): boolean {
    return !environment.production && environment.name === 'development';
  }

  get isStaging(): boolean {
    return !environment.production && environment.name === 'staging';
  }

  get apiUrl(): string {
    return environment.apiUrl;
  }

  get currencySymbol(): string {
    return environment.ecommerce.currencySymbol;
  }

  get currency(): string {
    return environment.ecommerce.currency;
  }

  get tokenKey(): string {
    return environment.auth.tokenKey;
  }

  get userKey(): string {
    return environment.auth.userKey;
  }

  get maxFileSize(): number {
    return environment.upload.maxFileSize;
  }

  get allowedImageTypes(): string[] {
    return environment.upload.allowedImageTypes;
  }

  isFeatureEnabled(feature: keyof typeof environment.features): boolean {
    return environment.features[feature] === true;
  }

  get isSSLCommerzEnabled(): boolean {
    return environment.services.sslCommerz.enabled;
  }

  get isSSLCommerzSandbox(): boolean {
    return environment.services.sslCommerz.isSandbox;
  }

  get sslCommerzUrl(): string {
    return environment.services.sslCommerz.isSandbox
      ? environment.services.sslCommerz.sandboxUrl
      : environment.services.sslCommerz.liveUrl;
  }

  get isBkashEnabled(): boolean {
    return environment.services.bKash.enabled;
  }

  get isBkashSandbox(): boolean {
    return environment.services.bKash.isSandbox;
  }

  get lowStockThreshold(): number {
    return environment.ecommerce.lowStockThreshold;
  }

  get defaultPageLimit(): number {
    return environment.pagination.defaultLimit;
  }

  log(level: string, message: string, data?: any): void {
    if (!environment.logging.enabled) return;

    const levels = ['debug', 'info', 'warn', 'error'];
    const currentLevelIndex = levels.indexOf(environment.logging.level);
    const messageLevelIndex = levels.indexOf(level);

    if (messageLevelIndex < currentLevelIndex) return;

    const prefix = `[Rural Mart ${level.toUpperCase()}]`;
    const timestamp = new Date().toISOString();

    switch (level) {
      case 'debug':
        console.debug(`${prefix} [${timestamp}]`, message, data || '');
        break;
      case 'info':
        console.info(`${prefix} [${timestamp}]`, message, data || '');
        break;
      case 'warn':
        console.warn(`${prefix} [${timestamp}]`, message, data || '');
        break;
      case 'error':
        console.error(`${prefix} [${timestamp}]`, message, data || '');
        break;
    }
  }
}