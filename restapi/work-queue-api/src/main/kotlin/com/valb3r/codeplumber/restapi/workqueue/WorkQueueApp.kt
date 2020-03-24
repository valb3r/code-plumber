package com.valb3r.codeplumber.restapi.workqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableNeo4jRepositories
@EnableTransactionManagement
@SpringBootApplication
open class WorkQueueApp

fun main(args: Array<String>) {
  runApplication<WorkQueueApp>(*args)
}