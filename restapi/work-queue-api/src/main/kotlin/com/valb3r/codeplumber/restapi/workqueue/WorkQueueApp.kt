package com.valb3r.codeplumber.restapi.workqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class WorkQueueApp

fun main(args: Array<String>) {
  runApplication<WorkQueueApp>(*args)
}