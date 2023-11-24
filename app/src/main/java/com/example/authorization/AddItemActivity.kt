package com.example.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddItemActivity : AppCompatActivity() {
    AddItemActivity(ADD_ITEM_REQUEST_CODE: int){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
    }
}