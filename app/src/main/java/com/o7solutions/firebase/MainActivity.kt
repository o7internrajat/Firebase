package com.o7solutions.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.o7solutions.ClickInterface
import com.o7solutions.firebase.model.MenuModel

class MainActivity : AppCompatActivity() {

    val arrayList = ArrayList<MenuModel>()
//    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}