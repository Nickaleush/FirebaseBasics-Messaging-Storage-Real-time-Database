package com.example.calculatorbottomactivity.signup

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sing_up.*
import kotlinx.android.synthetic.main.activity_sing_up.select_photo_btn
import kotlinx.android.synthetic.main.activity_sing_up.sign_up_btn
import kotlinx.android.synthetic.main.activity_sing_up.tv_password
import kotlinx.android.synthetic.main.activity_sing_up.tv_username
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
        auth = FirebaseAuth.getInstance()
        sign_up_btn.setOnClickListener {
            signUpUser()
        }
        val actionbar = supportActionBar
        actionbar!!.title="Регистрация"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        select_photo_btn.setOnClickListener {
            MaterialDialog.Builder(this)
                .content("Аватар ставится один раз и навсегда. Пожалуйста, отнеситесь к выбору ответственно!")
                .positiveText("Подтверждаю")
                .onPositive{ materialDialog, dialogAction ->
                    materialDialog.dismiss()
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 0)
                }
                .show()
        }
    }
    private fun signUpUser() {
        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Введите электронную почту"
            tv_username.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Пожалуйста, попробуйте ещё раз"
            tv_username.requestFocus()
            return
        }
        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Пожалуйста, введите пароль"
            tv_password.requestFocus()
            return
        }
        if (tv_repeatpassword.text.toString().isEmpty()) {
            tv_repeatpassword.error = "Повторите пароль"
            tv_repeatpassword.requestFocus()
            return
        }
        if (tv_password.text.toString() == tv_repeatpassword.text.toString()) {
            auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            uploadAvatarToFirebaseStorage()
                            if (user != null) {
                            }
                            user!!.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                    }
                        } else {
                            Toast.makeText(baseContext, "Пароль должен содержать минимум 6 символов",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(baseContext, "Пароли не совпадают,попробуйте еще раз",
                    Toast.LENGTH_SHORT).show()
        }
    }
    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==0 && resultCode == RESULT_OK && data !=null)
        {
            Log.d("LoginActivity", "Фотография выбрана")
            selectedPhotoUri = data.data
            MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageURI(selectedPhotoUri)
            select_photo_btn.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photo_btn.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun uploadAvatarToFirebaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/avatars/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, tv_username.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Пользователь сохранен")
            }
            .addOnFailureListener {
                Log.d(TAG, "Ошибка в сохранении данных: ${it.message}")
            }
    }

}
class User(val uid: String, val username: String, val profileImageUrl: String)