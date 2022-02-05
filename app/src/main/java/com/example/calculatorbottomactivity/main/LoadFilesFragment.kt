package com.example.calculatorbottomactivity.main
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_loadfiles.*
import android.graphics.Bitmap;
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.example.calculatorbottomactivity.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
class LoadFilesFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loadfiles, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }
        uploadImage.setOnClickListener {
            val imgURI = uploadImage.tag as Uri?
            if(imgURI == null){

            }else{
                uploadImage1(requireContext(),imgURI)
            }
        }
    }
    fun uploadImage1(context: Context, imageFileUri: Uri) {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Загрузка...")
        progressDialog.show()
        val storageReference = FirebaseStorage.getInstance().getReference("images/").child(email.toString())
        val imageRef = storageReference.child(UUID.randomUUID().toString())
        imageRef.putFile(imageFileUri).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(activity?.applicationContext,"Загрузка завершена", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(activity?.applicationContext,"Не удалось загрузить файл", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapShot ->
            val progress = 100.0 * taskSnapShot.bytesTransferred/taskSnapShot.totalByteCount
            progressDialog.setMessage("Загружено " + progress.toInt() + "%...")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri: Uri = data?.data!!
            imageView.setImageURI(uri)
            uploadImage.tag = uri
        }
    }
}