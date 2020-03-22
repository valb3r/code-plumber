package com.valb3r.codeplumber.ingestors.javabcel.neo4j

import org.neo4j.graphdb.Label

class CodeLabels {
    enum Labels implements Label {
        App,
        Jar,
        Class,
        Interface,
        Method,
        Field,
        Argument
    }
}
