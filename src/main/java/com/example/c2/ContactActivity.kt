package com.example.c2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class ContactActivity : AppCompatActivity() {
    private var isEditing = false
    private var currentImageUri: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val contactImage: ImageView = findViewById(R.id.contact_image)
        val contactName: EditText = findViewById(R.id.contact_name)
        val contactPhone: EditText = findViewById(R.id.contact_phone)
        val contactInfo: EditText = findViewById(R.id.contact_info)
        val buttonEdit: Button = findViewById(R.id.button_edit)
        val buttonDelete: Button = findViewById(R.id.button_delete)

        val contactId = intent.getIntExtra("contactId", -1)
        val imageUri = intent.getStringExtra("contactImage") ?: "content://media/external/images/media/bonichka"
        val name = intent.getStringExtra("contactTitle") ?: "Неизвестный контакт"
        val phone = intent.getStringExtra("contactNumb") ?: "Нет номера"
        val info = intent.getStringExtra("contactDesc") ?: "Нет информации"

        currentImageUri = imageUri

        Glide.with(this)
            .load(Uri.parse(imageUri))
            .error(R.drawable.bonichka)
            .into(contactImage)

        contactName.setText(name)
        contactPhone.setText(phone)
        contactInfo.setText(info)

        contactName.isEnabled = false
        contactPhone.isEnabled = false
        contactInfo.isEnabled = false

        val dbHelper = DbHelper(this, null)

        contactImage.setOnClickListener {
            if (isEditing) {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
        }

        buttonEdit.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                contactName.isEnabled = true
                contactPhone.isEnabled = true
                contactInfo.isEnabled = true
                buttonEdit.text = "Сохранить"
            } else {
                val newName = contactName.text.toString().trim()
                val newPhone = contactPhone.text.toString().trim()
                val newInfo = contactInfo.text.toString().trim()

                if (newName.isEmpty()) {
                    Toast.makeText(this, "Имя контакта не может быть пустым", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!isValidPhone(newPhone)) {
                    Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                isEditing = false
                contactName.isEnabled = false
                contactPhone.isEnabled = false
                contactInfo.isEnabled = false
                buttonEdit.text = "Редактировать контакт"

                if (contactId != -1) {
                    dbHelper.updateContact(contactId, newName, newPhone, newInfo, currentImageUri)
                    Toast.makeText(
                        this,
                        "Контакт обновлён: $newName, $newPhone",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Ошибка: контакт не найден", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonDelete.setOnClickListener {
            if (contactId != -1) {
                dbHelper.deleteContact(contactId)
                Toast.makeText(this, "Контакт ${contactName.text} удалён", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Ошибка: контакт не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                currentImageUri = selectedImageUri.toString()
                Glide.with(this)
                    .load(selectedImageUri)
                    .error(R.drawable.bonichka)
                    .into(findViewById(R.id.contact_image))
                Toast.makeText(this, "Изображение обновлено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val belarusRegex = Regex("^\\+375\\s?(\\d{2})\\s?(\\d{3})\\s?(\\d{4})$")
        val russiaRegex = Regex("^\\+7\\s?(\\d{3})\\s?(\\d{3})\\s?(\\d{2})\\s?(\\d{2})$")
        return belarusRegex.matches(phone) || russiaRegex.matches(phone)
    }
}
