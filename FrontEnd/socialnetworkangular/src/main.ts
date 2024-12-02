import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';


platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
  const theme = localStorage.getItem('theme');
    
  // Nếu không có theme trong localStorage, mặc định là 'light'
  if (!theme) {
    localStorage.setItem('theme', 'light');
  }