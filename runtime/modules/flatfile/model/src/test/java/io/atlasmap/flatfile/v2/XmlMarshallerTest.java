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
package io.atlasmap.flatfile.v2;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.atlasmap.v2.AtlasMapping;

public class XmlMarshallerTest extends BaseMarshallerTest {

    private JAXBContext jaxbContext = null;
    private Marshaller marshaller = null;
    private Unmarshaller unmarshaller = null;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.deleteTestFolders = false;

        jaxbContext = JAXBContext.newInstance("io.atlasmap.v2:io.atlasmap.flatfile.v2");

        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        marshaller = null;
        unmarshaller = null;
        jaxbContext = null;
    }

    @Test
    public void testReferenceMapping() throws Exception {

        marshaller.marshal(generateAtlasMapping(),
                new File("target/junit/" + testName.getMethodName() + "/" + "atlasmapping.xml"));
        StreamSource fileSource = new StreamSource(
                new File("target/junit/" + testName.getMethodName() + "/" + "atlasmapping.xml"));
        JAXBElement<AtlasMapping> mappingElem = unmarshaller.unmarshal(fileSource, AtlasMapping.class);
        assertNotNull(mappingElem);
        assertNotNull(mappingElem.getValue());
        validateAtlasMapping(mappingElem.getValue());
    }

}
