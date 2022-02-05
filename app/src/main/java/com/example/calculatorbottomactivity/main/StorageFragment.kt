package com.example.calculatorbottomactivity.main
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.models.Item
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialog_wallpaper.*
import kotlinx.android.synthetic.main.fragment_storage.*
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth


class StorageFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
//    private val user = auth.currentUser
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("images")
    private val imageList: ArrayList<Item> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storage, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email
        val storageRef = storage.getReference("/images").child(email.toString())
        progressBar.visibility = View.VISIBLE
        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: MutableList<StorageReference> =
                result.result!!.items // создание списка всех элементов
            //добавляем цикл для загрузки url картинки в список
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    Log.d("item", "$it")
                    imageList.add(Item(it.toString()))

                }.addOnCompleteListener {
                    recyclerView.adapter = getActivity()?.let { it1 ->
                        ImageAdapter(imageList, it1, this@StorageFragment)
                    }
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    progressBar.visibility = View.GONE
                }
            }
            if (items.isEmpty()) {
                null_alert.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }


    }
    fun deletePhoto(ImageUrl: String){
        val photoRef = FirebaseStorage.getInstance("gs://calculatorbottomactivity.appspot.com").getReferenceFromUrl(ImageUrl)
        photoRef.delete().addOnSuccessListener { // File deleted successfully
            Log.e("firebasestorage", "Изображение удалено")
        }.addOnFailureListener { // Uh-oh, an error occurred!
            Log.e("firebasestorage", "Произошла ошибка")
        }

    }

}
