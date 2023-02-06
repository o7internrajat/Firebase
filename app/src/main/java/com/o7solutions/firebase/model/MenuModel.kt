package com.o7solutions.firebase.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MenuModel(
    var id: String? = null,
    val name:String? = null,
    val price:String? = null,
//    var stars: MutableMap<String, Boolean> = HashMap()
){

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "price" to price,


        )

}
}