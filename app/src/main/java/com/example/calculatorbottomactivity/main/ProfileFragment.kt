package com.example.calculatorbottomactivity.main

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.login.LoginActivity
import com.example.calculatorbottomactivity.models.Data
import com.example.calculatorbottomactivity.models.Item
import com.example.calculatorbottomactivity.signup.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_storage.*
import com.google.firebase.storage.StorageReference




class ProfileFragment  : Fragment() {
    private lateinit var auth: FirebaseAuth
    companion object {
        var currentUser: com.example.calculatorbottomactivity.models.User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sign_out_btn.setOnClickListener { signOut() }
        setProfileAvatar()
        getUserData()
    }
    private fun getUserData() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val ref1 = FirebaseDatabase.getInstance().getReference ("Messages")
        ref.child(uid).get().addOnSuccessListener {
            if (it.exists()) {
                val email =  it.child("username").value.toString()
                user_email.text = "Пользователь: " + email
            }
        }
        ref1.child(uid).get().addOnSuccessListener {
            if (it.exists()) {
                val message =  it.child("message").value.toString()
                note_count.text = "Количество записок: " + message
            }
        }
    }
    private fun setProfileAvatar() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Загрузка информации...")
        progressDialog.show()
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(com.example.calculatorbottomactivity.models.User::class.java)
                val uri = currentUser?.profileImageUrl
                Picasso.get().load(uri).into(avatar1)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
                progressDialog.dismiss()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private fun signOut() {
        auth=FirebaseAuth.getInstance()
        auth.signOut()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun finish() {
        requireActivity().finish()
    }
//    private fun showProgress(){
//        progressBar_profile.indeterminateDrawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.purple_200), android.graphics.PorterDuff.Mode.MULTIPLY)
//        progressBar_profile.visibility=View.VISIBLE
//    }
    }





