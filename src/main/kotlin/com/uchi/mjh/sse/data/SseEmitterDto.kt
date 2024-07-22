package com.uchi.mjh.sse.data

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

class SseEmitterDto(
    val clientId: String,
    val sseEmitter: SseEmitter
) {
}
