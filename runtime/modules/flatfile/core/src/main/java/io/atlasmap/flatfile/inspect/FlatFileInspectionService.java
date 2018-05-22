package io.atlasmap.flatfile.inspect;

import io.atlasmap.flatfile.v2.FlatFileDocument;

public class FlatFileInspectionService {

    public FlatFileDocument inspectFlatFileDocument(String sourceDocument) throws FlatFileInspectionException {
        if (sourceDocument == null || sourceDocument.isEmpty() || (sourceDocument.trim().length() == 0)) {
            throw new IllegalArgumentException("Source document cannot be null, empty or contain only whitespace.");
        }

        return FlatFileInspector.instance().inspect(sourceDocument);
    }

}
