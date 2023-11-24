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

// Основной класс активности, расширяющий AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Объявление двух переменных EditText с возможностью принимать значения null для ввода имени пользователя и пароля
    private var logBox: EditText? = null
    private var passBox: EditText? = null

    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка макета активности из XML-файла ресурсов
        setContentView(R.layout.activity_main)

        // Инициализация переменных EditText
        logBox = findViewById(R.id.logbox)
        passBox = findViewById(R.id.passbox)

        // Инициализация кнопок с использованием их идентификаторов из XML-файла ресурсов
        val buttonAuth: Button = findViewById(R.id.authButton)
        val buttonReg: Button = findViewById(R.id.buttonReg)

        // Обработчик нажатия на кнопку регистрации
        buttonReg.setOnClickListener {
            // Создание намерения (Intent) для перехода к другой активности (RegActivity)
            val intent = Intent(this, RegActivity::class.java)
            // Запуск активности по созданному намерению
            startActivity(intent)
            // Завершение текущей активности
            finish()
        }
    }

    // Метод для обработки входа пользователя, вызываемый при нажатии соответствующей кнопки
    fun userLogin(view: View) {
        // Проверка наличия данных в полях ввода
        if(logBox == null || passBox == null){
            // Вывод уведомления Toast о пустых полях
            Toast.makeText(this, "поля пустые",
                Toast.LENGTH_SHORT).show()
            return
        } else {

        }

        // Получение введенных логина и пароля
        val login = logBox?.text.toString()
        val pass = passBox?.text.toString()

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
        // Переменная для отслеживания успешности аутентификации
        var isAuth = false
        // Массив пользователей из JSON-объекта
        val usersArray = jsonObject.getJSONArray("users")

        // Перебор всех пользователей в массиве
        for (i in 0 until usersArray.length()) {
            // Получение JSON-объекта для каждого пользователя
            val userObject = usersArray.getJSONObject(i)
            // Извлечение логина, пароля и ФИО пользователя из JSON-объекта
            val userJSON = userObject.getString("login")
            val passJSON = userObject.getString("pass")
            val fioJSON = userObject.getString("fio")

            // Проверка соответствия введенных данных пользователя данным из файла
            if (userJSON == login && passJSON == pass) {
                // Вывод уведомления Toast об успешной аутентификации
                Toast.makeText(this, "$fioJSON вошел успешно",
                    Toast.LENGTH_SHORT).show()
                // Установка флага успешной аутентификации в true
                isAuth = true
                // Создание намерения для перехода к другой активности (LoginActivity)
                val newPage = Intent(this, LoginActivity::class.java)
                // Запуск активности по созданному намерению
                startActivity(newPage)
                // Прерывание цикла, так как аутентификация успешна
                break
            }
        }

        // Если аутентификация не удалась, вывод уведомления Toast
        if (isAuth == false) {
            Toast.makeText(this, "Скорее всего, данные неверны",
                Toast.LENGTH_SHORT).show()
        }
    }
}