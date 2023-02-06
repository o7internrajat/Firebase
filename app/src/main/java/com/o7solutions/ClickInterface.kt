package com.o7solutions

import com.o7solutions.firebase.model.MenuModel

interface ClickInterface {
    fun editClick(menuModel: MenuModel,position:Int)
    fun deleteClick(menuModel: MenuModel,position: Int)

}