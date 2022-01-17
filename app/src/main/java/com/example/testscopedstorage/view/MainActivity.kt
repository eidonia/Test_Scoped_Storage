package com.example.testscopedstorage.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import com.example.testscopedstorage.databinding.ActivityMainBinding
import com.example.testscopedstorage.utils.Constante.CHOSEN_PATH
import com.example.testscopedstorage.utils.Constante.URI_DCIM
import com.example.testscopedstorage.utils.GetPath
import java.security.Permission
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var uri: Uri
    private val ACCESS_EXTERNAL_STORAGE_REQUEST = 507

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                ACCESS_EXTERNAL_STORAGE_REQUEST
                )
        }


        binding.button.setOnClickListener {
            Log.d("blop", Environment.DIRECTORY_DCIM.toUri().toString())
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, URI_DCIM)
            }
            activityForResult.launch(intent)
        }
    }

    @SuppressLint("NewApi")
    val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data.also { intent ->
                intent?.data.let {
                    val docutree = DocumentsContract.buildDocumentUriUsingTree(
                        it,
                        DocumentsContract.getTreeDocumentId(it)
                    )

                    val path = GetPath().getPath(this, docutree)

                    var sharedPrefs: SharedPreferences = getSharedPreferences("plop", Context.MODE_PRIVATE)
                    sharedPrefs.edit { putString(CHOSEN_PATH, path) }

                    val takeFlags: Int =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    applicationContext.contentResolver.takePersistableUriPermission(
                        intent?.data!!,
                        takeFlags
                    )
                }
            }
            val intent = Intent(this, FilesActivity::class.java)
            startActivity(intent)
        }
    }

}