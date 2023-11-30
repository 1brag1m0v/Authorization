package com.example.authorization

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import java.io.File

class edit_item : AppCompatActivity() {

    private var number: EditText? = null
    private var name: EditText? = null
    private var quantity: EditText? = null
    private var price: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        number = findViewById(R.id.editTextNumber)
        name = findViewById(R.id.editTextName)
        quantity = findViewById(R.id.editTextQuantity)
        price = findViewById(R.id.editTextPrice)

        number?.setText(intent.getStringExtra("numberItem"))
        name?.setText(intent.getStringExtra("nameItem"))
        quantity?.setText(intent.getStringExtra("quantityItem"))
        price?.setText(intent.getStringExtra("priceItem"))

        val btnAdd: Button = findViewById(R.id.editButton)
    }

    fun editItem(view: View) {
        val numberItem2 = number?.text.toString()

    }
}