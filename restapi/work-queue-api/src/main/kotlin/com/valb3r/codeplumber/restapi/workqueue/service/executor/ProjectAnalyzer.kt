package com.valb3r.codeplumber.restapi.workqueue.service.executor

import com.valb3r.codeplumber.restapi.workqueue.dto.ProjectToAnalyze
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
open class ProjectAnalyzer {

    @Async
    open fun analyze(project: ProjectToAnalyze) : CompletableFuture<Void> {
        return CompletableFuture.runAsync({
        });
    }
}