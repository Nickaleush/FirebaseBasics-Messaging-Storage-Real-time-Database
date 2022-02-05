package com.example.calculatorbottomactivity.main
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatorbottomactivity.R
import com.example.calculatorbottomactivity.models.Item
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
class ImageAdapter (private var items:MutableList<Item>, private val context: Context, val fragment: StorageFragment):
        RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item,
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int // получение числа картинок
    {
       return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Picasso.get().load(item.imageUrl).into(holder.imageView)
        holder.imageView.setOnClickListener {
            setupDialog(item)
        }
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
    private fun setupDialog(item: Item) {
        val dialog = Dialog(context, R.style.DialogStyle)
        dialog.setContentView(R.layout.dialog_wallpaper)
        val dialogImageView = dialog.findViewById<ImageView>(R.id.dialogImageView)
        val setWallpaperBtn = dialog.findViewById<Button>(R.id.setWallpaperBtn)
        val deletePhotoBtn = dialog.findViewById<Button>(R.id.delete_btn)
        val dialogProgressBar = dialog.findViewById<ProgressBar>(R.id.dialogProgressBar)
        dialogImageView.visibility = View.VISIBLE
        dialogProgressBar.visibility = View.VISIBLE
        setWallpaperBtn.visibility = View.GONE
        //load image into Picasso
        Picasso.get().load(item.imageUrl).into(dialogImageView, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                setWallpaperBtn.visibility = View.VISIBLE
                dialogProgressBar.visibility = View.GONE
            }
            override fun onError(e: Exception?) {
                Log.d("Ошибка загрузки", e.toString())
            }
        })
        setWallpaperBtn.setOnClickListener {
            val wallpaperManager = WallpaperManager.getInstance(context) as WallpaperManager
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bitmap: Bitmap = dialogImageView.drawable.toBitmap()
                    // for async set wallpaper
                    withContext(Dispatchers.IO) { wallpaperManager.setBitmap(bitmap) }
                    Toast.makeText(context, "Обои успешно установлены!", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Ошибка: $e", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
        deletePhotoBtn.setOnClickListener {
        fragment.deletePhoto(item.imageUrl)
        items.remove(item)
            notifyDataSetChanged()
           dialog.dismiss()
        }

        dialog.show()
    }


}