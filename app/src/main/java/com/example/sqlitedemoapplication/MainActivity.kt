package com.example.sqlitedemoapplication

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sqlitedemoapplication.databinding.ActivityMainBinding
import com.example.sqlitedemoapplication.databinding.UpdateDialogBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdd.setOnClickListener {view->
            addRecord(view)
        }

        setUpListofDataIntoRecyclerView()
    }

    private fun addRecord(view: View) {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val databaseHandler = DatabaseHandler(this)
        if (!name.isEmpty() && !email.isEmpty()){
            //id is primary key so system will assign right id itself
            val status = databaseHandler.addEmployee(MyModelClass(0, name, email))
            if (status > -1){
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
                binding.etName.text.clear()
                binding.etEmail.text.clear()

                setUpListofDataIntoRecyclerView()
            }
        } else{
            Toast.makeText(this, "cant be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpListofDataIntoRecyclerView() {
        if (getItemList().size > 0){
            binding.rvList.visibility = View.VISIBLE

            binding.rvList.layoutManager = LinearLayoutManager(this)
            binding.rvList.adapter = ItemAdapter(this, getItemList())
        } else{
            binding.rvList.visibility = View.GONE
        }
    }

    private fun getItemList(): ArrayList<MyModelClass> {
        //creating the instance of DataBaseHandler class
        val databaseHandler = DatabaseHandler(this)
        val myList = databaseHandler.viewEmployee()
        return myList
    }

    fun getEditDialog(item: MyModelClass) {
        val editDialog = Dialog(this)
        val binding2 = UpdateDialogBinding.inflate(layoutInflater)
        editDialog.setContentView(binding2.root)

        editDialog.setCancelable(false)

        binding2.apply {
            etName.setText(item.name)
            etEmail.setText(item.email)
            buttonUpdate.setOnClickListener {
                val newName = etName.text.toString()
                val newEmail = etEmail.text.toString()
                val databaseHandler = DatabaseHandler(this@MainActivity)
                if (newName.isNotEmpty() && newEmail.isNotEmpty()){
                    val status = databaseHandler.updateEmployee(MyModelClass(item.id,newName,newEmail))

                    if (status>-1){
                        Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()

                        setUpListofDataIntoRecyclerView()

                        editDialog.dismiss()
                    }
                } else{
                    Toast.makeText(applicationContext, "cant be empty", Toast.LENGTH_SHORT).show()
                }
            }

            buttonCancel.setOnClickListener {
                editDialog.dismiss()
            }
        }
        editDialog.show()
    }

    fun deleteRecordAlertDialog(item: MyModelClass) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure you want to delete ${item.id}?")
        builder.setPositiveButton("Yes"){dialogInterface, which ->

            val databaseHandler = DatabaseHandler(this)

            val status =  databaseHandler.deleteEmployee(item)
            if (status>-1){
                Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()

                setUpListofDataIntoRecyclerView()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}