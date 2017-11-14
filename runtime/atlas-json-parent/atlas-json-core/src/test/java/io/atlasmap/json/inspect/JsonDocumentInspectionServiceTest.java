package io.atlasmap.json.inspect;

import io.atlasmap.json.v2.JsonComplexType;
import io.atlasmap.json.v2.JsonDocument;
import io.atlasmap.json.v2.JsonField;
import io.atlasmap.v2.CollectionType;
import io.atlasmap.v2.Field;
import io.atlasmap.v2.FieldStatus;
import io.atlasmap.v2.FieldType;
import io.atlasmap.v2.Fields;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 */
public class JsonDocumentInspectionServiceTest {

    private final JsonDocumentInspectionService inspectionService = new JsonDocumentInspectionService();

    @Test(expected = IllegalArgumentException.class)
    public void inspectJsonDocumentEmpty() throws Exception {
        final String instance = "";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inspectJsonDocumentWhitespaceOnly() throws Exception {
        final String instance = " ";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inspectJsonDocumentNull() throws Exception {
        final String instance = null;
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = JsonInspectionException.class)
    public void inspectJsonDocumentUnparseableHighlyComplexNestedObject() throws Exception {
        final String instance = new String(Files
                .readAllBytes(Paths.get("src/test/resources/inspect/unparseable-highly-complex-nested-object.json")));
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = JsonInspectionException.class)
    public void inspectJsonDocumentUnparseableMissingOpenCurly() throws Exception {
        final String instance = "\"ads\":[{\"id_ad\":\"20439\"}, {\"id_ad\":\"20449\"}]}";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = JsonInspectionException.class)
    public void inspectJsonDocumentUnparseableMissingClosingCurly() throws Exception {
        final String instance = "{\"ads\":[{\"id_ad\":\"20439\"}, {\"id_ad\":\"20449\"}]";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = JsonInspectionException.class)
    public void inspectJsonDocumentUnparseableMissingKeySeperator() throws Exception {
        final String instance = "{\"ads\"[{\"id_ad\":\"20439\"}, {\"id_ad\":\"20449\"}]";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test(expected = JsonInspectionException.class)
    public void inspectJsonDocumentUnparseableMissingValueSeperator() throws Exception {
        final String instance = "{\"id_ad\":\"20439\" \"id_ad\":\"20449\"}";
        inspectionService.inspectJsonDocument(instance);
    }

    @Test()
    public void inspectJsonDocumentEmptyDocument() throws Exception {
        final String instance = "{}";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(0));
    }

    @Test
    public void inspectJsonDocumentSimpleArray() throws Exception {
        final String instance = "[ 100, 500, 300, 200, 400 ]";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(1, Is.is(document.getFields().getField().size()));
        JsonField jsonField = (JsonField) document.getFields().getField().get(0);
        Assert.assertTrue(jsonField.getStatus().compareTo(FieldStatus.UNSUPPORTED) == 0);
        Assert.assertTrue(jsonField.getCollectionType().compareTo(CollectionType.ARRAY) == 0);
        Assert.assertNull(jsonField.getFieldType());
        Assert.assertNull(jsonField.getName());
        Assert.assertNull(jsonField.getPath());
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentSimpleArrayStartsWithWhiteSpace() throws Exception {
        final String instance = "\n\t\r [ 100, 500, 300, 200, 400 ]";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(1, Is.is(document.getFields().getField().size()));
        JsonField jsonField = (JsonField) document.getFields().getField().get(0);
        Assert.assertTrue(jsonField.getStatus().compareTo(FieldStatus.UNSUPPORTED) == 0);
        Assert.assertTrue(jsonField.getCollectionType().compareTo(CollectionType.ARRAY) == 0);
        Assert.assertNull(jsonField.getFieldType());
        Assert.assertNull(jsonField.getName());
        Assert.assertNull(jsonField.getPath());
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentSimpleObjectArray() throws Exception {
        final String instance = "[\n" + "\t{\n" + "\t\t\"color\": \"red\",\n" + "\t\t\"value\": \"#f00\"\n" + "\t},\n"
                + "\t{\n" + "\t\t\"color\": \"green\",\n" + "\t\t\"value\": \"#0f0\"\n" + "\t},\n" + "\t{\n"
                + "\t\t\"color\": \"blue\",\n" + "\t\t\"value\": \"#00f\"\n" + "\t}]";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(1, Is.is(document.getFields().getField().size()));
        JsonField jsonField = (JsonField) document.getFields().getField().get(0);
        Assert.assertTrue(jsonField.getStatus().compareTo(FieldStatus.UNSUPPORTED) == 0);
        Assert.assertTrue(jsonField.getCollectionType().compareTo(CollectionType.ARRAY) == 0);
        Assert.assertNull(jsonField.getFieldType());
        Assert.assertNull(jsonField.getName());
        Assert.assertNull(jsonField.getPath());
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentArrayHighlyNestedObjects() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/array-highly-nested-objects.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));
        JsonField jsonField = (JsonField) document.getFields().getField().get(0);
        Assert.assertTrue(jsonField.getStatus().compareTo(FieldStatus.UNSUPPORTED) == 0);
        Assert.assertTrue(jsonField.getCollectionType().compareTo(CollectionType.ARRAY) == 0);
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentEscapedCharsInKeys() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/keys-with-escaped-characters.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(7));
        for (int i = 0; i < document.getFields().getField().size(); i++) {
            JsonField field = (JsonField) document.getFields().getField().get(i);
            if (i == 0) {
                Assert.assertThat(field.getName(), Is.is("'booleanField'"));
                Assert.assertThat(field.getValue(), Is.is(false));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.BOOLEAN));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 1) {
                Assert.assertThat(field.getName(), Is.is("\"charField\""));
                Assert.assertThat(field.getValue(), Is.is("a"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 2) {
                Assert.assertThat(field.getName(), Is.is("\\doubleField"));
                Assert.assertThat(field.getValue(), Is.is(-27152745.3422));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 3) {
                Assert.assertThat(field.getName(), Is.is("floatField\t"));
                Assert.assertThat(field.getValue(), Is.is(-63988281.00));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 4) {
                Assert.assertThat(field.getName(), Is.is("intField\n"));
                Assert.assertThat(field.getValue(), Is.is(8281));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 5) {
                Assert.assertThat(field.getName(), Is.is("shortField"));
                Assert.assertThat(field.getValue(), Is.is(81));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 6) {
                Assert.assertThat(field.getName(), Is.is("longField"));
                Assert.assertThat(field.getValue(), Is.is(3988281));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            }
        }
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentEscapedCharsInValue() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/value-with-escaped-characters.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(5));
        for (int i = 0; i < document.getFields().getField().size(); i++) {
            JsonField field = (JsonField) document.getFields().getField().get(i);
            if (i == 0) {
                Assert.assertThat(field.getName(), Is.is("quote"));
                Assert.assertThat(field.getValue(), Is.is("\"yadda\""));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 1) {
                Assert.assertThat(field.getName(), Is.is("singlequote"));
                Assert.assertThat(field.getValue(), Is.is("'a'"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 2) {
                Assert.assertThat(field.getName(), Is.is("backslash"));
                Assert.assertThat(field.getValue(), Is.is("\\qwerty"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 3) {
                Assert.assertThat(field.getName(), Is.is("tab"));
                Assert.assertThat(field.getValue(), Is.is("foo\t"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 4) {
                Assert.assertThat(field.getName(), Is.is("linefeed"));
                Assert.assertThat(field.getValue(), Is.is("bar\n"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            }
        }
        // printDocument(document);
    }

    // FlatPrimitive
    @Test
    public void inspectFlatPrimitiveNoRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/flatprimitive-base-unrooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(7));
        for (int i = 0; i < document.getFields().getField().size(); i++) {
            JsonField field = (JsonField) document.getFields().getField().get(i);
            if (i == 0) {
                Assert.assertThat(field.getName(), Is.is("booleanField"));
                Assert.assertThat(field.getValue(), Is.is(false));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.BOOLEAN));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 1) {
                Assert.assertThat(field.getName(), Is.is("charField"));
                Assert.assertThat(field.getValue(), Is.is("a"));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                // FIXME DOES NOT RECOGNIZE CHAR DISTINCTLY AND RETURNS THIS AS A STRING
                // (TEXTUAL)
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 2) {
                Assert.assertThat(field.getName(), Is.is("doubleField"));
                Assert.assertThat(field.getValue(), Is.is(-27152745.3422));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 3) {
                Assert.assertThat(field.getName(), Is.is("floatField"));
                Assert.assertThat(field.getValue(), Is.is(-63988281.00));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                // FIXME DOES NOT RECOGNIZE FLOAT DISTINCTLY AND RETURNS THIS AS A DOUBLE
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 4) {
                Assert.assertThat(field.getName(), Is.is("intField"));
                Assert.assertThat(field.getValue(), Is.is(8281));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 5) {
                Assert.assertThat(field.getName(), Is.is("shortField"));
                Assert.assertThat(field.getValue(), Is.is(81));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                // FIXME JSON DOES NOT RECOGNIZE SHORT DISTINCTLY AND RETURNS THIS AS A INTEGER
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 6) {
                Assert.assertThat(field.getName(), Is.is("longField"));
                Assert.assertThat(field.getValue(), Is.is(3988281));
                Assert.assertThat(field.getPath(), Is.is("/".concat(field.getName())));
                // FIXME JSON DOES NOT RECOGNIZE LONG DISTINCTLY AND RETURNS THIS AS A INTEGER
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            }
        }
    }

    @Test
    public void inspectFlatPrimitiveWithRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/flatprimitive-base-rooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));
        JsonComplexType root = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(root);
        Assert.assertThat(root.getName(), Is.is("SourceFlatPrimitive"));
        Assert.assertThat(root.getPath(), Is.is("/SourceFlatPrimitive"));
        Assert.assertThat(root.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(root.getStatus(), Is.is(FieldStatus.SUPPORTED));

        Assert.assertThat(root.getJsonFields().getJsonField().size(), Is.is(8));
        for (int i = 0; i < root.getJsonFields().getJsonField().size(); i++) {
            JsonField field = root.getJsonFields().getJsonField().get(i);
            if (i == 0) {
                Assert.assertThat(field.getName(), Is.is("booleanField"));
                Assert.assertThat(field.getValue(), Is.is(false));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.BOOLEAN));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 1) {
                Assert.assertThat(field.getName(), Is.is("charField"));
                Assert.assertThat(field.getValue(), Is.is("a"));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.STRING));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 2) {
                Assert.assertThat(field.getName(), Is.is("doubleField"));
                Assert.assertThat(field.getValue(), Is.is(-27152745.3422));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 3) {
                Assert.assertThat(field.getName(), Is.is("floatField"));
                Assert.assertThat(field.getValue(), Is.is(-63988281.00));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.DOUBLE));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 4) {
                Assert.assertThat(field.getName(), Is.is("intField"));
                Assert.assertThat(field.getValue(), Is.is(8281));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 5) {
                Assert.assertThat(field.getName(), Is.is("shortField"));
                Assert.assertThat(field.getValue(), Is.is(81));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            } else if (i == 6) {
                Assert.assertThat(field.getName(), Is.is("longField"));
                Assert.assertThat(field.getValue(), Is.is(3988281));
                Assert.assertThat(field.getPath(), Is.is("/SourceFlatPrimitive/".concat(field.getName())));
                Assert.assertThat(field.getFieldType(), Is.is(FieldType.INTEGER));
                Assert.assertThat(field.getStatus(), Is.is(FieldStatus.SUPPORTED));
            }
        }
    }

    @Test
    public void inspectComplexObjectNoRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/complex-object-unrooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(3));

        JsonComplexType address = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(address);
        Assert.assertThat(address.getJsonFields().getJsonField().size(), Is.is(5));

        JsonField address1 = address.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(address1);
        Assert.assertThat(address1.getName(), Is.is("addressLine1"));
        Assert.assertThat(address1.getValue(), Is.is("123 Main St"));
        Assert.assertThat(address1.getPath(), Is.is("/address/addressLine1"));
        Assert.assertThat(address1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField address2 = address.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(address2);
        Assert.assertThat(address2.getName(), Is.is("addressLine2"));
        Assert.assertThat(address2.getValue(), Is.is("Suite 42b"));
        Assert.assertThat(address2.getPath(), Is.is("/address/addressLine2"));
        Assert.assertThat(address2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField city = address.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(city);
        Assert.assertThat(city.getName(), Is.is("city"));
        Assert.assertThat(city.getValue(), Is.is("Anytown"));
        Assert.assertThat(city.getPath(), Is.is("/address/city"));
        Assert.assertThat(city.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(city.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField state = address.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(state);
        Assert.assertThat(state.getName(), Is.is("state"));
        Assert.assertThat(state.getValue(), Is.is("NY"));
        Assert.assertThat(state.getPath(), Is.is("/address/state"));
        Assert.assertThat(state.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(state.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField postalCode = address.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(postalCode);
        Assert.assertThat(postalCode.getName(), Is.is("zipCode"));
        Assert.assertThat(postalCode.getValue(), Is.is("90210"));
        Assert.assertThat(postalCode.getPath(), Is.is("/address/zipCode"));
        Assert.assertThat(postalCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(postalCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType contact = (JsonComplexType) document.getFields().getField().get(1);
        Assert.assertNotNull(contact);
        Assert.assertThat(contact.getJsonFields().getJsonField().size(), Is.is(4));

        JsonField firstName = contact.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(firstName);
        Assert.assertThat(firstName.getName(), Is.is("firstName"));
        Assert.assertThat(firstName.getValue(), Is.is("Ozzie"));
        Assert.assertThat(firstName.getPath(), Is.is("/contact/firstName"));
        Assert.assertThat(firstName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(firstName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField lastName = contact.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(lastName);
        Assert.assertThat(lastName.getName(), Is.is("lastName"));
        Assert.assertThat(lastName.getValue(), Is.is("Smith"));
        Assert.assertThat(lastName.getPath(), Is.is("/contact/lastName"));
        Assert.assertThat(lastName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(lastName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField phoneNumber = contact.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(phoneNumber);
        Assert.assertThat(phoneNumber.getName(), Is.is("phoneNumber"));
        Assert.assertThat(phoneNumber.getValue(), Is.is("5551212"));
        Assert.assertThat(phoneNumber.getPath(), Is.is("/contact/phoneNumber"));
        Assert.assertThat(phoneNumber.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(phoneNumber.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField zipCode = contact.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(zipCode);
        Assert.assertThat(zipCode.getName(), Is.is("zipCode"));
        Assert.assertThat(zipCode.getValue(), Is.is("81111"));
        Assert.assertThat(zipCode.getPath(), Is.is("/contact/zipCode"));
        Assert.assertThat(zipCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(zipCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField orderId = (JsonField) document.getFields().getField().get(2);
        Assert.assertNotNull(orderId);
        Assert.assertThat(orderId.getName(), Is.is("orderId"));
        Assert.assertThat(orderId.getValue(), Is.is(0));
        Assert.assertThat(orderId.getPath(), Is.is("/orderId"));
        Assert.assertThat(orderId.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(orderId.getStatus(), Is.is(FieldStatus.SUPPORTED));
    }

    @Test
    public void inspectComplexObjectWithRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/complex-object-rooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));

        JsonComplexType root = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertThat(root.getJsonFields().getJsonField().size(), Is.is(3));

        JsonComplexType address = (JsonComplexType) root.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(address);
        Assert.assertThat(address.getJsonFields().getJsonField().size(), Is.is(5));

        JsonField address1 = address.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(address1);
        Assert.assertThat(address1.getName(), Is.is("addressLine1"));
        Assert.assertThat(address1.getValue(), Is.is("123 Main St"));
        Assert.assertThat(address1.getPath(), Is.is("/order/address/addressLine1"));
        Assert.assertThat(address1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField address2 = address.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(address2);
        Assert.assertThat(address2.getName(), Is.is("addressLine2"));
        Assert.assertThat(address2.getValue(), Is.is("Suite 42b"));
        Assert.assertThat(address2.getPath(), Is.is("/order/address/addressLine2"));
        Assert.assertThat(address2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField city = address.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(city);
        Assert.assertThat(city.getName(), Is.is("city"));
        Assert.assertThat(city.getValue(), Is.is("Anytown"));
        Assert.assertThat(city.getPath(), Is.is("/order/address/city"));
        Assert.assertThat(city.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(city.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField state = address.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(state);
        Assert.assertThat(state.getName(), Is.is("state"));
        Assert.assertThat(state.getValue(), Is.is("NY"));
        Assert.assertThat(state.getPath(), Is.is("/order/address/state"));
        Assert.assertThat(state.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(state.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField postalCode = address.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(postalCode);
        Assert.assertThat(postalCode.getName(), Is.is("zipCode"));
        Assert.assertThat(postalCode.getValue(), Is.is("90210"));
        Assert.assertThat(postalCode.getPath(), Is.is("/order/address/zipCode"));
        Assert.assertThat(postalCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(postalCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType contact = (JsonComplexType) root.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(contact);
        Assert.assertThat(contact.getJsonFields().getJsonField().size(), Is.is(4));

        JsonField firstName = contact.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(firstName);
        Assert.assertThat(firstName.getName(), Is.is("firstName"));
        Assert.assertThat(firstName.getValue(), Is.is("Ozzie"));
        Assert.assertThat(firstName.getPath(), Is.is("/order/contact/firstName"));
        Assert.assertThat(firstName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(firstName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField lastName = contact.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(lastName);
        Assert.assertThat(lastName.getName(), Is.is("lastName"));
        Assert.assertThat(lastName.getValue(), Is.is("Smith"));
        Assert.assertThat(lastName.getPath(), Is.is("/order/contact/lastName"));
        Assert.assertThat(lastName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(lastName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField phoneNumber = contact.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(phoneNumber);
        Assert.assertThat(phoneNumber.getName(), Is.is("phoneNumber"));
        Assert.assertThat(phoneNumber.getValue(), Is.is("5551212"));
        Assert.assertThat(phoneNumber.getPath(), Is.is("/order/contact/phoneNumber"));
        Assert.assertThat(phoneNumber.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(phoneNumber.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField zipCode = contact.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(zipCode);
        Assert.assertThat(zipCode.getName(), Is.is("zipCode"));
        Assert.assertThat(zipCode.getValue(), Is.is("81111"));
        Assert.assertThat(zipCode.getPath(), Is.is("/order/contact/zipCode"));
        Assert.assertThat(zipCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(zipCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField orderId = root.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(orderId);
        Assert.assertThat(orderId.getName(), Is.is("orderId"));
        Assert.assertThat(orderId.getValue(), Is.is(0));
        Assert.assertThat(orderId.getPath(), Is.is("/order/orderId"));
        Assert.assertThat(orderId.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(orderId.getStatus(), Is.is(FieldStatus.SUPPORTED));
    }

    @Test
    public void inspectRepeatingComplexObjectWithRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/complex-repeated-rooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));
        JsonComplexType root = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(root);
        Assert.assertThat(root.getJsonFields().getJsonField().size(), Is.is(3));
        Assert.assertThat(root.getName(), Is.is("SourceOrderList"));

        JsonComplexType orders = (JsonComplexType) root.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(orders);
        Assert.assertThat(orders.getJsonFields().getJsonField().size(), Is.is(15));
        Assert.assertThat(orders.getName(), Is.is("orders"));

        JsonField orderBatchNumber = root.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(orderBatchNumber);
        Assert.assertThat(orderBatchNumber.getName(), Is.is("orderBatchNumber"));
        Assert.assertThat(orderBatchNumber.getValue(), Is.is(4123562));
        Assert.assertThat(orderBatchNumber.getPath(), Is.is("/SourceOrderList/orderBatchNumber"));
        Assert.assertThat(orderBatchNumber.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(orderBatchNumber.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField numberOrders = root.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(numberOrders);
        Assert.assertThat(numberOrders.getName(), Is.is("numberOrders"));
        Assert.assertThat(numberOrders.getValue(), Is.is(5));
        Assert.assertThat(numberOrders.getPath(), Is.is("/SourceOrderList/numberOrders"));
        Assert.assertThat(numberOrders.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(numberOrders.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType complexAddress11 = (JsonComplexType) orders.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(complexAddress11);
        Assert.assertThat(complexAddress11.getJsonFields().getJsonField().size(), Is.is(5));
        Assert.assertThat(complexAddress11.getName(), Is.is("address"));
        Assert.assertThat(complexAddress11.getPath(), Is.is("/SourceOrderList/orders/address"));

        JsonField address11 = complexAddress11.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(address11);
        Assert.assertThat(address11.getName(), Is.is("addressLine1"));
        Assert.assertThat(address11.getValue(), Is.is("123 Main St"));
        Assert.assertThat(address11.getPath(), Is.is("/SourceOrderList/orders/address/addressLine1"));
        Assert.assertThat(address11.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address11.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField address12 = complexAddress11.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(address12);
        Assert.assertThat(address12.getName(), Is.is("addressLine2"));
        Assert.assertThat(address12.getValue(), Is.is("Suite 42b"));
        Assert.assertThat(address12.getPath(), Is.is("/SourceOrderList/orders/address/addressLine2"));
        Assert.assertThat(address12.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address12.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField city = complexAddress11.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(city);
        Assert.assertThat(city.getName(), Is.is("city"));
        Assert.assertThat(city.getValue(), Is.is("Anytown"));
        Assert.assertThat(city.getPath(), Is.is("/SourceOrderList/orders/address/city"));
        Assert.assertThat(city.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(city.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField state = complexAddress11.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(state);
        Assert.assertThat(state.getName(), Is.is("state"));
        Assert.assertThat(state.getValue(), Is.is("NY"));
        Assert.assertThat(state.getPath(), Is.is("/SourceOrderList/orders/address/state"));
        Assert.assertThat(state.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(state.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField postalCode = complexAddress11.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(postalCode);
        Assert.assertThat(postalCode.getName(), Is.is("zipCode"));
        Assert.assertThat(postalCode.getValue(), Is.is("90210"));
        Assert.assertThat(postalCode.getPath(), Is.is("/SourceOrderList/orders/address/zipCode"));
        Assert.assertThat(postalCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(postalCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType complexContact11 = (JsonComplexType) orders.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(complexContact11);
        Assert.assertThat(complexContact11.getJsonFields().getJsonField().size(), Is.is(4));
        Assert.assertThat(complexContact11.getName(), Is.is("contact"));
        Assert.assertThat(complexContact11.getPath(), Is.is("/SourceOrderList/orders/contact"));

        JsonField firstName = complexContact11.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(firstName);
        Assert.assertThat(firstName.getName(), Is.is("firstName"));
        Assert.assertThat(firstName.getValue(), Is.is("Ozzie"));
        Assert.assertThat(firstName.getPath(), Is.is("/SourceOrderList/orders/contact/firstName"));
        Assert.assertThat(firstName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(firstName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField lastName = complexContact11.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(lastName);
        Assert.assertThat(lastName.getName(), Is.is("lastName"));
        Assert.assertThat(lastName.getValue(), Is.is("Smith"));
        Assert.assertThat(lastName.getPath(), Is.is("/SourceOrderList/orders/contact/lastName"));
        Assert.assertThat(lastName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(lastName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField phoneNumber = complexContact11.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(phoneNumber);
        Assert.assertThat(phoneNumber.getName(), Is.is("phoneNumber"));
        Assert.assertThat(phoneNumber.getValue(), Is.is("5551212"));
        Assert.assertThat(phoneNumber.getPath(), Is.is("/SourceOrderList/orders/contact/phoneNumber"));
        Assert.assertThat(phoneNumber.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(phoneNumber.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField zipCode = complexContact11.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(zipCode);
        Assert.assertThat(zipCode.getName(), Is.is("zipCode"));
        Assert.assertThat(zipCode.getValue(), Is.is("81111"));
        Assert.assertThat(zipCode.getPath(), Is.is("/SourceOrderList/orders/contact/zipCode"));
        Assert.assertThat(zipCode.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(zipCode.getStatus(), Is.is(FieldStatus.SUPPORTED));

        // repeat
        JsonComplexType complexAddress12 = (JsonComplexType) orders.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(complexAddress12);
        Assert.assertThat(complexAddress12.getJsonFields().getJsonField().size(), Is.is(5));
        Assert.assertThat(complexAddress12.getName(), Is.is("address"));
        Assert.assertThat(complexAddress12.getPath(), Is.is("/SourceOrderList/orders[1]/address"));

        JsonField address2 = complexAddress12.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(address2);
        Assert.assertThat(address2.getName(), Is.is("addressLine1"));
        Assert.assertThat(address2.getValue(), Is.is("123 Main St"));
        Assert.assertThat(address2.getPath(), Is.is("/SourceOrderList/orders[1]/address/addressLine1"));
        Assert.assertThat(address2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField address22 = complexAddress12.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(address22);
        Assert.assertThat(address22.getName(), Is.is("addressLine2"));
        Assert.assertThat(address22.getValue(), Is.is("Suite 42b"));
        Assert.assertThat(address22.getPath(), Is.is("/SourceOrderList/orders[1]/address/addressLine2"));
        Assert.assertThat(address22.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(address22.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField city2 = complexAddress12.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(city2);
        Assert.assertThat(city2.getName(), Is.is("city"));
        Assert.assertThat(city2.getValue(), Is.is("Anytown"));
        Assert.assertThat(city2.getPath(), Is.is("/SourceOrderList/orders[1]/address/city"));
        Assert.assertThat(city2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(city2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField state2 = complexAddress12.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(state2);
        Assert.assertThat(state2.getName(), Is.is("state"));
        Assert.assertThat(state2.getValue(), Is.is("NY"));
        Assert.assertThat(state2.getPath(), Is.is("/SourceOrderList/orders[1]/address/state"));
        Assert.assertThat(state2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(state2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField postalCode2 = complexAddress12.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(postalCode2);
        Assert.assertThat(postalCode2.getName(), Is.is("zipCode"));
        Assert.assertThat(postalCode2.getValue(), Is.is("90210"));
        Assert.assertThat(postalCode2.getPath(), Is.is("/SourceOrderList/orders[1]/address/zipCode"));
        Assert.assertThat(postalCode2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(postalCode2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType complexContact12 = (JsonComplexType) orders.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(complexContact12);
        Assert.assertThat(complexContact12.getJsonFields().getJsonField().size(), Is.is(4));
        Assert.assertThat(complexContact12.getName(), Is.is("contact"));
        Assert.assertThat(complexContact12.getPath(), Is.is("/SourceOrderList/orders[1]/contact"));

        JsonField firstName2 = complexContact12.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(firstName2);
        Assert.assertThat(firstName2.getName(), Is.is("firstName"));
        Assert.assertThat(firstName2.getValue(), Is.is("Ozzie"));
        Assert.assertThat(firstName2.getPath(), Is.is("/SourceOrderList/orders[1]/contact/firstName"));
        Assert.assertThat(firstName2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(firstName2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField lastName2 = complexContact12.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(lastName2);
        Assert.assertThat(lastName2.getName(), Is.is("lastName"));
        Assert.assertThat(lastName2.getValue(), Is.is("Smith"));
        Assert.assertThat(lastName2.getPath(), Is.is("/SourceOrderList/orders[1]/contact/lastName"));
        Assert.assertThat(lastName2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(lastName2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField phoneNumber2 = complexContact12.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(phoneNumber2);
        Assert.assertThat(phoneNumber2.getName(), Is.is("phoneNumber"));
        Assert.assertThat(phoneNumber2.getValue(), Is.is("5551212"));
        Assert.assertThat(phoneNumber2.getPath(), Is.is("/SourceOrderList/orders[1]/contact/phoneNumber"));
        Assert.assertThat(phoneNumber2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(phoneNumber2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField zipCode2 = complexContact12.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(zipCode2);
        Assert.assertThat(zipCode2.getName(), Is.is("zipCode"));
        Assert.assertThat(zipCode2.getValue(), Is.is("81111"));
        Assert.assertThat(zipCode2.getPath(), Is.is("/SourceOrderList/orders[1]/contact/zipCode"));
        Assert.assertThat(zipCode2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(zipCode2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        // etc......

        // printDocument(document);
    }

    @Test
    public void inspectISO8601DatesNoRoot() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/iso8601dates-unrooted.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(7));

        JsonField yyyy = (JsonField) document.getFields().getField().get(0);
        Assert.assertNotNull(yyyy);
        Assert.assertThat(yyyy.getName(), Is.is("YYYY"));
        Assert.assertThat(yyyy.getValue(), Is.is("1997"));
        Assert.assertThat(yyyy.getPath(), Is.is("/YYYY"));
        Assert.assertThat(yyyy.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyy.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymm = (JsonField) document.getFields().getField().get(1);
        Assert.assertNotNull(yyyymm);
        Assert.assertThat(yyyymm.getName(), Is.is("YYYY-MM"));
        Assert.assertThat(yyyymm.getValue(), Is.is("1997-07"));
        Assert.assertThat(yyyymm.getPath(), Is.is("/YYYY-MM"));
        Assert.assertThat(yyyymm.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymm.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymmdd = (JsonField) document.getFields().getField().get(2);
        Assert.assertNotNull(yyyymmdd);
        Assert.assertThat(yyyymmdd.getName(), Is.is("YYYY-MM-DD"));
        Assert.assertThat(yyyymmdd.getValue(), Is.is("1997-07-16"));
        Assert.assertThat(yyyymmdd.getPath(), Is.is("/YYYY-MM-DD"));
        Assert.assertThat(yyyymmdd.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymmdd.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymmddthhmmtzd = (JsonField) document.getFields().getField().get(3);
        Assert.assertNotNull(yyyymmddthhmmtzd);
        Assert.assertThat(yyyymmddthhmmtzd.getName(), Is.is("YYYY-MM-DDThh:mmTZD"));
        Assert.assertThat(yyyymmddthhmmtzd.getValue(), Is.is("1997-07-16T19:20+01:00"));
        Assert.assertThat(yyyymmddthhmmtzd.getPath(), Is.is("/YYYY-MM-DDThh:mmTZD"));
        Assert.assertThat(yyyymmddthhmmtzd.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymmddthhmmtzd.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymmddthhmmsstzd = (JsonField) document.getFields().getField().get(4);
        Assert.assertNotNull(yyyymmddthhmmsstzd);
        Assert.assertThat(yyyymmddthhmmsstzd.getName(), Is.is("YYYY-MM-DDThh:mm:ssTZD"));
        Assert.assertThat(yyyymmddthhmmsstzd.getValue(), Is.is("1997-07-16T19:20:30+01:00"));
        Assert.assertThat(yyyymmddthhmmsstzd.getPath(), Is.is("/YYYY-MM-DDThh:mm:ssTZD"));
        Assert.assertThat(yyyymmddthhmmsstzd.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymmddthhmmsstzd.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymmddthhmmssstzd = (JsonField) document.getFields().getField().get(5);
        Assert.assertNotNull(yyyymmddthhmmssstzd);
        Assert.assertThat(yyyymmddthhmmssstzd.getName(), Is.is("YYYY-MM-DDThh:mm:ss.sTZD"));
        Assert.assertThat(yyyymmddthhmmssstzd.getValue(), Is.is("1997-07-16T19:20:30.45+01:00"));
        Assert.assertThat(yyyymmddthhmmssstzd.getPath(), Is.is("/YYYY-MM-DDThh:mm:ss.sTZD"));
        Assert.assertThat(yyyymmddthhmmssstzd.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymmddthhmmssstzd.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField yyyymmddthhmmssutz = (JsonField) document.getFields().getField().get(6);
        Assert.assertNotNull(yyyymmddthhmmssutz);
        Assert.assertThat(yyyymmddthhmmssutz.getName(), Is.is("YYYY-MM-DDThh:mm:ssUTZ"));
        Assert.assertThat(yyyymmddthhmmssutz.getValue(), Is.is("1994-11-05T13:15:30Z"));
        Assert.assertThat(yyyymmddthhmmssutz.getPath(), Is.is("/YYYY-MM-DDThh:mm:ssUTZ"));
        Assert.assertThat(yyyymmddthhmmssutz.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(yyyymmddthhmmssutz.getStatus(), Is.is(FieldStatus.SUPPORTED));

    }

    @Test
    public void inspectJsonDocumentNoRoot() throws Exception {
        final String instance = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(2));

        JsonField field1 = (JsonField) document.getFields().getField().get(0);
        Assert.assertNotNull(field1);
        Assert.assertThat(field1.getName(), Is.is("brand"));
        Assert.assertThat(field1.getValue(), Is.is("Mercedes"));
        Assert.assertThat(field1.getPath(), Is.is("/brand"));
        Assert.assertThat(field1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(field1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField field2 = (JsonField) document.getFields().getField().get(1);
        Assert.assertNotNull(field2);
        Assert.assertThat(field2.getName(), Is.is("doors"));
        Assert.assertThat(field2.getValue(), Is.is(5));
        Assert.assertThat(field2.getPath(), Is.is("/doors"));
        Assert.assertThat(field2.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(field2.getStatus(), Is.is(FieldStatus.SUPPORTED));
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentWithRoot() throws Exception {
        final String instance = "{\"car\" :{ \"brand\" : \"Mercedes\", \"doors\" : 5 } }";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));
        JsonComplexType car = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(car);
        Assert.assertThat(car.getName(), Is.is("car"));
        Assert.assertThat(car.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(car.getPath(), Is.is("/car"));
        Assert.assertThat(car.getStatus(), Is.is(FieldStatus.SUPPORTED));
        Assert.assertThat(car.getJsonFields().getJsonField().size(), Is.is(2));

        JsonField field1 = car.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(field1);
        Assert.assertThat(field1.getName(), Is.is("brand"));
        Assert.assertThat(field1.getValue(), Is.is("Mercedes"));
        Assert.assertThat(field1.getPath(), Is.is("/car/brand"));
        Assert.assertThat(field1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(field1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField field2 = car.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(field2);
        Assert.assertThat(field2.getName(), Is.is("doors"));
        Assert.assertThat(field2.getValue(), Is.is(5));
        Assert.assertThat(field2.getPath(), Is.is("/car/doors"));
        Assert.assertThat(field2.getFieldType(), Is.is(FieldType.INTEGER));
        Assert.assertThat(field2.getStatus(), Is.is(FieldStatus.SUPPORTED));
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentNestedObjectArray() throws Exception {
        final String instance = "{\"menu\": {\n" + "  \"id\": \"file\",\n" + "  \"value\": \"Filed\",\n"
                + "  \"popup\": {\n" + "    \"menuitem\": [\n"
                + "      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n"
                + "      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n"
                + "      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" + "    ]\n" + "  }\n" + "}}";
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(1, Is.is(document.getFields().getField().size()));
        Assert.assertNotNull(document.getFields().getField());

        JsonComplexType jsonComplexType = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(jsonComplexType);
        Assert.assertNotNull(jsonComplexType.getJsonFields().getJsonField());
        Assert.assertThat(jsonComplexType.getJsonFields().getJsonField().size(), Is.is(3));
        Assert.assertThat(jsonComplexType.getName(), Is.is("menu"));

        JsonField jsonField1 = jsonComplexType.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(jsonField1);
        Assert.assertThat(jsonField1.getName(), Is.is("id"));
        Assert.assertThat(jsonField1.getValue(), Is.is("file"));
        Assert.assertThat(jsonField1.getPath(), Is.is("/menu/id"));
        Assert.assertThat(jsonField1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(jsonField1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField jsonField2 = jsonComplexType.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(jsonField2);
        Assert.assertThat(jsonField2.getName(), Is.is("value"));
        Assert.assertThat(jsonField2.getValue(), Is.is("Filed"));
        Assert.assertThat(jsonField2.getPath(), Is.is("/menu/value"));
        Assert.assertThat(jsonField2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(jsonField2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType popup = (JsonComplexType) jsonComplexType.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(popup);
        Assert.assertNotNull(popup.getJsonFields().getJsonField());
        Assert.assertThat(popup.getJsonFields().getJsonField().size(), Is.is(1));
        Assert.assertThat(popup.getName(), Is.is("popup"));
        Assert.assertThat(popup.getPath(), Is.is("/menu/popup"));
        Assert.assertThat(popup.getFieldType(), Is.is(FieldType.COMPLEX));

        JsonComplexType menuitem = (JsonComplexType) popup.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(menuitem);
        Assert.assertNotNull(menuitem.getJsonFields().getJsonField());
        Assert.assertThat(menuitem.getJsonFields().getJsonField().size(), Is.is(6));

        JsonField menuitemValue = menuitem.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(menuitemValue);
        Assert.assertThat(menuitemValue.getName(), Is.is("value"));
        Assert.assertThat(menuitemValue.getValue(), Is.is("New"));
        Assert.assertThat(menuitemValue.getPath(), Is.is("/menu/popup/menuitem/value"));
        Assert.assertThat(menuitemValue.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitemValue.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField menuitemOnclick = menuitem.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(menuitemOnclick);
        Assert.assertThat(menuitemOnclick.getName(), Is.is("onclick"));
        Assert.assertThat(menuitemOnclick.getValue(), Is.is("CreateNewDoc()"));
        Assert.assertThat(menuitemOnclick.getPath(), Is.is("/menu/popup/menuitem/onclick"));
        Assert.assertThat(menuitemOnclick.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitemOnclick.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField menuitem1Value = menuitem.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(menuitem1Value);
        Assert.assertThat(menuitem1Value.getName(), Is.is("value"));
        Assert.assertThat(menuitem1Value.getValue(), Is.is("Open"));
        Assert.assertThat(menuitem1Value.getPath(), Is.is("/menu/popup/menuitem[1]/value"));
        Assert.assertThat(menuitem1Value.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitem1Value.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField menuitem1Onclick = menuitem.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(menuitem1Onclick);
        Assert.assertThat(menuitem1Onclick.getName(), Is.is("onclick"));
        Assert.assertThat(menuitem1Onclick.getValue(), Is.is("OpenDoc()"));
        Assert.assertThat(menuitem1Onclick.getPath(), Is.is("/menu/popup/menuitem[1]/onclick"));
        Assert.assertThat(menuitem1Onclick.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitem1Onclick.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField menuitem2Value = menuitem.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(menuitem2Value);
        Assert.assertThat(menuitem2Value.getName(), Is.is("value"));
        Assert.assertThat(menuitem2Value.getValue(), Is.is("Close"));
        Assert.assertThat(menuitem2Value.getPath(), Is.is("/menu/popup/menuitem[2]/value"));
        Assert.assertThat(menuitem2Value.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitem2Value.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField menuitem2Onclick = menuitem.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(menuitem2Onclick);
        Assert.assertThat(menuitem2Onclick.getName(), Is.is("onclick"));
        Assert.assertThat(menuitem2Onclick.getValue(), Is.is("CloseDoc()"));
        Assert.assertThat(menuitem2Onclick.getPath(), Is.is("/menu/popup/menuitem[2]/onclick"));
        Assert.assertThat(menuitem2Onclick.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(menuitem2Onclick.getStatus(), Is.is(FieldStatus.SUPPORTED));
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentHighlyNestedObject() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/highly-nested-object.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(6));

        JsonField id = (JsonField) document.getFields().getField().get(0);
        Assert.assertNotNull(id);
        Assert.assertThat(id.getName(), Is.is("id"));
        Assert.assertThat(id.getValue(), Is.is("0001"));
        Assert.assertThat(id.getPath(), Is.is("/id"));
        Assert.assertThat(id.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(id.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField value = (JsonField) document.getFields().getField().get(1);
        Assert.assertNotNull(value);
        Assert.assertThat(value.getName(), Is.is("type"));
        Assert.assertThat(value.getValue(), Is.is("donut"));
        Assert.assertThat(value.getPath(), Is.is("/type"));
        Assert.assertThat(value.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(value.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField name = (JsonField) document.getFields().getField().get(2);
        Assert.assertNotNull(name);
        Assert.assertThat(name.getName(), Is.is("name"));
        Assert.assertThat(name.getValue(), Is.is("Cake"));
        Assert.assertThat(name.getPath(), Is.is("/name"));
        Assert.assertThat(name.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(name.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemPPU = (JsonField) document.getFields().getField().get(3);
        Assert.assertNotNull(itemPPU);
        Assert.assertThat(itemPPU.getName(), Is.is("ppu"));
        Assert.assertThat(itemPPU.getPath(), Is.is("/ppu"));
        Assert.assertThat(itemPPU.getValue(), Is.is(0.55));
        Assert.assertThat(itemPPU.getFieldType(), Is.is(FieldType.DOUBLE));
        Assert.assertThat(itemPPU.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType batters = (JsonComplexType) document.getFields().getField().get(4);
        Assert.assertNotNull(batters);
        Assert.assertThat(batters.getJsonFields().getJsonField().size(), Is.is(1));

        JsonComplexType batterParent = (JsonComplexType) batters.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(batterParent);
        Assert.assertThat(batterParent.getJsonFields().getJsonField().size(), Is.is(8));

        JsonField batter0Id = batterParent.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(batter0Id);
        Assert.assertThat(batter0Id.getName(), Is.is("id"));
        Assert.assertThat(batter0Id.getValue(), Is.is("1001"));
        Assert.assertThat(batter0Id.getPath(), Is.is("/batters/batter/id"));
        Assert.assertThat(batter0Id.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter0Id.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter0Type = batterParent.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(batter0Type);
        Assert.assertThat(batter0Type.getName(), Is.is("type"));
        Assert.assertThat(batter0Type.getValue(), Is.is("Regular"));
        Assert.assertThat(batter0Type.getPath(), Is.is("/batters/batter/type"));
        Assert.assertThat(batter0Type.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter0Type.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter1Id = batterParent.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(batter1Id);
        Assert.assertThat(batter1Id.getName(), Is.is("id"));
        Assert.assertThat(batter1Id.getValue(), Is.is("1002"));
        Assert.assertThat(batter1Id.getPath(), Is.is("/batters/batter[1]/id"));
        Assert.assertThat(batter1Id.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter1Id.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter1Type = batterParent.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(batter1Type);
        Assert.assertThat(batter1Type.getName(), Is.is("type"));
        Assert.assertThat(batter1Type.getValue(), Is.is("Chocolate"));
        Assert.assertThat(batter1Type.getPath(), Is.is("/batters/batter[1]/type"));
        Assert.assertThat(batter1Type.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter1Type.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter2Id = batterParent.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(batter2Id);
        Assert.assertThat(batter2Id.getName(), Is.is("id"));
        Assert.assertThat(batter2Id.getValue(), Is.is("1003"));
        Assert.assertThat(batter2Id.getPath(), Is.is("/batters/batter[2]/id"));
        Assert.assertThat(batter2Id.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter2Id.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter2Type = batterParent.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(batter2Type);
        Assert.assertThat(batter2Type.getName(), Is.is("type"));
        Assert.assertThat(batter2Type.getValue(), Is.is("Blueberry"));
        Assert.assertThat(batter2Type.getPath(), Is.is("/batters/batter[2]/type"));
        Assert.assertThat(batter2Type.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter2Type.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter3Id = batterParent.getJsonFields().getJsonField().get(6);
        Assert.assertNotNull(batter3Id);
        Assert.assertThat(batter3Id.getName(), Is.is("id"));
        Assert.assertThat(batter3Id.getValue(), Is.is("1004"));
        Assert.assertThat(batter3Id.getPath(), Is.is("/batters/batter[3]/id"));
        Assert.assertThat(batter3Id.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter3Id.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batter3Type = batterParent.getJsonFields().getJsonField().get(7);
        Assert.assertNotNull(batter3Type);
        Assert.assertThat(batter3Type.getName(), Is.is("type"));
        Assert.assertThat(batter3Type.getValue(), Is.is("Devil's Food"));
        Assert.assertThat(batter3Type.getPath(), Is.is("/batters/batter[3]/type"));
        Assert.assertThat(batter3Type.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batter3Type.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType topping = (JsonComplexType) document.getFields().getField().get(5);
        Assert.assertNotNull(topping);
        Assert.assertThat(topping.getJsonFields().getJsonField().size(), Is.is(14));

        JsonField toppingId0 = topping.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(toppingId0);
        Assert.assertThat(toppingId0.getName(), Is.is("id"));
        Assert.assertThat(toppingId0.getValue(), Is.is("5001"));
        Assert.assertThat(toppingId0.getPath(), Is.is("/topping/id"));
        Assert.assertThat(toppingId0.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingId0.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType0 = topping.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(toppingType0);
        Assert.assertThat(toppingType0.getName(), Is.is("type"));
        Assert.assertThat(toppingType0.getValue(), Is.is("None"));
        Assert.assertThat(toppingType0.getPath(), Is.is("/topping/type"));
        Assert.assertThat(toppingType0.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType0.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingId1 = topping.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(toppingId1);
        Assert.assertThat(toppingId1.getName(), Is.is("id"));
        Assert.assertThat(toppingId1.getValue(), Is.is("5002"));
        Assert.assertThat(toppingId1.getPath(), Is.is("/topping/id[1]"));
        Assert.assertThat(toppingId1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingId1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType1 = topping.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(toppingType1);
        Assert.assertThat(toppingType1.getName(), Is.is("type"));
        Assert.assertThat(toppingType1.getValue(), Is.is("Glazed"));
        Assert.assertThat(toppingType1.getPath(), Is.is("/topping/type[1]"));
        Assert.assertThat(toppingType1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingId2 = topping.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(toppingId2);
        Assert.assertThat(toppingId2.getName(), Is.is("id"));
        Assert.assertThat(toppingId2.getValue(), Is.is("5005"));
        Assert.assertThat(toppingId2.getPath(), Is.is("/topping/id[2]"));
        Assert.assertThat(toppingId2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingId2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType2 = topping.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(toppingType2);
        Assert.assertThat(toppingType2.getName(), Is.is("type"));
        Assert.assertThat(toppingType2.getValue(), Is.is("Sugar"));
        Assert.assertThat(toppingType2.getPath(), Is.is("/topping/type[2]"));
        Assert.assertThat(toppingType2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType2.getStatus(), Is.is(FieldStatus.SUPPORTED));
        // etc....
        // printDocument(document);
    }

    @Test
    public void inspectJsonDocumentHighlyComplexNestedObject() throws Exception {
        final String instance = new String(
                Files.readAllBytes(Paths.get("src/test/resources/inspect/highly-complex-nested-object.json")));
        JsonDocument document = inspectionService.inspectJsonDocument(instance);
        Assert.assertNotNull(document);
        Assert.assertThat(document.getFields().getField().size(), Is.is(1));

        JsonComplexType items = (JsonComplexType) document.getFields().getField().get(0);
        Assert.assertNotNull(items);
        Assert.assertThat(items.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(items.getStatus(), Is.is(FieldStatus.SUPPORTED));
        Assert.assertThat(items.getName(), Is.is("items"));
        Assert.assertThat(items.getJsonFields().getJsonField().size(), Is.is(1));

        JsonComplexType item = (JsonComplexType) items.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(item);
        Assert.assertThat(item.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(item.getJsonFields().getJsonField().size(), Is.is(38));

        JsonField itemId = item.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(itemId);
        Assert.assertThat(itemId.getName(), Is.is("id"));
        Assert.assertThat(itemId.getValue(), Is.is("0001"));
        Assert.assertThat(itemId.getPath(), Is.is("/items/item/id"));
        Assert.assertThat(itemId.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(itemId.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemValue = item.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(itemValue);
        Assert.assertThat(itemValue.getName(), Is.is("type"));
        Assert.assertThat(itemValue.getValue(), Is.is("donut"));
        Assert.assertThat(itemValue.getPath(), Is.is("/items/item/type"));
        Assert.assertThat(itemValue.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(itemValue.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemName = item.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(itemName);
        Assert.assertThat(itemName.getName(), Is.is("name"));
        Assert.assertThat(itemName.getValue(), Is.is("Cake"));
        Assert.assertThat(itemName.getPath(), Is.is("/items/item/name"));
        Assert.assertThat(itemName.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(itemName.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemPPU = item.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(itemPPU);
        Assert.assertThat(itemPPU.getName(), Is.is("ppu"));
        Assert.assertThat(itemPPU.getPath(), Is.is("/items/item/ppu"));
        Assert.assertThat(itemPPU.getValue(), Is.is(0.55));
        Assert.assertThat(itemPPU.getFieldType(), Is.is(FieldType.DOUBLE));
        Assert.assertThat(itemPPU.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType itemBattersComplexType = (JsonComplexType) item.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(itemBattersComplexType);
        Assert.assertThat(itemBattersComplexType.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemBattersComplexType.getName(), Is.is("batters"));
        Assert.assertThat(itemBattersComplexType.getJsonFields().getJsonField().size(), Is.is(1));

        JsonComplexType itemBatterComplexType = (JsonComplexType) itemBattersComplexType.getJsonFields().getJsonField()
                .get(0);
        Assert.assertNotNull(itemBatterComplexType);
        Assert.assertThat(itemBatterComplexType.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemBatterComplexType.getName(), Is.is("batter"));
        Assert.assertThat(itemBatterComplexType.getJsonFields().getJsonField().size(), Is.is(8));

        JsonField batterId = itemBatterComplexType.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(batterId);
        Assert.assertThat(batterId.getName(), Is.is("id"));
        Assert.assertThat(batterId.getValue(), Is.is("1001"));
        Assert.assertThat(batterId.getPath(), Is.is("/items/item/batters/batter/id"));
        Assert.assertThat(batterId.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterId.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterType = itemBatterComplexType.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(batterType);
        Assert.assertThat(batterType.getName(), Is.is("type"));
        Assert.assertThat(batterType.getValue(), Is.is("Regular"));
        Assert.assertThat(batterType.getPath(), Is.is("/items/item/batters/batter/type"));
        Assert.assertThat(batterType.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterType.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterId1 = itemBatterComplexType.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(batterId1);
        Assert.assertThat(batterId1.getName(), Is.is("id"));
        Assert.assertThat(batterId1.getValue(), Is.is("1002"));
        Assert.assertThat(batterId1.getPath(), Is.is("/items/item/batters/batter[1]/id"));
        Assert.assertThat(batterId1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterId1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterType1 = itemBatterComplexType.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(batterType1);
        Assert.assertThat(batterType1.getName(), Is.is("type"));
        Assert.assertThat(batterType1.getValue(), Is.is("Chocolate"));
        Assert.assertThat(batterType1.getPath(), Is.is("/items/item/batters/batter[1]/type"));
        Assert.assertThat(batterType1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterType1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterId2 = itemBatterComplexType.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(batterId2);
        Assert.assertThat(batterId2.getName(), Is.is("id"));
        Assert.assertThat(batterId2.getValue(), Is.is("1003"));
        Assert.assertThat(batterId2.getPath(), Is.is("/items/item/batters/batter[2]/id"));
        Assert.assertThat(batterId2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterId2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterType2 = itemBatterComplexType.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(batterType2);
        Assert.assertThat(batterType2.getName(), Is.is("type"));
        Assert.assertThat(batterType2.getValue(), Is.is("Blueberry"));
        Assert.assertThat(batterType2.getPath(), Is.is("/items/item/batters/batter[2]/type"));
        Assert.assertThat(batterType2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterType2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterId3 = itemBatterComplexType.getJsonFields().getJsonField().get(6);
        Assert.assertNotNull(batterId3);
        Assert.assertThat(batterId3.getName(), Is.is("id"));
        Assert.assertThat(batterId3.getValue(), Is.is("1004"));
        Assert.assertThat(batterId3.getPath(), Is.is("/items/item/batters/batter[3]/id"));
        Assert.assertThat(batterId3.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterId3.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterType3 = itemBatterComplexType.getJsonFields().getJsonField().get(7);
        Assert.assertNotNull(batterType3);
        Assert.assertThat(batterType3.getName(), Is.is("type"));
        Assert.assertThat(batterType3.getValue(), Is.is("Devil's Food"));
        Assert.assertThat(batterType3.getPath(), Is.is("/items/item/batters/batter[3]/type"));
        Assert.assertThat(batterType3.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterType3.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType itemToppingComplexType = (JsonComplexType) item.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(itemToppingComplexType);
        Assert.assertThat(itemToppingComplexType.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemToppingComplexType.getName(), Is.is("topping"));
        Assert.assertThat(itemToppingComplexType.getJsonFields().getJsonField().size(), Is.is(14));

        JsonField toppingID = itemToppingComplexType.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(toppingID);
        Assert.assertThat(toppingID.getName(), Is.is("id"));
        Assert.assertThat(toppingID.getValue(), Is.is("5001"));
        Assert.assertThat(toppingID.getPath(), Is.is("/items/item/topping/id"));
        Assert.assertThat(toppingID.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType = itemToppingComplexType.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(toppingType);
        Assert.assertThat(toppingType.getName(), Is.is("type"));
        Assert.assertThat(toppingType.getValue(), Is.is("None"));
        Assert.assertThat(toppingType.getPath(), Is.is("/items/item/topping/type"));
        Assert.assertThat(toppingType.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID1 = itemToppingComplexType.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(toppingID1);
        Assert.assertThat(toppingID1.getName(), Is.is("id"));
        Assert.assertThat(toppingID1.getValue(), Is.is("5002"));
        Assert.assertThat(toppingID1.getPath(), Is.is("/items/item/topping/id[1]"));
        Assert.assertThat(toppingID1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType1 = itemToppingComplexType.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(toppingType1);
        Assert.assertThat(toppingType1.getName(), Is.is("type"));
        Assert.assertThat(toppingType1.getValue(), Is.is("Glazed"));
        Assert.assertThat(toppingType1.getPath(), Is.is("/items/item/topping/type[1]"));
        Assert.assertThat(toppingType1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID2 = itemToppingComplexType.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(toppingID2);
        Assert.assertThat(toppingID2.getName(), Is.is("id"));
        Assert.assertThat(toppingID2.getValue(), Is.is("5005"));
        Assert.assertThat(toppingID2.getPath(), Is.is("/items/item/topping/id[2]"));
        Assert.assertThat(toppingID2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType2 = itemToppingComplexType.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(toppingType2);
        Assert.assertThat(toppingType2.getName(), Is.is("type"));
        Assert.assertThat(toppingType2.getValue(), Is.is("Sugar"));
        Assert.assertThat(toppingType2.getPath(), Is.is("/items/item/topping/type[2]"));
        Assert.assertThat(toppingType2.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType2.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID3 = itemToppingComplexType.getJsonFields().getJsonField().get(6);
        Assert.assertNotNull(toppingID3);
        Assert.assertThat(toppingID3.getName(), Is.is("id"));
        Assert.assertThat(toppingID3.getValue(), Is.is("5007"));
        Assert.assertThat(toppingID3.getPath(), Is.is("/items/item/topping/id[3]"));
        Assert.assertThat(toppingID3.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID3.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType3 = itemToppingComplexType.getJsonFields().getJsonField().get(7);
        Assert.assertNotNull(toppingType3);
        Assert.assertThat(toppingType3.getName(), Is.is("type"));
        Assert.assertThat(toppingType3.getValue(), Is.is("Powdered Sugar"));
        Assert.assertThat(toppingType3.getPath(), Is.is("/items/item/topping/type[3]"));
        Assert.assertThat(toppingType3.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType3.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID4 = itemToppingComplexType.getJsonFields().getJsonField().get(8);
        Assert.assertNotNull(toppingID4);
        Assert.assertThat(toppingID4.getName(), Is.is("id"));
        Assert.assertThat(toppingID4.getValue(), Is.is("5006"));
        Assert.assertThat(toppingID4.getPath(), Is.is("/items/item/topping/id[4]"));
        Assert.assertThat(toppingID4.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID4.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType4 = itemToppingComplexType.getJsonFields().getJsonField().get(9);
        Assert.assertNotNull(toppingType4);
        Assert.assertThat(toppingType4.getName(), Is.is("type"));
        Assert.assertThat(toppingType4.getValue(), Is.is("Chocolate with Sprinkles"));
        Assert.assertThat(toppingType4.getPath(), Is.is("/items/item/topping/type[4]"));
        Assert.assertThat(toppingType4.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType4.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID5 = itemToppingComplexType.getJsonFields().getJsonField().get(10);
        Assert.assertNotNull(toppingID5);
        Assert.assertThat(toppingID5.getName(), Is.is("id"));
        Assert.assertThat(toppingID5.getValue(), Is.is("5003"));
        Assert.assertThat(toppingID5.getPath(), Is.is("/items/item/topping/id[5]"));
        Assert.assertThat(toppingID5.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID5.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType5 = itemToppingComplexType.getJsonFields().getJsonField().get(11);
        Assert.assertNotNull(toppingType5);
        Assert.assertThat(toppingType5.getName(), Is.is("type"));
        Assert.assertThat(toppingType5.getValue(), Is.is("Chocolate"));
        Assert.assertThat(toppingType5.getPath(), Is.is("/items/item/topping/type[5]"));
        Assert.assertThat(toppingType5.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType5.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID6 = itemToppingComplexType.getJsonFields().getJsonField().get(12);
        Assert.assertNotNull(toppingID6);
        Assert.assertThat(toppingID6.getName(), Is.is("id"));
        Assert.assertThat(toppingID6.getValue(), Is.is("5004"));
        Assert.assertThat(toppingID6.getPath(), Is.is("/items/item/topping/id[6]"));
        Assert.assertThat(toppingID6.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID6.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType6 = itemToppingComplexType.getJsonFields().getJsonField().get(13);
        Assert.assertNotNull(toppingType6);
        Assert.assertThat(toppingType6.getName(), Is.is("type"));
        Assert.assertThat(toppingType6.getValue(), Is.is("Maple"));
        Assert.assertThat(toppingType6.getPath(), Is.is("/items/item/topping/type[6]"));
        Assert.assertThat(toppingType6.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType6.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField item1 = item.getJsonFields().getJsonField().get(6);
        Assert.assertNotNull(item1);
        Assert.assertThat(item1.getName(), Is.is("id"));
        Assert.assertThat(item1.getValue(), Is.is("0002"));
        Assert.assertThat(item1.getPath(), Is.is("/items/item[1]/id"));
        Assert.assertThat(item1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(item1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemValue1 = item.getJsonFields().getJsonField().get(7);
        Assert.assertNotNull(itemValue1);
        Assert.assertThat(itemValue1.getName(), Is.is("type"));
        Assert.assertThat(itemValue1.getValue(), Is.is("donut"));
        Assert.assertThat(itemValue1.getPath(), Is.is("/items/item[1]/type"));
        Assert.assertThat(itemValue1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(itemValue1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemName1 = item.getJsonFields().getJsonField().get(8);
        Assert.assertNotNull(itemName1);
        Assert.assertThat(itemName1.getName(), Is.is("name"));
        Assert.assertThat(itemName1.getValue(), Is.is("Raised"));
        Assert.assertThat(itemName1.getPath(), Is.is("/items/item[1]/name"));
        Assert.assertThat(itemName1.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(itemName1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField itemPPU1 = item.getJsonFields().getJsonField().get(9);
        Assert.assertNotNull(itemPPU1);
        Assert.assertThat(itemPPU1.getName(), Is.is("ppu"));
        Assert.assertThat(itemPPU1.getPath(), Is.is("/items/item[1]/ppu"));
        Assert.assertThat(itemPPU1.getValue(), Is.is(0.55));
        Assert.assertThat(itemPPU1.getFieldType(), Is.is(FieldType.DOUBLE));
        Assert.assertThat(itemPPU1.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType itemBattersComplexType1 = (JsonComplexType) item.getJsonFields().getJsonField().get(10);
        Assert.assertNotNull(itemBattersComplexType1);
        Assert.assertThat(itemBattersComplexType1.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemBattersComplexType1.getName(), Is.is("batters"));
        Assert.assertThat(itemBattersComplexType1.getJsonFields().getJsonField().size(), Is.is(1));

        JsonComplexType itemBatterComplexType1 = (JsonComplexType) itemBattersComplexType1.getJsonFields()
                .getJsonField().get(0);
        Assert.assertNotNull(itemBatterComplexType1);
        Assert.assertThat(itemBatterComplexType1.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemBatterComplexType1.getName(), Is.is("batter"));
        Assert.assertThat(itemBatterComplexType1.getJsonFields().getJsonField().size(), Is.is(2));

        JsonField batterId10 = itemBatterComplexType1.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(batterId10);
        Assert.assertThat(batterId10.getName(), Is.is("id"));
        Assert.assertThat(batterId10.getValue(), Is.is("1001"));
        Assert.assertThat(batterId10.getPath(), Is.is("/items/item[1]/batters/batter/id"));
        Assert.assertThat(batterId10.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterId10.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField batterType10 = itemBatterComplexType1.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(batterType10);
        Assert.assertThat(batterType10.getName(), Is.is("type"));
        Assert.assertThat(batterType10.getValue(), Is.is("Regular"));
        Assert.assertThat(batterType10.getPath(), Is.is("/items/item[1]/batters/batter/type"));
        Assert.assertThat(batterType10.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(batterType10.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonComplexType itemToppingsComplexType1 = (JsonComplexType) item.getJsonFields().getJsonField().get(11);
        Assert.assertNotNull(itemToppingsComplexType1);
        Assert.assertThat(itemToppingsComplexType1.getFieldType(), Is.is(FieldType.COMPLEX));
        Assert.assertThat(itemToppingsComplexType1.getName(), Is.is("topping"));
        Assert.assertThat(itemToppingsComplexType1.getJsonFields().getJsonField().size(), Is.is(10));

        JsonField toppingID11 = itemToppingsComplexType1.getJsonFields().getJsonField().get(0);
        Assert.assertNotNull(toppingID11);
        Assert.assertThat(toppingID11.getName(), Is.is("id"));
        Assert.assertThat(toppingID11.getValue(), Is.is("5001"));
        Assert.assertThat(toppingID11.getPath(), Is.is("/items/item[1]/topping/id"));
        Assert.assertThat(toppingID11.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID11.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType11 = itemToppingsComplexType1.getJsonFields().getJsonField().get(1);
        Assert.assertNotNull(toppingType11);
        Assert.assertThat(toppingType11.getName(), Is.is("type"));
        Assert.assertThat(toppingType11.getValue(), Is.is("None"));
        Assert.assertThat(toppingType11.getPath(), Is.is("/items/item[1]/topping/type"));
        Assert.assertThat(toppingType11.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType11.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID12 = itemToppingsComplexType1.getJsonFields().getJsonField().get(2);
        Assert.assertNotNull(toppingID12);
        Assert.assertThat(toppingID12.getName(), Is.is("id"));
        Assert.assertThat(toppingID12.getValue(), Is.is("5002"));
        Assert.assertThat(toppingID12.getPath(), Is.is("/items/item[1]/topping/id[1]"));
        Assert.assertThat(toppingID12.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID12.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType12 = itemToppingsComplexType1.getJsonFields().getJsonField().get(3);
        Assert.assertNotNull(toppingType12);
        Assert.assertThat(toppingType12.getName(), Is.is("type"));
        Assert.assertThat(toppingType12.getValue(), Is.is("Glazed"));
        Assert.assertThat(toppingType12.getPath(), Is.is("/items/item[1]/topping/type[1]"));
        Assert.assertThat(toppingType12.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType12.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID13 = itemToppingsComplexType1.getJsonFields().getJsonField().get(4);
        Assert.assertNotNull(toppingID13);
        Assert.assertThat(toppingID13.getName(), Is.is("id"));
        Assert.assertThat(toppingID13.getValue(), Is.is("5005"));
        Assert.assertThat(toppingID13.getPath(), Is.is("/items/item[1]/topping/id[2]"));
        Assert.assertThat(toppingID13.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID13.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType13 = itemToppingsComplexType1.getJsonFields().getJsonField().get(5);
        Assert.assertNotNull(toppingType13);
        Assert.assertThat(toppingType13.getName(), Is.is("type"));
        Assert.assertThat(toppingType13.getValue(), Is.is("Sugar"));
        Assert.assertThat(toppingType13.getPath(), Is.is("/items/item[1]/topping/type[2]"));
        Assert.assertThat(toppingType13.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType13.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID14 = itemToppingsComplexType1.getJsonFields().getJsonField().get(6);
        Assert.assertNotNull(toppingID14);
        Assert.assertThat(toppingID14.getName(), Is.is("id"));
        Assert.assertThat(toppingID14.getValue(), Is.is("5003"));
        Assert.assertThat(toppingID14.getPath(), Is.is("/items/item[1]/topping/id[3]"));
        Assert.assertThat(toppingID14.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID14.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType14 = itemToppingsComplexType1.getJsonFields().getJsonField().get(7);
        Assert.assertNotNull(toppingType14);
        Assert.assertThat(toppingType14.getName(), Is.is("type"));
        Assert.assertThat(toppingType14.getValue(), Is.is("Chocolate"));
        Assert.assertThat(toppingType14.getPath(), Is.is("/items/item[1]/topping/type[3]"));
        Assert.assertThat(toppingType14.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType14.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingID15 = itemToppingsComplexType1.getJsonFields().getJsonField().get(8);
        Assert.assertNotNull(toppingID15);
        Assert.assertThat(toppingID15.getName(), Is.is("id"));
        Assert.assertThat(toppingID15.getValue(), Is.is("5004"));
        Assert.assertThat(toppingID15.getPath(), Is.is("/items/item[1]/topping/id[4]"));
        Assert.assertThat(toppingID15.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingID15.getStatus(), Is.is(FieldStatus.SUPPORTED));

        JsonField toppingType15 = itemToppingsComplexType1.getJsonFields().getJsonField().get(9);
        Assert.assertNotNull(toppingType15);
        Assert.assertThat(toppingType15.getName(), Is.is("type"));
        Assert.assertThat(toppingType15.getValue(), Is.is("Maple"));
        Assert.assertThat(toppingType15.getPath(), Is.is("/items/item[1]/topping/type[4]"));
        Assert.assertThat(toppingType15.getFieldType(), Is.is(FieldType.STRING));
        Assert.assertThat(toppingType15.getStatus(), Is.is(FieldStatus.SUPPORTED));

        // etc....
        // printDocument(document);
    }

    @SuppressWarnings("unused")
    private void printDocument(JsonDocument document) {
        Assert.assertNotNull(document.getFields());
        printFields(document.getFields());
    }

    private void printFields(Fields fields) {
        Assert.assertNotNull(fields.getField());
        for (Field field : fields.getField()) {
            if (field instanceof JsonComplexType) {
                printJsonComplexType((JsonComplexType) field);
            } else {
                printJsonField((JsonField) field);
            }
        }
    }

    private void printFields(List<JsonField> jsonField) {
        for (JsonField field : jsonField) {
            if (field instanceof JsonComplexType) {
                printJsonComplexType((JsonComplexType) field);
            } else {
                printJsonField(field);
            }

        }
    }

    private void printJsonComplexType(JsonComplexType field) {
        Assert.assertNotNull(field.getJsonFields());
        printJsonField(field);
        printFields(field.getJsonFields().getJsonField());
    }

    private void printJsonField(JsonField jsonField) {
        System.out.println("Name --> " + jsonField.getName());
        System.out.println("Path --> " + jsonField.getPath());
        System.out.println("Value --> " + jsonField.getValue());
        if (jsonField.getFieldType() != null) {
            System.out.println("Type --> " + jsonField.getFieldType().name());
        }
        if (jsonField.getTypeName() != null) {
            System.out.println("Type Name --> " + jsonField.getTypeName());
        }
        if (jsonField.getCollectionType() != null) {
            System.out.println("Collection Type --> " + jsonField.getCollectionType().name());
        }
        if (jsonField.getStatus() != null) {
            System.out.println("Status  --> " + jsonField.getStatus().name());
        }
        System.out.println();
    }
}
