// Пакет, в котором находится текущий файл
package com.example.authorization

// Импорт необходимых классов и пакетов для разработки Android-приложения
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

// Основной класс активности для регистрации нового пользователя, расширяющий AppCompatActivity
class RegActivity : AppCompatActivity() {

    // Объявление переменных EditText для ввода логина, пароля и ФИО нового пользователя
    private var loginBox: EditText? = null
    private var passBox: EditText? = null
    private var fioBox: EditText? = null

    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка макета активности из XML-файла ресурсов
        setContentView(R.layout.activity_reg)

        // Инициализация переменных EditText
        loginBox = findViewById(R.id.logbox_reg)
        passBox = findViewById(R.id.passbox_reg)
        fioBox = findViewById(R.id.fio_reg)

        // Инициализация кнопок с использованием их идентификаторов из XML-файла ресурсов
        val buttonBack: Button = findViewById(R.id.buttonBack)
        val buttonReg: Button = findViewById(R.id.buttonReg)

        // Обработчик нажатия на кнопку "Назад"
        buttonBack.setOnClickListener {
            // Создание намерения (Intent) для перехода к предыдущей активности (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            // Запуск активности по созданному намерению
            startActivity(intent)
            // Завершение текущей активности
            finish()
        }
    }

    // Метод для обработки регистрации нового пользователя, вызываемый при нажатии соответствующей кнопки
    fun register(view: View) {
        // Получение введенных логина, пароля и ФИО
        val login = loginBox?.text.toString()
        val pass = passBox?.text.toString()
        val fio = fioBox?.text.toString()

        // Создание объекта файла для хранения пользовательских данных
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
        // Получение массива пользователей из JSON-объекта
        val usersArray = jsonObject.getJSONArray("users")

        // Проверка наличия пользователя с таким логином
        for (i in 0 until usersArray.length()) {
            val userObject = usersArray.getJSONObject(i)
            val existingLogin = userObject.getString("login")
            if (existingLogin == login) {
                Toast.makeText(this, "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Создание нового JSON-объекта для нового пользователя
        val newUser = JSONObject().apply {
            put("login", login)
            put("pass", pass)
            put("fio", fio)
        }

        // Добавление нового пользователя в массив пользователей
        usersArray.put(newUser)
        // Обновление JSON-объекта с массивом пользователей
        jsonObject.put("users", usersArray)

        try {
            // Запись JSON-данных в файл
            val fileOutputStream = openFileOutput("users.json", Context.MODE_PRIVATE)
            fileOutputStream.write(jsonObject.toString().toByteArray())
            fileOutputStream.close()
            // Вывод уведомления Toast об успешной регистрации
            Toast.makeText(this, "Пользователь $fio успешно зарегистрирован", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Обработка ошибок при записи данных в файл
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при сохранении пользователя", Toast.LENGTH_SHORT).show()
        }

        try {
            // Попытка записи JSON-данных в файл с использованием BufferedWriter (не рекомендуется)
            val writer: Writer = BufferedWriter(FileWriter(file))
            writer.write(jsonObject.toString())
            writer.close()
            // Вывод уведомления Toast об успешной регистрации
            Toast.makeText(this, "Пользователь $fio успешно зарегистрирован", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Обработка ошибок при записи данных в файл
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при сохранении пользователя", Toast.LENGTH_SHORT).show()
        }
    }
}