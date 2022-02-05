package com.example.calculatorbottomactivity.main
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.calculatorbottomactivity.*
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.models.Data
import com.example.calculatorbottomactivity.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_storage.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var messageLIstFragment: MessageLIstFragment
    lateinit var loadFilesFragment: LoadFilesFragment
    lateinit var storageFragment: StorageFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var editTextName: EditText
    lateinit var ratingBar: RatingBar
     lateinit var buttonSendData: Button
    lateinit var ref: DatabaseReference
    lateinit var MessageList: MutableList<Data>
    lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentLoadFiles = LoadFilesFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragmentLoadFiles).commit()
        val bottomNavigation: BottomNavigationView = findViewById(R.id.btm_nav)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cloud -> {
                    loadFilesFragment = LoadFilesFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, loadFilesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.files -> {
                    storageFragment = StorageFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, storageFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.messageList -> {
                    messageLIstFragment = MessageLIstFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, messageLIstFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                }
                R.id.profile -> {
                    profileFragment = ProfileFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, profileFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }
     fun saveInfo(){
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("Messages")
        var counter = 0
        MessageList = mutableListOf()
        listView=findViewById(R.id.ListView)
        editTextName = findViewById(R.id.editTextName)
        buttonSendData = findViewById(R.id.Send_data)
        ratingBar = findViewById(R.id.ratingBar)
        val message = editTextName.text.toString().trim()
        if(message.isEmpty()){
            editTextName.error = "Пожалуйста, введите текст!"
            return
        }
        val rateId = ref.push().key
        val rating = rateId?.let { user?.email?.let { it1 -> Data(it, message, ratingBar.rating.toInt(), it1) } }
        rateId?.let {
            ref.child(it).setValue(rating).addOnCompleteListener {
                Toast.makeText(applicationContext, "Заметка успешно сохранена!", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun showMessage(){
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        listView=findViewById(R.id.ListView)
        MessageList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference ("Messages")
        user?.let {val email = user.email}
        val email_user = user?.email
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot!!.exists()){
                    MessageList.clear()
                    for(m in snapshot.children){
                        val message = m.getValue(Data::class.java)
                        val email = m.getValue(Data::class.java)
                        if ( email_user==email?.email) { MessageList.add(message!!)}
                    }
                    val adapter = MessageAdapter(this@MainActivity, R.layout.messages, MessageList)
                    listView.adapter=adapter
                }
            }
        })
    }
}






