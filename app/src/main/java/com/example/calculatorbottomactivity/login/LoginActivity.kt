package com.example.calculatorbottomactivity.login
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorbottomactivity.main.MainActivity
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sing_up.*
import kotlinx.android.synthetic.main.activity_sing_up.sign_up_btn
import kotlinx.android.synthetic.main.activity_sing_up.tv_password
import kotlinx.android.synthetic.main.activity_sing_up.tv_username
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.activity_login.*
class LoginActivity:AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        sign_up_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        sign_in_btn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        frgt_pswrd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Забыли пароль?")
            val view = layoutInflater.inflate(R.layout.forgot_password, null)
            val username = view.findViewById<EditText>(R.id.reset_email)
            builder.setView(view)
            builder.setPositiveButton("Сбросить пароль", DialogInterface.OnClickListener { _, _ ->  resetPassword(username)})
            builder.setNegativeButton("Закрыть", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }
        sign_in_btn.setOnClickListener {
            doLogin()
        }
    }
    private fun resetPassword(username:EditText) {
            if (username.text.toString().isEmpty()) {
                return
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
                return
            }
            auth.sendPasswordResetEmail(username.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Reset Email sent", Toast.LENGTH_SHORT).show()
                        }
                    }
    }
    private fun doLogin() {
        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Пожалуйста, попробуйте еще раз!"
            tv_username.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Пожалуйста, попробуйте еще раз!"
            tv_username.requestFocus()
            return
        }
        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Пожалуйста, введите пароль!"
            tv_password.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(baseContext, "Пожалуйста, попробуйте ещё раз!",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                } else {
                Toast.makeText(baseContext, "Пожалуйста, проверьте вашу почту",
                        Toast.LENGTH_SHORT).show()
            }
            } else { }
        }
    }
