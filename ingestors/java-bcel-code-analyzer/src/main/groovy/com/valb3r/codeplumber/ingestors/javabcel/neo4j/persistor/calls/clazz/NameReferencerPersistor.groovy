package com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.clazz

import com.google.common.collect.Iterables
import com.valb3r.codeplumber.ingestors.javabcel.analysis.method.finegrained.ClassNameReferenceAnalyzer
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.Constants
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.calls.AbstractInMethodActionPersistor
import com.valb3r.codeplumber.ingestors.javabcel.analysis.ClassRegistry
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.CodeRelationships
import org.neo4j.batchinsert.BatchInserter


class NameReferencerPersistor extends AbstractInMethodActionPersistor<ClassNameReferenceAnalyzer.ClassNameReferenceDto> {

    private final ClassRegistry classRegistry

    NameReferencerPersistor(BatchInserter inserter, ClassRegistry classRegistry) {
        super(inserter)
        this.classRegistry = classRegistry
    }

    @Override
    void persist(long originEntityId, ClassRegistry registry,
                 List<ClassNameReferenceAnalyzer.ClassNameReferenceDto> calls) {

        def resolvedUses = calls.stream().map { classRegistry.get(it.referencedClassName)?.entityId}
                .filter {null != it}
                .collect {it}

        def callsWithCount = resolvedUses.groupBy {it}

        callsWithCount.forEach {id, clzCalls ->
            inserter.createRelationship(
                    originEntityId,
                    Iterables.getFirst(clzCalls, null),
                    CodeRelationships.Relationships.USES_CLASS_NAME,
                    [(Constants.StaticReference.CALL_COUNT): clzCalls.size()]
            )
        }
    }
}
