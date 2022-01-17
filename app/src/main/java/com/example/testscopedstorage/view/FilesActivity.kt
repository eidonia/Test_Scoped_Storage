package com.example.testscopedstorage.view

import android.app.Activity
import android.content.*
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.testscopedstorage.databinding.ActivityFilesBinding
import com.example.testscopedstorage.utils.Constante.CHOSEN_PATH
import com.example.testscopedstorage.utils.Constante.URI_DCIM
import com.example.testscopedstorage.utils.GetPath
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

@AndroidEntryPoint
class FilesActivity : AppCompatActivity() {

    private var filesInChosenFolder = 0
    private lateinit var viewModel: ListViewModel
    var iterator = 0

    private lateinit var binding: ActivityFilesBinding
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        viewModel.permissionNeededForDelete.observe(this, { intentSender ->
            openDeleteLauncher.launch(IntentSenderRequest.Builder(intentSender!!).build())
        })

        binding.btnMigrate.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, URI_DCIM)
                }
                openDocumentLauncher.launch(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data.let { uri ->
                val docutree = DocumentsContract.buildDocumentUriUsingTree(
                    uri,
                    DocumentsContract.getTreeDocumentId(uri)
                )

                val source = File(GetPath().getPath(this, docutree))
                val sharedPrefs: SharedPreferences = getSharedPreferences("plop", Context.MODE_PRIVATE)

                fileIsDirectory(source, sharedPrefs.getString(CHOSEN_PATH, null)!!)

                //binding.txtFileSource.text = "Nombre de fichier dans le fichier source : ${source.listFiles().size}"
                //binding.txtFileCreate.text = "Nombre de fichier dans le fichier crée : ${destination.listFiles().size}"

            }
        }
    }

    val openDeleteLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            viewModel.deletePendingPic()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun fileIsDirectory(file: File, destination: String) {
        if (file.isDirectory) {
            val fileDest = File(destination + File.separator + file.name).apply { mkdir() }
            Log.d("uriFile", "- - - - - - - - -")
            Log.d("uriFile", "DIRECTORY ${file.name}")
            Log.d("uriFile", "DIRECTORY ${file.path}")
            Log.d("uriFile", "DIRECTORY ${file.listFiles().size}")
            Log.d("uriFile", "- - - - - - - - -")
            for (f in file.listFiles()) {
                Log.d("uriFile", "$iterator")
                fileIsDirectory(f, fileDest.path)
                iterator++
            }
        }else {
            viewModel.startMigration(file, destination)
        }
    }

}