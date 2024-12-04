package com.example.c2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "app", factory, 10) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            execSQL(
                """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT UNIQUE NOT NULL,
                email TEXT NOT NULL,
                pass TEXT NOT NULL
            )
            """.trimIndent()
            )

            execSQL(
                """
            CREATE TABLE contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                info TEXT,
                image TEXT, 
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """.trimIndent()
            )
        }
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.apply {
            execSQL("DROP TABLE IF EXISTS contacts")
            execSQL("DROP TABLE IF EXISTS users")
            onCreate(this)
        }
    }

    fun addUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("login", user.login)
            put("email", user.email)
            put("pass", user.pass)
        }
        return try {
            db.insertOrThrow("users", null, values) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getUser(login: String, pass: String): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM users WHERE login = ? AND pass = ?",
            arrayOf(login, pass)
        )
        return cursor.use {
            if (it.moveToFirst()) it.getInt(it.getColumnIndexOrThrow("id")) else null
        }
    }

    fun isLoginTaken(login: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM users WHERE login = ?", arrayOf(login))
        return cursor.use { it.moveToFirst() }
    }

    fun addContact(userId: Int, contact: Contacts): Boolean {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("name", contact.title)
            put("phone", contact.numb)
            put("info", contact.desc)
            put("image", contact.image)
        }

        val db = writableDatabase
        return try {
            Log.d("DbHelper", "Adding contact with image: ${contact.image}")
            db.insert("contacts", null, values) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun updateContact(contactId: Int, name: String, phone: String, info: String, image: String?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("phone", phone)
            put("info", info)
            if (image != null) put("image", image)
        }
        return try {
            db.update("contacts", values, "id = ?", arrayOf(contactId.toString())) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }



    fun getContacts(userId: Int): ArrayList<Contacts> {
        val db = readableDatabase
        val contacts = ArrayList<Contacts>()

        val cursor = db.rawQuery("SELECT * FROM contacts WHERE user_id = ?", arrayOf(userId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow("id"))
                    val name = it.getString(it.getColumnIndexOrThrow("name"))
                    val phone = it.getString(it.getColumnIndexOrThrow("phone"))
                    val info = it.getString(it.getColumnIndexOrThrow("info"))
                    val image = it.getString(it.getColumnIndexOrThrow("image"))
                    contacts.add(Contacts(id, image ?: "bonichka", name, phone, info))
                } while (it.moveToNext())
            }
        }
        return contacts
    }

    fun deleteContact(contactId: Int): Boolean {
        val db = writableDatabase
        return try {
            db.delete("contacts", "id = ?", arrayOf(contactId.toString())) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
}
