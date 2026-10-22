package com.example.widgetcreator

import android.app.Activity
import android.app.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var textInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = (24 * resources.displayMetrics.density).toInt()

        textInput = EditText(this).apply {
            hint = "Type text for your widget"
            setText(
                getSharedPreferences("widget_creator", MODE_PRIVATE)
                    .getString("widget_text", "Hello, Android!")
            )
            textSize = 18f
        }

        val saveButton = Button(this).apply {
            text = "Save and update widgets"
            setOnClickListener {
                saveText()
                TextWidgetProvider.updateAll(this@MainActivity)
                Toast.makeText(
                    this@MainActivity,
                    "Widget text saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val addButton = Button(this).apply {
            text = "Add widget to home screen"
            setOnClickListener { addWidget() }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(padding, padding, padding, padding)
            addView(textInput)
            addView(saveButton)
            addView(addButton)
        })
    }

    private fun saveText() {
        getSharedPreferences("widget_creator", MODE_PRIVATE)
            .edit()
            .putString("widget_text", textInput.text.toString())
            .apply()
    }

    private fun addWidget() {
        saveText()

        val manager = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TextWidgetProvider::class.java)

        if (manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(provider, null, null)
        } else {
            Toast.makeText(
                this,
                "Open your launcher’s widget picker and choose Widget Creator.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
