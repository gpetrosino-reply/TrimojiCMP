package it.reply.open.trimoji.data.model

class ApiException(
    message: String,
    e: Throwable? = null,
): RuntimeException(message, e)