package com.valb3r.codeplumber.ingestors.javabcel.neo4j.indexer


import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.Constants
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.CodeLabels
import org.neo4j.batchinsert.BatchInserter

class Indexer {

    private final BatchInserter batchInserter

    Indexer(BatchInserter batchInserter) {
        this.batchInserter = batchInserter
    }

    void createIndexes() {
        indexClasses()
        indexMethods()
    }

    private void indexClasses() {
        batchInserter.createDeferredSchemaIndex(CodeLabels.Labels.Class).on(Constants.Class.SIMPLE_NAME).create()
        batchInserter.createDeferredSchemaIndex(CodeLabels.Labels.Interface).on(Constants.Class.SIMPLE_NAME).create()
        batchInserter.createDeferredSchemaIndex(CodeLabels.Labels.Field).on(Constants.Field.TYPE).create()
    }

    private void indexMethods() {
        batchInserter.createDeferredSchemaIndex(CodeLabels.Labels.Method).on(Constants.Method.NAME).create()
        batchInserter.createDeferredSchemaIndex(CodeLabels.Labels.Argument).on(Constants.Method.Arg.TYPE).create()
    }
}
