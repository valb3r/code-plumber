package com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.entity

import com.google.common.collect.ImmutableList
import com.valb3r.codeplumber.ingestors.javabcel.analysis.AppRegistry
import com.valb3r.codeplumber.ingestors.javabcel.analysis.JarRegistry
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.CodeLabels
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.CodeRelationships
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.AbstractPersistor
import com.valb3r.codeplumber.ingestors.javabcel.neo4j.persistor.Constants
import org.neo4j.batchinsert.BatchInserter

class JarPersistor extends AbstractPersistor<String, JarRegistry.JarDto> {

    private final JarRegistry jarRegistry

    JarPersistor(int poolSize, AppRegistry appRegistry, JarRegistry jarRegistry, BatchInserter inserter) {
        super(poolSize, ImmutableList.of(new Persistor("Jar persist", inserter, appRegistry)))
        this.jarRegistry = jarRegistry
    }

    @Override
    Map<String, JarRegistry.JarDto> getObjectsByKey() {
        jarRegistry.getRegistry()
    }

    private static class Persistor extends AbstractPersistor.PersistStage<String, JarRegistry.JarDto, JarRegistry.JarDto> {

        private final AppRegistry appRegistry

        Persistor(String name, BatchInserter batchInserter, AppRegistry appRegistry) {
            super(name, batchInserter)
            this.appRegistry = appRegistry
        }

        @Override
        void doPersist(String objectId, JarRegistry.JarDto original, JarRegistry.JarDto analyzed) {
            def id = inserter.createNode([
                    (Constants.Jar.NAME) : analyzed.name,
                    (Constants.Jar.PATHS): analyzed.paths.toString()
            ], CodeLabels.Labels.Jar)
            analyzed.entityId = id

            analyzed.appNames.forEach {app ->
                def resovledApp = appRegistry.get(app) ?: appRegistry.getUnresolved()
                inserter.createRelationship(resovledApp.entityId, id, CodeRelationships.Relationships.COMPOSED, [:])
            }
        }
    }
}

