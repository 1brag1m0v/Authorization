package com.example.authorization

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer

class AddItemActivity : AppCompatActivity() {

    private var number: EditText? = null
    private var name: EditText? = null
    private var quantity: EditText? = null
    private var price: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        number = findViewById(R.id.editTextNumber)
        name = findViewById(R.id.editTextName)
        quantity = findViewById(R.id.editTextQuantity)
        price = findViewById(R.id.editTextPrice)

        val btnAdd: Button = findViewById(R.id.buttonAdd)

    }

    fun addItem(view: View) {
        val numberItem = number?.text.toString()
        val nameItem = name?.text.toString()
        val quantityItem = quantity?.text.toString()
        val priceItem = price?.text.toString()
        val imageUrl = "https://i0.wp.com/grabli.ru/wp-content/uploads/2023/04/dsc07456-scaled.jpg?fit=2048%2C1536&ssl=1"

        val file = File(filesDir, "items.json")

        // Чтение содержимого файла JSON с пользователями или создание пустого JSON, если файл не существует
        val jsonString = if (file.exists()) {
            file.bufferedReader().use {
                it.readText()
            }
        } else {
            "{\"items\":[]}"
        }

        // Создание объекта JSON из строки
        val jsonObject = JSONObject(jsonString)
        // Получение массива пользователей из JSON-объекта
        val itemsArray = jsonObject.getJSONArray("items")


        // Создание нового JSON-объекта для нового пользователя
        val newUser = JSONObject().apply {
            put("number", numberItem)
            put("name", nameItem)
            put("quantity", quantityItem)
            put("price", priceItem)
            put("image", imageUrl)
        }

        // Добавление нового пользователя в массив пользователей
        itemsArray.put(newUser)
        // Обновление JSON-объекта с массивом пользователей
        jsonObject.put("items", itemsArray)

        try {
            // Запись JSON-данных в файл
            val fileOutputStream = openFileOutput("items.json", Context.MODE_PRIVATE)
            fileOutputStream.write(jsonObject.toString().toByteArray())
            fileOutputStream.close()
            // Вывод уведомления Toast об успешной регистрации
            Toast.makeText(this, "Item успешно добавлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Обработка ошибок при записи данных в файл
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
        }

        try {
            // Попытка записи JSON-данных в файл с использованием BufferedWriter (не рекомендуется)
            val writer: Writer = BufferedWriter(FileWriter(file))
            writer.write(jsonObject.toString())
            writer.close()
            // Вывод уведомления Toast об успешной регистрации
            Toast.makeText(this, "Item успешно добавлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Обработка ошибок при записи данных в файл
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
        }
        val mainPage = Intent(this, LoginActivity::class.java)
        startActivity(mainPage)
        finish()
    }
}