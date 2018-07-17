import { Component } from '@angular/core';
// import { AppModule } from '../../app/src/app/app.module';

@Component({
  selector: 'atlasmap-import',
  templateUrl: './atlasmap-import.component.html',
  styleUrls: ['./app/lib/atlasmap-data-mapper/components/data-mapper-app.component.css']
})
// templateUrl: './index.html',
export class AtlasmapImportComponent {

  rat(paramName: string): boolean {
    console.log('arg is ' + paramName);
    return false;
  }

  handleClick(): void {
    console.log('handle click ');
  }
}
