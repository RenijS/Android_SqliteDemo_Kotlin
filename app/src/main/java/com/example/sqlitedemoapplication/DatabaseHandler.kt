package com.example.sqlitedemoapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context):  SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "EmployeeDatabase"
        private const val TABLE_CONTACTS = "EmployeeTable"

        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        /**actual sql instruction
         * CREATE TABLE EmployeeDatabase(_id INTEGER PRIMARY KEY, name TEXT, email TEXT)
         * here, the table title is EmployeeDatabase with column title: _id, name & email which has its own type
         **/
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    //when we upgrade table for example when we add new column so we need to upgrade to see change
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTACTS)
        onCreate(db)
    }

    //method to insert data
    fun addEmployee(mc: MyModelClass): Long{
        val db = this.writableDatabase

        //its like container
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, mc.name)
        contentValues.put(KEY_EMAIL, mc.email)

        //inserting row
        val success = db.insert(TABLE_CONTACTS,null, contentValues)

        db.close()
        //insert returns long
        return success
    }

    //method to read data
    fun viewEmployee(): ArrayList<MyModelClass>{
        val myList: ArrayList<MyModelClass> = ArrayList()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e:SQLException){
            return ArrayList()
        }

        var id: Int
        var name: String
        var email:String

        //cursor goes through row
        if (cursor.moveToFirst()){
            do {
                //getting data from table
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL))

                //making class from read data of table
                val mc = MyModelClass(id = id,name = name,email= email)
                myList.add(mc)
            } while (cursor.moveToNext())
        }
        return myList
    }

    //function to update record
    fun updateEmployee(mc: MyModelClass): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, mc.name)
        contentValues.put(KEY_EMAIL, mc.email)

        //updating row
        val success = db.update(TABLE_CONTACTS,contentValues, KEY_ID + "=" + mc.id, null )

        db.close()
        return success
    }

    fun deleteEmployee(mc: MyModelClass): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID,mc.id)
        //deleting row
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + mc.id, null)

        db.close()
        return success
    }
}