package io.atlasmap.java.inspect;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.atlasmap.core.DefaultAtlasConversionService;
import io.atlasmap.java.v2.JavaClass;

import static org.junit.Assert.assertNotNull;

public class ClassInspectionServiceITest {

    private ClassInspectionService classInspectionService = null;

    @Before
    public void setUp() throws Exception {
        classInspectionService = new ClassInspectionService();
        classInspectionService.setConversionService(DefaultAtlasConversionService.getInstance());
    }

    @After
    public void tearDown() throws Exception {
        classInspectionService = null;
    }

    @Test
    public void testInspectClassClassNameClassPath() throws InspectionException {
        JavaClass javaClazz = classInspectionService.inspectClass("io.atlasmap.java.test.v2.FlatPrimitiveClass",
                "target/reference-jars/atlas-java-test-model-1.16.0-SNAPSHOT.jar");
        assertNotNull(javaClazz);
    }

    @Test
    public void testInspectClassClassNameClassPath45() throws InspectionException {
        JavaClass javaClazz = classInspectionService.inspectClass("io.syndesis.connector.salesforce.Contact",
                "target/reference-jars/salesforce-upsert-contact-connector-0.4.5.jar:target/reference-jars/camel-salesforce-2.19.0.jar");
        assertNotNull(javaClazz);
    }
}
