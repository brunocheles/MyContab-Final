package br.com.brunocheles.mycontab.model.items

data class User(
    val userId: String? = "",
    val username: String? = "",
    val email: String? = "",
    val userConfig: String? = "",
    val photoUrl: String? = "",
    val provider: String? = ""
)