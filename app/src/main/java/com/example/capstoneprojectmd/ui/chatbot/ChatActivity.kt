package com.example.capstoneprojectmd.ui.chatbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstoneprojectmd.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat)

        // Check if the fragment is already added
        if (savedInstanceState == null) {
            // Add ChatFragment to the container if this is the first launch
            val chatFragment: Fragment = ChatFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .commit()
        }
    }
}
