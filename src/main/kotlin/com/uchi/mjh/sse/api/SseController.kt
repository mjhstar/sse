package com.uchi.mjh.sse.api

import com.uchi.mjh.sse.application.SseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class SseController(
    private val sseService: SseService
) {
    @GetMapping("/subscribe/{userKey}")
    fun subscribe(
        @PathVariable userKey: String
    ): ResponseEntity<SseEmitter> {
        return ResponseEntity.ok(sseService.subscribe(userKey))
    }

    @PostMapping("/send/{userKey}")
    fun sendData(
        @PathVariable userKey: String,
        @RequestParam message: String
    ) {
        sseService.sendMessage(userKey, message)
    }
}
