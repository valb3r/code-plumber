package com.valb3r.codeplumber.restapi.workqueue.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
open class ObjectMapperConfig : Jackson2ObjectMapperBuilderCustomizer{

    override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder?) {
        jacksonObjectMapperBuilder?.modules(KotlinModule(nullisSameAsDefault = true))
    }
}