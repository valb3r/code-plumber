package com.valb3r.codeplumber.restapi.workqueue.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class QueueController {

    @GetMapping("/greeting")
    fun greeting() = "Hello, 1234"
}