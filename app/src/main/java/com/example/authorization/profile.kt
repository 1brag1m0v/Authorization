package com.example.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.io.File

class profile : AppCompatActivity() {

    private var itemsList = mutableListOf<profileClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val buttonBack: Button = findViewById(R.id.backBtn)
        val imageProfile: ImageView = findViewById(R.id.imageProfile)
        val fioProfile: TextView = findViewById(R.id.fioProfile)
        val ageProfile: TextView = findViewById(R.id.ageProfile)

        buttonBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        try {
            val file = File(filesDir, "users.json")

            // Чтение содержимого файла JSON с пользователями или создание пустого JSON, если файл не существует
            val jsonString = if (file.exists()) {
                file.bufferedReader().use {
                    it.readText()
                }
            } else {
                "{\"users\":[]}"
            }

            // Создание объекта JSON из строки
            val jsonObject = JSONObject(jsonString)
            // Массив пользователей из JSON-объекта
            val usersArray = jsonObject.getJSONArray("users")
            val login  = intent.getStringExtra("login")

            // Перебор всех пользователей в массиве
            for (i in 0 until usersArray.length()) {
                // Получение JSON-объекта для каждого пользователя
                val userObject = usersArray.getJSONObject(i)
                // Извлечение логина, пароля и ФИО пользователя из JSON-объекта
                val userJSON = userObject.getString("login")
                val passJSON = userObject.getString("pass")
                val fioJSON = userObject.getString("fio")
                val ageJSON = userObject.getString("age")
                if (userJSON == "${intent.getStringExtra("login")}") {
                    imageProfile.setImageResource(R.drawable.cat)
                    fioProfile.text = "ФИО: $fioJSON"
                    ageProfile.text = "Возраст: $ageJSON"
                    break
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }

    }
}