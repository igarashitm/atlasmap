package io.atlasmap.json.test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AtlasJsonTestUnrootedMapper extends ObjectMapper {

    /**
     *
     */
    private static final long serialVersionUID = -2292373925488427113L;

    public AtlasJsonTestUnrootedMapper() {
        enable(SerializationFeature.INDENT_OUTPUT);
        configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        setSerializationInclusion(Include.NON_NULL);
    }
}
