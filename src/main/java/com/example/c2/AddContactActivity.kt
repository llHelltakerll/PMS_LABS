package com.example.c2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val imageView: ImageView = findViewById(R.id.input_contact_image)
        val nameInput: EditText = findViewById(R.id.input_contact_name)
        val phoneInput: EditText = findViewById(R.id.input_contact_phone)
        val infoInput: EditText = findViewById(R.id.input_contact_info)
        val saveButton: Button = findViewById(R.id.button_save)
        val cancelButton: Button = findViewById(R.id.button_cancel)

        val userId = intent.getIntExtra("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val info = infoInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя контакта", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageUriString = selectedImageUri?.toString() ?: ""

            val dbHelper = DbHelper(this, null)
            val newContact = Contacts(0, imageUriString, name, phone, info)
            dbHelper.addContact(userId, newContact)

            Toast.makeText(this, "Контакт $name добавлен", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }

        cancelButton.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val belarusRegex = Regex("^\\+375\\s?(\\d{2})\\s?(\\d{3})\\s?(\\d{4})$")
        val russiaRegex = Regex("^\\+7\\s?(\\d{3})\\s?(\\d{3})\\s?(\\d{2})\\s?(\\d{2})$")
        return belarusRegex.matches(phone) || russiaRegex.matches(phone)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            val imageView: ImageView = findViewById(R.id.input_contact_image)
            imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}
