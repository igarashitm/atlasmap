/* tslint:disable:no-unused-variable */

import { Component, Input } from '@angular/core';
import { TestBed, async, inject } from '@angular/core/testing';
import { AtlasmapImportComponent } from './atlasmap-import.component';

describe('AtlasmapImportComponent', () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [AtlasmapImportComponent],
      });
    });

    it(
      'should ...',
      inject([AtlasmapImportComponent], (service: AtlasmapImportComponent) => {
        expect(service).toBeTruthy();
      }),
    );
  });
