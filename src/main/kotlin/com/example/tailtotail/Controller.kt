package com.example.tailtotail

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class Controller {
    @GetMapping("/hello")
    fun hello(): List<String> {
        return listOf("Hello", "World2")
    }
}