package com.example.c2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userLogin: EditText = findViewById(R.id.user_login)
        val userEmail: EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val userConfirmPass: EditText = findViewById(R.id.user_confirm_pass)
        val button: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val confirmPass = userConfirmPass.text.toString().trim()

            if (validateInputs(login, email, pass, confirmPass)) {
                val user = User(login, email, pass)
                val db = DbHelper(this, null)
                if (db.isLoginTaken(login)) {
                    Toast.makeText(this, "Пользователь с таким логином уже существует", Toast.LENGTH_LONG).show()
                } else {
                    db.addUser(user)
                    Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()

                    clearFields(userLogin, userEmail, userPass, userConfirmPass)

                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        setLongClickListener(userLogin, "Поле логина очищено")
        setLongClickListener(userEmail, "Поле email очищено")
        setLongClickListener(userPass, "Поле пароля очищено")
    }

    private fun validateInputs(login: String, email: String, pass: String, confirmPass: String): Boolean {
        return when {
            login.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_LONG).show()
                false
            }
            pass != confirmPass -> {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun clearFields(vararg fields: EditText) {
        fields.forEach { it.text.clear() }
    }

    private fun setLongClickListener(field: EditText, message: String) {
        field.setOnLongClickListener {
            field.text.clear()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            true
        }
    }
}
