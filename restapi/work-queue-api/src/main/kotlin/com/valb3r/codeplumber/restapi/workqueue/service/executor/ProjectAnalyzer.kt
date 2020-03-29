package com.valb3r.codeplumber.restapi.workqueue.service.executor

import com.valb3r.codeplumber.restapi.workqueue.dto.ProjectToAnalyze
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

@Service
open class ProjectAnalyzer {

    @Async
    open fun analyze(project: ProjectToAnalyze) : CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            /*AnalysisRunner.runAnalysis(
                    project.pathToDb,
                    Collections.singletonList(project.productJars?.stream()?.map { it.name + '=' + it.jarPaths }?.collect(Collectors.joining(";"))),
                    project.includeClassesFromPackagesRegex,
                    project.excludeClassesFromPackagesRegex,
                    project.includeCallsFromPackagesRegex,
                    project.excludeCallsFromPackagesRegex
            )*/
        }
    }
}