package com.example.testscopedstorage.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testscopedstorage.R
import com.example.testscopedstorage.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}