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
package io.atlasmap.json.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.atlasmap.api.AtlasConversionException;
import io.atlasmap.api.AtlasException;
import io.atlasmap.api.AtlasSession;
import io.atlasmap.api.AtlasValidationException;
import io.atlasmap.core.BaseAtlasModule;
import io.atlasmap.core.PathUtil;
import io.atlasmap.core.PathUtil.SegmentContext;
import io.atlasmap.json.core.JsonFieldReader;
import io.atlasmap.json.core.JsonFieldWriter;
import io.atlasmap.json.v2.AtlasJsonModelFactory;
import io.atlasmap.json.v2.JsonField;
import io.atlasmap.spi.AtlasModuleDetail;
import io.atlasmap.spi.AtlasModuleMode;
import io.atlasmap.v2.AuditStatus;
import io.atlasmap.v2.BaseMapping;
import io.atlasmap.v2.ConstantField;
import io.atlasmap.v2.DataSource;
import io.atlasmap.v2.DataSourceType;
import io.atlasmap.v2.Field;
import io.atlasmap.v2.FieldType;
import io.atlasmap.v2.Mapping;
import io.atlasmap.v2.PropertyField;
import io.atlasmap.v2.SimpleField;
import io.atlasmap.v2.Validation;
import io.atlasmap.v2.Validations;

@AtlasModuleDetail(name = "JsonModule", uri = "atlas:json", modes = { "SOURCE", "TARGET" }, dataFormats = {
        "json" }, configPackages = { "io.atlasmap.json.v2" })
public class JsonModule extends BaseAtlasModule {
    private static final Logger LOG = LoggerFactory.getLogger(JsonModule.class);

    @Override
    public void processPreOutputExecution(AtlasSession session) throws AtlasException {
        JsonFieldWriter writer = new JsonFieldWriter();
        session.setOutput(writer);

        if (LOG.isDebugEnabled()) {
            LOG.debug("processPreOutputExcution completed");
        }
    }

    @Override
    public void processPreValidation(AtlasSession atlasSession) throws AtlasException {
        if (atlasSession == null || atlasSession.getMapping() == null) {
            LOG.error("Invalid session: Session and AtlasMapping must be specified");
            throw new AtlasValidationException("Invalid session");
        }

        Validations validations = atlasSession.getValidations();
        JsonValidationService jsonValidationService = new JsonValidationService(getConversionService());
        List<Validation> jsonValidations = jsonValidationService.validateMapping(atlasSession.getMapping());
        if (jsonValidations != null && !jsonValidations.isEmpty()) {
            validations.getValidation().addAll(jsonValidations);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Detected " + jsonValidations.size() + " json validation notices");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("processPreValidation completed");
        }
    }

