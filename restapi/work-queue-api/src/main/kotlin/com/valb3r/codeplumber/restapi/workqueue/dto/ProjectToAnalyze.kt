package com.valb3r.codeplumber.restapi.workqueue.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectToAnalyze(

        var pathToDb: String?,
        var productJars: List<ProductJars>?,

        @Schema(example = ".+", description = "Regex for packages that limits what classes to include")
        var includeClassesFromPackagesRegex: String? = ".+",

        @Schema(example = " ", description = "Regex for packages to exclude in class listing")
        var excludeClassesFromPackagesRegex: String? = "",

        @Schema(example = ".+", description = "Regex for packages that limits what classes to include method call listing")
        var includeCallsFromPackagesRegex: String? = ".+",

        @Schema(example = " ", description = "Regex for packages to exclude in method call listing")
        var excludeCallsFromPackagesRegex: String? = ""
)

data class ProductJars (

        var name: String,
        var jarPaths: Set<String>
)