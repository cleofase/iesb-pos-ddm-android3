package br.com.cleofase.alunoon.entity

data class Chat(
        var uuid: String = "",
        var message: String = "",
        var recipient: String = "",
        var sender: String = "",
        var date: MutableMap<String, Any> = mutableMapOf()
)