package com.valb3r.codeplumber.ingestors.javabcel.neo4j

import org.neo4j.batchinsert.BatchInserter
import org.neo4j.batchinsert.BatchInserters
import org.neo4j.configuration.Config
import org.neo4j.io.layout.DatabaseLayout

class Neo4j {

    private static final CONFIG = [
            "use_memory_mapped_buffers": "true",
            "neostore.nodestore.db.mapped_memory": "250M",
            "neostore.relationshipstore.db.mapped_memory": "1G",
            "neostore.propertystore.db.mapped_memory": "500M",
            "neostore.propertystore.db.strings.mapped_memory": "500M",
            "neostore.propertystore.db.arrays.mapped_memory": "0M",
            "cache_type": "none",
            "dump_config": "true"
    ]

    private final BatchInserter inserter

    Neo4j(File store) {
        this.inserter = BatchInserters.inserter(
                DatabaseLayout.ofFlat(store),
                Config.newBuilder().setRaw(CONFIG).build()
        )
    }

    BatchInserter getInserter() {
        return inserter
    }
}

