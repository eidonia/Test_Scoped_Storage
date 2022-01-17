package com.example.testscopedstorage.view

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.example.testscopedstorage.App.Companion.appContext
import com.example.testscopedstorage.repo.PicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val repository: PicRepository): ViewModel() {

    private var pendingFile: File? = null

    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    fun startMigration(sourceFile: File, destPath: String) {
        viewModelScope.launch {
            copyImage(sourceFile, destPath)
        }
    }

    private suspend fun copyImage(sourceFile: File, destPath: String) {
        withContext(Dispatchers.IO) {
            val fileCreate = File(destPath, sourceFile.name)
            Log.d("uriFile", "${fileCreate.path} + ${fileCreate.exists()}")
            if (!fileCreate.exists()) {
                var source: FileChannel? = null
                var dest: FileChannel? = null
                try {
                    source = FileInputStream(sourceFile).channel
                    dest = FileOutputStream(fileCreate).channel
                    dest.transferFrom(source, 0, source.size())

                    Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also {
                        val f = File(fileCreate.path)
                        it.data = Uri.fromFile(f)
                        appContext.sendBroadcast(it)
                    }
                }catch (fileNotFoundExc: FileNotFoundException){
                    Log.e("errorFile", "error : ${fileNotFoundExc.localizedMessage}")
                }
                finally {
                    source?.close()
                    dest?.close()
                }
            }

            deletePicture(sourceFile)

        }
    }

    fun deletePendingPic() {
        pendingFile?.let {
            pendingFile = null
            deletePic(it)
        }
    }

    private fun deletePic(file: File) {
        viewModelScope.launch {
            deletePicture(file)
        }
    }

    private suspend fun deletePicture(file: File) {
        withContext(Dispatchers.IO) {
            val idFile = (file.name.replace(".JPG", "")).toLong()
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                idFile
            )

            Log.d("contentUris", "${contentUri.path}")

            try {
                appContext.contentResolver.delete(
                    contentUri,
                    "${MediaStore.Images.Media._ID} = ?", arrayOf(idFile.toString())
                )
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as RecoverableSecurityException

                    pendingFile = file

                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }

}