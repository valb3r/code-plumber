package com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls


import com.valb3r.codeplumber.ingestors.javabcel.analysis.ClassRegistry
import org.neo4j.batchinsert.BatchInserter

abstract class AbstractInMethodActionPersistor<T> {

    protected final BatchInserter inserter

    AbstractInMethodActionPersistor(BatchInserter inserter) {
        this.inserter = inserter
    }

    abstract void persist(long originEntityId, ClassRegistry registry, List<T> calls)
}
