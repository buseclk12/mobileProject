package com.example.foodtrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodtrack.auth.User
import com.example.foodtrack.auth.UserRoomDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            attemptRegistration()
        }
    }

    private fun attemptRegistration() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        when {
            email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else -> {
                lifecycleScope.launch {
                    if (isEmailAlreadyRegistered(email)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Email is already registered", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        registerUser(email, password)
                    }
                }
            }
        }
    }

    private suspend fun isEmailAlreadyRegistered(email: String): Boolean {
        val db = UserRoomDatabase.getDatabase(this)
        val user = withContext(Dispatchers.IO) {
            db.userDao().getUserByEmail(email)
        }
        return user != null
    }

    private fun registerUser(email: String, password: String) {
        val user = User(email = email, password = password)
        val db = UserRoomDatabase.getDatabase(this)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.userDao().insertUser(user)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToLogin(email, password)
            }
        }
    }

    private fun navigateToLogin(email: String, password: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("email", email)
            putExtra("password", password)
        }
        startActivity(intent)
        finish()
    }
}
