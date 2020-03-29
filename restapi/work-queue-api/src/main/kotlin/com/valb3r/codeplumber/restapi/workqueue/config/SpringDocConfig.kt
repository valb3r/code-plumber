package com.valb3r.codeplumber.restapi.workqueue.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringDocConfig {

    @Bean
    open fun modelResolver(objectMapper: ObjectMapper?): ModelResolver? {
        return ModelResolver(objectMapper)
    }
}