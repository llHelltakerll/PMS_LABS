package com.example.c2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsActivity : AppCompatActivity() {
    private lateinit var contactAdapter: ContactsAdapter
    private lateinit var contacts: ArrayList<Contacts>
    private lateinit var filteredContacts: ArrayList<Contacts>
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contacts)

        userId = intent.getIntExtra("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadContactsFromDatabase()

        filteredContacts = ArrayList(contacts)

        val contactList: RecyclerView = findViewById(R.id.contactsList)
        contactAdapter = ContactsAdapter(filteredContacts, this)
        contactList.layoutManager = LinearLayoutManager(this)
        contactList.adapter = contactAdapter

        val searchField: EditText = findViewById(R.id.searchField)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val addContactButton: ImageButton = findViewById(R.id.addContactButton)
        addContactButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun loadContactsFromDatabase() {
        val dbHelper = DbHelper(this, null)
        contacts = dbHelper.getContacts(userId)

        contacts.sortBy { it.title.lowercase() }

        Log.d("ContactsActivity", "Contacts loaded for user $userId: $contacts")
    }

    private fun filterContacts(query: String) {
        filteredContacts.clear()
        if (query.isEmpty()) {
            filteredContacts.addAll(contacts)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (contact in contacts) {
                if (contact.title.lowercase().contains(lowerCaseQuery) ||
                    contact.numb.contains(lowerCaseQuery)
                ) {
                    filteredContacts.add(contact)
                }
            }
        }
        contacts.sortBy { it.title.lowercase() }
        contactAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadContactsFromDatabase()
        filterContacts("")
        Log.d("ContactsActivity", "Resumed and contacts reloaded.")
    }
}
