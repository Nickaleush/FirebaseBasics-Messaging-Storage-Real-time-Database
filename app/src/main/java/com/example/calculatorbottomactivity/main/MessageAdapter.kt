 package com.example.calculatorbottomactivity.main

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.models.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class  MessageAdapter(val mCtx: Context, val layoutResId: Int, val messageList: List<Data>):ArrayAdapter<Data>(mCtx,layoutResId, messageList) {

    private lateinit var auth: FirebaseAuth
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId,null)
        val textViewName =  view.findViewById<TextView>(R.id.textViewMes)
        val textViewUpdate = view.findViewById<TextView>(R.id.TextViewUpdate)
        val message = messageList[position]
        textViewName.text=message.message
        textViewUpdate.setOnClickListener {
            showUpdateDialog(message)
        }
        return view
    }
    fun showUpdateDialog(message: Data) {
        auth = FirebaseAuth.getInstance()
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Изменить заметку?")
        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update_message, null)
        val editText = view.findViewById<EditText>(R.id.editTextName)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        editText.setText(message.message)
        ratingBar.rating=message.rating.toFloat()
        builder.setView(view)
        builder.setPositiveButton("Изменить") { dialog, which ->
            val dbMessage = FirebaseDatabase.getInstance().getReference("Messages")
            val mes =editText.text.toString().trim()
            if(mes.isEmpty()){
                editText.error="Пожалуйста, введите текст"
                editText.requestFocus()
                return@setPositiveButton
            }
            val user = auth.currentUser
                val message = user?.email?.let { Data(message.id, mes, ratingBar.rating.toInt(), it) }
            if (message != null) {
                dbMessage.child(message.id).setValue(message)
            }
            Toast.makeText(mCtx, "Заметка изменена",Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Нет") { dialog, which ->
          dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }
}