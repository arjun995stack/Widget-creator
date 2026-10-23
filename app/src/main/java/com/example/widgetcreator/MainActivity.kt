package com.example.widgetcreator

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var adminComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adminComponent = ComponentName(this, LockAdminReceiver::class.java)

        val padding = (24 * resources.displayMetrics.density).toInt()

        val message = TextView(this).apply {
            text = "Enable screen-lock permission, then add the lock widget."
            textSize = 18f
        }

        val enableButton = Button(this).apply {
            text = "Enable screen-lock permission"
            setOnClickListener { requestAdminPermission() }
        }

        val addButton = Button(this).apply {
            text = "Add lock widget"
            setOnClickListener { addWidget() }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(padding, padding, padding, padding)
            addView(message)
            addView(enableButton)
            addView(addButton)
        })
    }

    private fun requestAdminPermission() {
        val policyManager = getSystemService(DEVICE_POLICY_SERVICE)
                as DevicePolicyManager

        if (policyManager.isAdminActive(adminComponent)) {
            Toast.makeText(this, "Permission is already enabled", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "This permission lets the lock-screen widget lock your phone when tapped."
            )
        }
        startActivity(intent)
    }

    private fun addWidget() {
        val policyManager = getSystemService(DEVICE_POLICY_SERVICE)
                as DevicePolicyManager

        if (!policyManager.isAdminActive(adminComponent)) {
            Toast.makeText(
                this,
                "Enable screen-lock permission first",
                Toast.LENGTH_LONG
            ).show()
            requestAdminPermission()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val widgetManager = AppWidgetManager.getInstance(this)
            val provider = ComponentName(this, LockWidgetProvider::class.java)

            if (widgetManager.isRequestPinAppWidgetSupported) {
                widgetManager.requestPinAppWidget(provider, null, null)
            } else {
                showWidgetPickerInstructions()
            }
        } else {
            showWidgetPickerInstructions()
        }
    }

    private fun showWidgetPickerInstructions() {
        Toast.makeText(
            this,
            "Long-press your home screen, choose Widgets, then add Lock Phone.",
            Toast.LENGTH_LONG
        ).show()
    }
}
