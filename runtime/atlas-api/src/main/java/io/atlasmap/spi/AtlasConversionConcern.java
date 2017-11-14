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
package io.atlasmap.spi;

/**
 */
public enum AtlasConversionConcern {
    NONE("none", "Conversion from '%s' to '%s' is supported"),
    RANGE("range", "Conversion from '%s' to '%s' can cause out of range exceptions"),
    FORMAT("format", "Conversion from '%s' to '%s' can cause numeric format exceptions"),
    UNSUPPORTED("unsupported", "Conversions from '%s' to '%s' is not supported");

    private String name;
    private String message;

    AtlasConversionConcern(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String value() {
        return this.name;
    }

    public String getMessage(AtlasConversionInfo converterAnno) {
        String source = (converterAnno.sourceClassName() == null || converterAnno.sourceClassName().isEmpty())
                ? converterAnno.sourceType().name() : converterAnno.sourceClassName();
        String target = (converterAnno.targetClassName() == null || converterAnno.targetClassName().isEmpty())
                ? converterAnno.targetType().name() : converterAnno.targetClassName();
        return String.format(this.message, source, target);
    }

    public static AtlasConversionConcern fromValue(String v) {
        for (AtlasConversionConcern c : AtlasConversionConcern.values()) {
            if (c.name().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
