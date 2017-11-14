/**
 * Copyright (C) 2017 Red Hat, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atlasmap.core;

import io.atlasmap.api.AtlasConverter;
import io.atlasmap.api.AtlasConversionException;
import io.atlasmap.spi.AtlasConversionInfo;
import io.atlasmap.v2.FieldType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class MockCustomConverter implements AtlasConverter<Date> {

    @AtlasConversionInfo(sourceType = FieldType.DATE, targetType = FieldType.COMPLEX, sourceClassName = "java.util.Date", targetClassName = "java.time.ZonedDateTime")
    public ZonedDateTime convertToZonedDateTime(Date date) throws AtlasConversionException {
        return convertToZonedDateTime(date, ZoneId.systemDefault());
    }

    public ZonedDateTime convertToZonedDateTime(Date date, ZoneId zoneId) throws AtlasConversionException {
        return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
    }

    @AtlasConversionInfo(sourceType = FieldType.STRING, targetType = FieldType.STRING, sourceClassName = "java.lang.String", targetClassName = "java.lang.String")
    public String convertToString(String value) throws AtlasConversionException {
        return "aString";
    }

}
