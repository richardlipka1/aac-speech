package com.example.aacspeech

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AddItemActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private var selectedColor: Int = Color.LTGRAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        editText = findViewById(R.id.editText)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        setupColorButtons()

        btnSave.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent()
            resultIntent.putExtra("text", text)
            resultIntent.putExtra("color", selectedColor)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun setupColorButtons() {
        findViewById<Button>(R.id.colorRed).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.red)
        }
        findViewById<Button>(R.id.colorGreen).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.green)
        }
        findViewById<Button>(R.id.colorBlue).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.blue)
        }
        findViewById<Button>(R.id.colorYellow).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.yellow)
        }
        findViewById<Button>(R.id.colorOrange).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.orange)
        }
        findViewById<Button>(R.id.colorPink).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.pink)
        }
        findViewById<Button>(R.id.colorPurple).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.purple_500)
        }
        findViewById<Button>(R.id.colorLightGray).setOnClickListener {
            selectedColor = ContextCompat.getColor(this, R.color.lightgray)
        }
    }
}
