package com.example.c2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    // Загрузка нативной библиотеки
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    // Объявление нативного метода
    private external fun clearFieldNative(editText: EditText): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val userLogin: EditText = findViewById(R.id.user_login_auth)
        val userPass: EditText = findViewById(R.id.user_pass_auth)
        val button: Button = findViewById(R.id.button_auth)
        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        // Переход на экран регистрации
        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Авторизация пользователя
        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val db = DbHelper(this, null)
            val userId = db.getUser(login, pass)

            if (userId != null) {
                Toast.makeText(this, "Добро пожаловать, $login!", Toast.LENGTH_LONG).show()

                userLogin.text.clear()
                userPass.text.clear()

                val intent = Intent(this, ContactsActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
            }
        }

        // Очистка поля логина через нативный код
        userLogin.setOnLongClickListener {
            if (clearFieldNative(userLogin)) {
                Toast.makeText(this, "Поле логина очищено (Native)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка очистки поля логина (Native)", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Очистка поля пароля через нативный код
        userPass.setOnLongClickListener {
            if (clearFieldNative(userPass)) {
                Toast.makeText(this, "Поле пароля очищено (Native)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка очистки поля пароля (Native)", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}