package com.valb3r.codeplumber.restapi.workqueue.controller

import com.valb3r.codeplumber.restapi.workqueue.dto.ProjectToAnalyze
import com.valb3r.codeplumber.restapi.workqueue.service.executor.ProjectAnalyzer
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class QueueController(val analyzer: ProjectAnalyzer) {

    @PutMapping("/v1/projects")
    fun greeting(@RequestBody project: ProjectToAnalyze) {
        analyzer.analyze(project)
    }
}