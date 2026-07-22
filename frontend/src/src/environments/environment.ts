
export const environment = {
  production: false,
  name: 'development',

  apiUrl: 'http://localhost:8080',
  apiTimeout: 30000,

  auth: {
    tokenKey: 'rural_mart_token',
    refreshTokenKey: 'rural_mart_refresh_token',
    userKey: 'rural_mart_user',
    tokenExpiryKey: 'rural_mart_token_expiry',
    defaultExpiry: 3600 
  },

  upload: {
    maxFileSize: 5 * 1024 * 1024, 
    allowedImageTypes: ['image/jpeg', 'image/png', 'image/webp', 'image/gif'],
    allowedDocumentTypes: ['application/pdf', 'application/msword'],
    imageQuality: 0.8,
    thumbnailSize: { width: 300, height: 300 },
    mediumSize: { width: 600, height: 600 }
  },

  pagination: {
    defaultPage: 1,
    defaultLimit: 10,
    maxLimit: 100,
    productsPerPage: 12,
    ordersPerPage: 10,
    customersPerPage: 15
  },

  ecommerce: {
    currency: 'BDT',
    currencySymbol: '৳',
    currencyPosition: 'before', 
    decimalPlaces: 2,
    thousandsSeparator: ',',
    taxEnabled: true,
    defaultTaxRate: 15, 
    lowStockThreshold: 10,
    outOfStockThreshold: 0,
    allowGuestCheckout: false,
    enableReviews: true,
    enableWishlist: true,
    enableCompare: true,
    orderPrefix: 'RM',
    invoicePrefix: 'INV'
  },

  features: {
    enableDarkMode: true,
    enableMultiLanguage: false,
    enableMultiCurrency: false,
    enableAnalytics: false,
    enableNotifications: true,
    enableChat: false,
    enableProductVariants: true,
    enableDigitalProducts: true,
    enableBulkActions: true,
    enableExport: true,
    enableImport: true
  },

  services: {
    sslCommerz: {
      enabled: true,
      storeId: 'rural_mart_test',
      storePassword: 'rural_mart_test_password',
      isSandbox: true,
      sandboxUrl: 'https://sandbox.sslcommerz.com/gwprocess/v4/api.php',
      liveUrl: 'https://securepay.sslcommerz.com/gwprocess/v4/api.php'
    },
    bKash: {
      enabled: false,
      appKey: '',
      appSecret: '',
      isSandbox: true,
      sandboxUrl: 'https://tokenized.sandbox.bka.sh/v1.2.0-beta/tokenized/checkout',
      liveUrl: 'https://tokenized.pay.bka.sh/v1.2.0-beta/tokenized/checkout'
    },
    stripe: {
      enabled: false,
      publishableKey: 'pk_test_xxxxx',
      secretKey: 'sk_test_xxxxx'
    },

    cloudinary: {
      enabled: true,
      cloudName: 'rural-mart-ecommerce',
      uploadPreset: 'rural_mart_products',
      apiKey: '',
      folder: 'rural-mart/products'
    },
    awsS3: {
      enabled: false,
      bucket: '',
      region: 'ap-south-1',
      accessKeyId: '',
      secretAccessKey: ''
    },

    email: {
      provider: 'smtp', 
      from: 'noreply@ruralmart.com',
      fromName: 'Rural Mart Ecommerce'
    },

    sms: {
      provider: 'none',
      twilio: {
        accountSid: '',
        authToken: '',
        fromNumber: ''
      }
    },

    googleAnalytics: {
      enabled: false,
      trackingId: ''
    },
    facebookPixel: {
      enabled: false,
      pixelId: ''
    }
  },

  googleMaps: {
    enabled: false,
    apiKey: '',
    defaultCenter: { lat: 23.8103, lng: 90.4125 }, 
    defaultZoom: 12
  },

  logging: {
    enabled: true,
    level: 'debug', 
    logApiCalls: true,
    logErrors: true,
    maxLogEntries: 100
  }
};