    @Override
    public void processInputMapping(AtlasSession session, BaseMapping baseMapping) throws AtlasException {
        Map<String, JsonFieldReader> fieldReaderCache = new HashMap<>();
        for (Mapping mapping : this.generateInputMappings(session, baseMapping)) {
            if (mapping.getInputField() == null || mapping.getInputField().isEmpty()) {
                addAudit(session, null,
                        String.format("Mapping does not contain at least one input field alias=%s desc=%s",
                                mapping.getAlias(), mapping.getDescription()),
                        null, AuditStatus.WARN, null);
                return;
            }

            for (Field field : mapping.getInputField()) {

                if (!isSupportedField(field)) {
                    addAudit(session, field.getDocId(),
                            String.format("Unsupported input field type=%s", field.getClass().getName()), field.getPath(),
                            AuditStatus.ERROR, null);
                    return;
                }

                if (field instanceof ConstantField) {
                    processConstantField(session, mapping);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Processed input constantField sPath=" + field.getPath() + " sV=" + field.getValue()
                                + " sT=" + field.getFieldType() + " docId: " + field.getDocId());
                    }
                    continue;
                }

                if (field instanceof PropertyField) {
                    processPropertyField(session, mapping,
                            session.getAtlasContext().getContextFactory().getPropertyStrategy());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Processed input propertyField sPath=" + field.getPath() + " sV=" + field.getValue()
                                + " sT=" + field.getFieldType() + " docId: " + field.getDocId());
                    }
                    continue;
                }

                JsonField inputField = (JsonField) field;

                JsonFieldReader fieldReader = fieldReaderCache.get(field.getDocId());
                if (fieldReader == null) {
                    Object sourceObject = null;
                    if (field.getDocId() != null && session.hasInput(field.getDocId())) {
                        // Use docId only when it exists, otherwise use default input
                        sourceObject = session.getInput(field.getDocId());
                    } else {
                        sourceObject = session.getInput();
                    }

                    if (sourceObject == null || !(sourceObject instanceof String)) {
                        addAudit(session, field.getDocId(),
                                String.format("Unsupported input object type=%s", field.getClass().getName()), field.getPath(),
                                AuditStatus.ERROR, null);
                        return;
                    }
                    String document = (String) sourceObject;
                    fieldReader = new JsonFieldReader();
                    fieldReader.setDocument(document);
                    fieldReaderCache.put(field.getDocId(), fieldReader);
                }
                fieldReader.read(inputField);

                // NOTE: This shouldn't happen
                if (inputField.getFieldType() == null) {
                    LOG.warn(
                            String.format("FieldType detection was unsuccessful for p=%s falling back to type UNSUPPORTED",
                                    inputField.getPath()));
                    inputField.setFieldType(FieldType.UNSUPPORTED);
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processed input field sPath=" + field.getPath() + " sV=" + field.getValue() + " sT="
                            + field.getFieldType() + " docId: " + field.getDocId());
                }
            }
        }
    }

    @Override
    public void processOutputMapping(AtlasSession session, BaseMapping baseMapping) throws AtlasException {

        JsonFieldWriter writer = null;
        if (session.getOutput() == null) {
            writer = new JsonFieldWriter();
            session.setOutput(writer);
        } else if (session.getOutput() != null && session.getOutput() instanceof JsonFieldWriter) {
            writer = (JsonFieldWriter) session.getOutput();
        } else {
            addAudit(session, null,
                    String.format("Unsupported output object type=%s", session.getOutput().getClass().getName()), null,
                    AuditStatus.ERROR, null);
            return;
        }

        for (Mapping mapping : this.getOutputMappings(session, baseMapping)) {
            if (mapping.getOutputField() == null || mapping.getOutputField().isEmpty()) {
                addAudit(session, null,
                        String.format("Mapping does not contain at least one output field alias=%s desc=%s",
                                mapping.getAlias(), mapping.getDescription()),
                        null, AuditStatus.ERROR, null);
                return;
            }

            Field outputField = mapping.getOutputField().get(0);
            if (!(outputField instanceof JsonField)) {
                addAudit(session, outputField.getDocId(),
                        String.format("Unsupported output field type=%s", outputField.getClass().getName()),
                        outputField.getPath(), AuditStatus.ERROR, null);
                LOG.error(String.format("Unsupported field type %s", outputField.getClass().getName()));
                return;
            }

            switch (mapping.getMappingType()) {
            case MAP:
                Field inField = mapping.getInputField().get(0);
                if (inField.getValue() == null) {
                    continue;
                }

                // Attempt to Auto-detect field type based on input value
                if (outputField.getFieldType() == null && inField.getValue() != null) {
                    outputField.setFieldType(getConversionService().fieldTypeFromClass(inField.getValue().getClass()));
                }

                Object outputValue = null;

                // Do auto-conversion
                if (inField.getFieldType() != null && inField.getFieldType().equals(outputField.getFieldType())) {
                    outputValue = inField.getValue();
                } else {
                    try {
                        outputValue = getConversionService().convertType(inField.getValue(), inField.getFieldType(),
                                outputField.getFieldType());
                    } catch (AtlasConversionException e) {
                        LOG.error(String.format("Unable to auto-convert for iT=%s oT=%s oF=%s msg=%s",
                                inField.getFieldType(), outputField.getFieldType(), outputField.getPath(),
                                e.getMessage()), e);
                        continue;
                    }
                }

                outputField.setValue(outputValue);

                if (outputField.getActions() != null && outputField.getActions().getActions() != null
                        && !outputField.getActions().getActions().isEmpty()) {
                    processFieldActions(session.getAtlasContext().getContextFactory().getFieldActionService(),
                            outputField);
                }

                writer.write((JsonField) outputField);
                break;
            case COMBINE:
                processCombineField(session, mapping, mapping.getInputField(), outputField);
                SimpleField combinedField = new SimpleField();
                combinedField.setFieldType(FieldType.STRING);
                combinedField.setPath(outputField.getPath());
                combinedField.setValue(outputField.getValue());

                if (combinedField.getActions() != null && combinedField.getActions().getActions() != null
                        && !combinedField.getActions().getActions().isEmpty()) {
                    processFieldActions(session.getAtlasContext().getContextFactory().getFieldActionService(),
                            combinedField);
                }

                writer.write(combinedField);
                break;
            case LOOKUP:
                Field inputFieldlkp = mapping.getInputField().get(0);
                if (inputFieldlkp.getValue() != null
                        && inputFieldlkp.getValue().getClass().isAssignableFrom(String.class)) {
                    processLookupField(session, mapping.getLookupTableName(), (String) inputFieldlkp.getValue(),
                            outputField);
                } else {
                    processLookupField(session, mapping.getLookupTableName(), (String) getConversionService()
                            .convertType(inputFieldlkp.getValue(), inputFieldlkp.getFieldType(), FieldType.STRING),
                            outputField);
                }

                if (outputField.getActions() != null && outputField.getActions().getActions() != null
                        && !outputField.getActions().getActions().isEmpty()) {
                    processFieldActions(session.getAtlasContext().getContextFactory().getFieldActionService(),
                            outputField);
                }

                writer.write(outputField);
                break;
            case SEPARATE:
                Field inputFieldsep = mapping.getInputField().get(0);
                for (Field outputFieldsep : mapping.getOutputField()) {
                    Field separateField = processSeparateField(session, mapping, inputFieldsep, outputFieldsep);
                    if (separateField == null) {
                        continue;
                    }

                    outputFieldsep.setValue(separateField.getValue());
                    if (outputFieldsep.getFieldType() == null) {
                        outputFieldsep.setFieldType(separateField.getFieldType());
                    }

                    if (outputFieldsep.getActions() != null && outputFieldsep.getActions().getActions() != null
                            && !outputFieldsep.getActions().getActions().isEmpty()) {
                        processFieldActions(session.getAtlasContext().getContextFactory().getFieldActionService(),
                                outputFieldsep);
                    }
                    writer.write(outputFieldsep);
                }
                break;
            default:
                LOG.error("Unsupported mappingType=%s detected", mapping.getMappingType());
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Processed output field oP=%s oV=%s oT=%s docId: %s", outputField.getPath(),
                        outputField.getValue(), outputField.getFieldType(), outputField.getDocId()));
            }
        }
    }

    @Override
    public void processPostOutputExecution(AtlasSession session) throws AtlasException {

        List<String> docIds = new ArrayList<String>();
        for (DataSource ds : session.getMapping().getDataSource()) {
            if (DataSourceType.TARGET.equals(ds.getDataSourceType()) && ds.getUri().startsWith("atlas:json")) {
                docIds.add(ds.getId());
            }
        }

        // for(String docId : docIds) {
        // Object output = session.getOutput(docId);
        Object output = session.getOutput();
        if (output instanceof JsonFieldWriter) {
            if (((JsonFieldWriter) output).getRootNode() != null) {
                String outputBody = ((JsonFieldWriter) output).getRootNode().toString();
                session.setOutput(outputBody);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("processPostOutputExecution converting JsonNode to string size=%s",
                            outputBody.length()));
                }
            } else {
                LOG.error("Root node for {} is null", output);
            }
        } else {
            LOG.error("DocumentJsonFieldWriter object expected for Json output data source");
        }
        // }

        if (LOG.isDebugEnabled()) {
            LOG.debug("processPostOutputExecution completed");
        }
    }

    @Override
    public List<AtlasModuleMode> listSupportedModes() {
        return Arrays.asList(AtlasModuleMode.SOURCE, AtlasModuleMode.TARGET);
    }

    @Override
    public Boolean isSupportedField(Field field) {
        if (field instanceof JsonField) {
            return true;
        } else if (field instanceof PropertyField) {
            return true;
        } else if (field instanceof ConstantField) {
            return true;
        } else if (field instanceof SimpleField) {
            return true;
        }
        return false;
    }

    @Override
    public int getCollectionSize(AtlasSession session, Field field) throws AtlasException {
        String sourceDocument = null;
        if (field.getDocId() != null && session.hasInput(field.getDocId())) {
            // Use docId only when it exists, otherwise use default input
            sourceDocument = (String) session.getInput(field.getDocId());
        } else {
            sourceDocument = (String) session.getInput();
        }

        // make this a JSON document
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonParser parser = jsonFactory.createParser(sourceDocument);
            JsonNode rootNode = objectMapper.readTree(parser);
            ObjectNode parentNode = (ObjectNode) rootNode;
            String parentSegment = "[root node]";
            for (SegmentContext sc : new PathUtil(field.getPath()).getSegmentContexts(false)) {
                JsonNode currentNode = JsonFieldWriter.getChildNode(parentNode, parentSegment, sc.getSegment());
                if (currentNode == null) {
                    return 0;
                }
                if (PathUtil.isCollectionSegment(sc.getSegment())) {
                    if (currentNode != null && currentNode.isArray()) {
                        return currentNode.size();
                    }
                    return 0;
                }
                parentNode = (ObjectNode) currentNode;
            }
        } catch (IOException e) {
            throw new AtlasException(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public Field cloneField(Field field) throws AtlasException {
        return AtlasJsonModelFactory.cloneField(field);
    }
}
