package com.example.testscopedstorage.repo

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.testscopedstorage.App.Companion.appContext
import com.example.testscopedstorage.network.PictureService
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject
import android.provider.MediaStore


class PicRepository @Inject constructor(private val picService: PictureService) {


    fun copyImage(sourceFile: File, destPath: String): File {
        Log.d("uriFile", "path : ${sourceFile.path}")
        Log.d("uriFile", "parentFile : ${sourceFile.parentFile}")
        Log.d("uriFile", "absolutePath : ${sourceFile.absolutePath}")
        Log.d("uriFile", "name : ${sourceFile.name}")
        Log.d("uriFile", "- - - - - - - - -")

        //val fileDest = File(destination, sourceFile.name)
        Log.d("fileDest", destPath)



        return sourceFile

    }
}