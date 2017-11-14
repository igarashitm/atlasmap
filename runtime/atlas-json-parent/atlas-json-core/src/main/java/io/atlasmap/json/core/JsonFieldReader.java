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
package io.atlasmap.json.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.atlasmap.api.AtlasException;
import io.atlasmap.json.v2.JsonField;
import io.atlasmap.v2.CollectionType;
import io.atlasmap.v2.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFieldReader {

    private static final Logger LOG = LoggerFactory.getLogger(JsonFieldReader.class);
    private JsonNode rootNode;

    public void setDocument(String document) throws AtlasException {
        if (document == null || document.isEmpty()) {
            throw new AtlasException(new IllegalArgumentException("document cannot be null nor empty"));
        }

        try {
            JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper();
            JsonParser parser = factory.createParser(document);
            this.rootNode = mapper.readTree(parser);
        } catch (Exception e) {
            throw new AtlasException(e);
        }
    }

    public void read(final JsonField jsonField) throws AtlasException {
        if (rootNode == null) {
            throw new AtlasException("document is not set");
        }
        if (jsonField == null) {
            throw new AtlasException(new IllegalArgumentException("Argument 'jsonField' cannot be null"));
        }

        JsonNode valueNode = null;
        String path = jsonField.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] nodes = path.split("/");
        if (nodes.length >= 1) {
            if (rootNode.size() == 1 && !nodes[0].startsWith(rootNode.fieldNames().next())) {
                // peel off a rooted object
                valueNode = rootNode.elements().next();
            } else {
                valueNode = rootNode;
            }

            // need to walk the path....
            for (String nodeName : nodes) {
                if (valueNode == null) {
                    break;
                }
                valueNode = getValueNode(valueNode, nodeName);
            }
        }
        if (valueNode == null) {
            jsonField.setFieldType(FieldType.NONE);
            return;
        }

        if (valueNode.isTextual()) {
            handleTextualNode(valueNode, jsonField);
        } else if (valueNode.isNumber()) {
            handleNumberNode(valueNode, jsonField);
        } else if (valueNode.isBoolean()) {
            handleBooleanNode(valueNode, jsonField);
        } else if (valueNode.isContainerNode()) {
            handleContainerNode(valueNode, jsonField);
        } else if (valueNode.isNull()) {
            jsonField.setValue(null);
            // we can't detect field type if it's null node
        } else {
            LOG.warn(String.format("Detected unsupported json type for field p=%s docId=%s",
                    jsonField.getPath(), jsonField.getDocId()));
            jsonField.setValue(valueNode.toString());
            jsonField.setFieldType(FieldType.UNSUPPORTED);
        }
    }

    private JsonNode getValueNode(JsonNode parent, String nodeName) {
        String strippedNodeName = nodeName;
        Integer index = null;
        if (nodeName.contains("[")) {
            // are we looking for an array?
            index = Integer.parseInt(nodeName.substring(nodeName.indexOf("[") + 1, nodeName.indexOf("]")));
            strippedNodeName = nodeName.substring(0, nodeName.indexOf("["));
        } else if (nodeName.contains("<")) {
            // maybe a list?
            index = Integer.parseInt(nodeName.substring(nodeName.indexOf("<") + 1, nodeName.indexOf(">")));
            strippedNodeName = nodeName.substring(0, nodeName.indexOf("<"));
        }
        JsonNode answer = parent.get(strippedNodeName);
        if (answer != null && answer.isArray() && index != null) {
            if (index >= 0) {
                answer = answer.get(index);
            } else {
                LOG.warn(String.format("Detected negative index for field p=%s, ignoring...", nodeName));
            }
        }
        return answer;
    }

    private void handleTextualNode(JsonNode valueNode, JsonField jsonField) {
        if (jsonField.getFieldType() == null || FieldType.STRING.equals(jsonField.getFieldType())) {
            jsonField.setValue(valueNode.textValue());
            jsonField.setFieldType(FieldType.STRING);
        } else {
            if (FieldType.CHAR.equals(jsonField.getFieldType())) {
                jsonField.setValue(valueNode.textValue().charAt(0));
            } else {
                LOG.warn(String.format("Unsupported FieldType for text data t=%s p=%s docId=%s",
                        jsonField.getFieldType().value(), jsonField.getPath(), jsonField.getDocId()));
            }
        }
    }

    private void handleNumberNode(JsonNode valueNode, JsonField jsonField) {
        if (valueNode.isInt()) {
            jsonField.setValue(valueNode.intValue());
            jsonField.setFieldType(FieldType.INTEGER);
        } else if (valueNode.isDouble()) {
            jsonField.setValue(valueNode.doubleValue());
            jsonField.setFieldType(FieldType.DOUBLE);
        } else if (valueNode.isBigDecimal()) {
            jsonField.setValue(valueNode.decimalValue());
            jsonField.setFieldType(FieldType.DECIMAL);
        } else if (valueNode.isFloat()) {
            jsonField.setValue(valueNode.floatValue());
            jsonField.setFieldType(FieldType.DOUBLE);
        } else if (valueNode.isLong()) {
            jsonField.setValue(valueNode.longValue());
            jsonField.setFieldType(FieldType.LONG);
        } else if (valueNode.isShort()) {
            jsonField.setValue(valueNode.shortValue());
            jsonField.setFieldType(FieldType.SHORT);
        } else if (valueNode.isBigInteger()) {
            jsonField.setValue(valueNode.bigIntegerValue());
            jsonField.setFieldType(FieldType.NUMBER);
        } else {
            jsonField.setValue(valueNode.numberValue());
            jsonField.setFieldType(FieldType.NUMBER);
        }
    }

    private void handleBooleanNode(JsonNode valueNode, JsonField jsonField) {
        jsonField.setValue(valueNode.booleanValue());
        jsonField.setFieldType(FieldType.BOOLEAN);
    }

    private void handleContainerNode(JsonNode valueNode, JsonField jsonField) {
        if (valueNode.isArray()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Detected json array p=%s docId=%s", jsonField.getPath(),
                        jsonField.getDocId()));
            }
            jsonField.setValue(valueNode.toString());
            jsonField.setFieldType(FieldType.COMPLEX);
            jsonField.setCollectionType(CollectionType.ARRAY);
        } else if (valueNode.isObject()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Detected json complex object p=%s docId=%s",
                        jsonField.getPath(), jsonField.getDocId()));
            }
            jsonField.setValue(valueNode.toString());
            jsonField.setFieldType(FieldType.COMPLEX);
        }
    }

}
