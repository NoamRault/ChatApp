package com.noamrault.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.noamrault.chatapp.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}