/**
 * Copyright (C) 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atlasmap.reference.xml_to_java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import io.atlasmap.api.AtlasContext;
import io.atlasmap.api.AtlasSession;
import io.atlasmap.java.test.TargetFlatPrimitiveClass;
import io.atlasmap.reference.AtlasMappingBaseTest;
import io.atlasmap.reference.AtlasTestUtil;

public class XmlJavaAutoConversionTest extends AtlasMappingBaseTest {

    protected Object executeMapping(String fileName) throws Exception {
        AtlasContext context = atlasContextFactory.createContext(new File(fileName).toURI());
        AtlasSession session = context.createSession();
        String source = AtlasTestUtil
                .loadFileAsString("src/test/resources/xmlToJava/atlas-xml-flatprimitive-attribute-autoconversion.xml");
        session.setDefaultSourceDocument(source);
        context.process(session);

        assertFalse(printAudit(session), session.hasErrors());
        Object object = session.getDefaultTargetDocument();
        assertNotNull(object);
        assertTrue(object instanceof TargetFlatPrimitiveClass);
        return object;
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion1() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-1.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion1((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion2() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-2.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion2((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion3() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-3.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion3((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion4() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-4.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion4((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion5() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-5.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion5((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion6() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-6.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion6((TargetFlatPrimitiveClass) object);
    }

    @Test
    public void testProcessXmlJavaFlatFieldMappingAutoConversion7() throws Exception {
        Object object = executeMapping(
                "src/test/resources/xmlToJava/atlasmapping-flatprimitive-attribute-autoconversion-7.xml");
        AtlasTestUtil.validateFlatPrimitiveClassPrimitiveFieldAutoConversion7((TargetFlatPrimitiveClass) object);
    }

}
