package it.reply.open.trimoji

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform