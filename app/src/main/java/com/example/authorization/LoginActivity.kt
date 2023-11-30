package com.example.authorization

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset


class LoginActivity : AppCompatActivity() {

    private lateinit var itemsListView: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var sortRadioGroup: RadioGroup
    private lateinit var addButton: Button
    private lateinit var backButton: Button
    private lateinit var editButton: Button
    private lateinit var profileButton: Button
    private lateinit var deleteButton: Button
    private var loginFromPage: String? = null
    private lateinit var itemsAdapter: ArrayAdapter<String>

    private var itemsList = mutableListOf<Item>()


    private fun deleteItem (updatedList: List<Item>) {
        for (item in itemsListView) {
            if (item.findViewById<CheckBox>(R.id.checkBox).isChecked) {
                val selectedItem = item.findViewById<TextView>(R.id.itemNumberTextView).text
                itemsListView.removeView(item)
                try {
                    val file = File(filesDir, "items.json")
                    val jsonString = if (file.exists()) {
                        file.bufferedReader().use {
                            it.readText()
                        }
                    } else {
                        "{\"items\":[]}"
                    }

                    val jsonObject = JSONObject(jsonString)

                    val jsonArray = jsonObject.getJSONArray("items")

                    for (i in 0 until jsonArray.length()) {

                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val numberItem = jsonObject.getString("number")
                        val selectNumberItem = "Номер: $numberItem"
                        if (selectNumberItem == selectedItem) {
                            jsonArray.remove(i)
                            break
                        }
                    }
                    jsonObject.put("items", jsonArray)
                    val fileOutputStream = openFileOutput("items.json", Context.MODE_PRIVATE)
                    fileOutputStream.write(jsonObject.toString().toByteArray())
                    fileOutputStream.close()
                    Toast.makeText(this, "$selectedItem успешно удален", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun editItem() {
        for (item in itemsListView) {
            if (item.findViewById<CheckBox>(R.id.checkBox).isChecked) {
                val numberItem = item.findViewById<TextView>(R.id.itemNumberTextView).text
                val nameItem = item.findViewById<TextView>(R.id.itemNameTextView).text
                val quantityItem = item.findViewById<TextView>(R.id.itemQuantityTextView).text
                val priceItem = item.findViewById<TextView>(R.id.itemPriceTextView).text
                val intent = Intent(this, edit_item::class.java).putExtra("numberItem", numberItem).putExtra("nameItem", nameItem).putExtra("quantityItem", quantityItem).putExtra("priceItem", priceItem)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginFromPage = intent.getStringExtra("login")

        itemsListView = findViewById(R.id.itemsListView)
        searchEditText = findViewById(R.id.searchEditText)
        sortRadioGroup = findViewById(R.id.sortRadioGroup)

        addButton = findViewById(R.id.addbtn)
        editButton = findViewById(R.id.editbtn)
        profileButton = findViewById(R.id.profileBtn)
        backButton = findViewById(R.id.backBtn)
        deleteButton = findViewById(R.id.deleteBtn)
        // Load items from JSON
        loadItemsFromJson()

        editButton.setOnClickListener {
            editItem()
        }

        deleteButton.setOnClickListener {
            deleteItem(itemsList)
        }


        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, profile::class.java).putExtra("login", loginFromPage)
            startActivity(intent)
            finish()
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Add text change listener for search
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItems(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
//
//        // Set radio group listener for sorting
        sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAscending -> sortItemsAlphabec(true)
                R.id.radioDescending -> sortItemsAlphabec(false)
            }
        }
    }

    private fun filterItems(filterText: String) {
        val filteredList = itemsList.filter { item ->
            item.name.contains(filterText, ignoreCase = true)
        }
        refreshItems(filteredList)
    }

    private fun sortItemsAlphabec(ascending: Boolean) {
        val sortedList = if (ascending) {
            itemsList.sortedBy { it.name }
        } else {
            itemsList.sortedByDescending { it.name }
        }
        refreshItems(sortedList)
    }

    private fun refreshItems(updatedList: List<Item>) {
        itemsListView.removeAllViews()
        for (item in updatedList) {

            val tile = layoutInflater.inflate(R.layout.list_item_layout, null)
            tile.findViewById<TextView>(R.id.itemNumberTextView).setText("Номер: "+item.number)
            tile.findViewById<TextView>(R.id.itemNameTextView).setText("Наименование: "+item.name)
            tile.findViewById<TextView>(R.id.itemQuantityTextView).setText("Количество: "+item.quantity)
            tile.findViewById<TextView>(R.id.itemPriceTextView).setText("Цена: "+item.price)

            Glide.with(this)
                .load(item.image)
                .into(tile.findViewById<ImageView>(R.id.image))

            itemsListView.addView(tile)
        }
    }

    private fun loadItemsFromJson() {
        try {
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
            // Переменная для отслеживания успешности аутентификации
            var isAuth = false
            // Массив пользователей из JSON-объекта
            val jsonArray = jsonObject.getJSONArray("items")

            for (i in 0 until jsonArray.length()) {

                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val item = Item(
                    jsonObject.getInt("number"),
                    jsonObject.getString("name"),
                    jsonObject.getInt("quantity"),
                    jsonObject.getDouble("price"),
                    jsonObject.getString("image"),
                )
                itemsList.add(item);

                val tile = layoutInflater.inflate(R.layout.list_item_layout, null)
                tile.findViewById<TextView>(R.id.itemNumberTextView).setText("Номер: "+item.number)
                tile.findViewById<TextView>(R.id.itemNameTextView).setText("Наименование: "+item.name)
                tile.findViewById<TextView>(R.id.itemQuantityTextView).setText("Количество: "+item.quantity)
                tile.findViewById<TextView>(R.id.itemPriceTextView).setText("Цена: "+item.price)

                Glide.with(this)
                    .load(item.image)
                    .into(tile.findViewById<ImageView>(R.id.image))

                itemsListView.addView(tile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAsset(filename: String): String {
        var json: String? = null
        try {
            val inputStream: InputStream = assets.open(filename)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

}

