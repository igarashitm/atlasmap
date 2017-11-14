package io.atlasmap.xml.module;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.atlasmap.v2.ConstantField;
import io.atlasmap.v2.PropertyField;
import io.atlasmap.v2.SimpleField;
import io.atlasmap.xml.v2.XmlField;

import static org.junit.Assert.assertTrue;

public class XmlModuleTest {

    private XmlModule module = null;

    @Before
    public void setUp() throws Exception {
        module = new XmlModule();
    }

    @After
    public void tearDown() throws Exception {
        module = null;
    }

    @Test
    public void testIsSupportedField() {
        assertTrue(module.isSupportedField(new XmlField()));
        assertTrue(module.isSupportedField(new PropertyField()));
        assertTrue(module.isSupportedField(new ConstantField()));
        assertTrue(module.isSupportedField(new SimpleField()));
    }

}
