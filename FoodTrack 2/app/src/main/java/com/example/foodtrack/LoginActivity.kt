package com.example.foodtrack

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodtrack.auth.UserRoomDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val loginButton: MaterialButton = findViewById(R.id.loginButton)
        val registerText: MaterialTextView = findViewById(R.id.registerText)

        loginButton.setOnClickListener {
            attemptLogin()
        }

        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // RegisterActivity'den gelen verileri al
        val emailFromRegister = intent.getStringExtra("email")
        val passwordFromRegister = intent.getStringExtra("password")

        // Eğer veriler mevcutsa, giriş yap
        if (!emailFromRegister.isNullOrEmpty() && !passwordFromRegister.isNullOrEmpty()) {
            emailEditText.setText(emailFromRegister)
            passwordEditText.setText(passwordFromRegister)
            loginUser(emailFromRegister, passwordFromRegister)
        }
    }

    private fun attemptLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showAlertDialog("Missing Fields", "Please enter both email and password.")
        } else {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val db = UserRoomDatabase.getDatabase(this)
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                db.userDao().getUser(email, password)
            }
            if (user != null) {
                showAlertDialog("Login Successful", "You have successfully logged in.")
            } else {
                showAlertDialog("Login Failed", "Incorrect email or password.")
            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                if (title == "Login Successful") {
                    navigateToHome()
                }
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
