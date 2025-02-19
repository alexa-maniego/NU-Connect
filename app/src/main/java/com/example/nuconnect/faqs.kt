package com.example.nuconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class faqs : AppCompatActivity() {

    private lateinit var imageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_faqs)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageButton = findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            val intent = Intent(this, Welcomepage::class.java)
            startActivity(intent)
        }

        setupFaqItem(R.id.faqTitle1, R.id.faqContent1, R.id.arrowIcon1)
        setupFaqItem(R.id.faqTitle2, R.id.faqContent2, R.id.arrowIcon2)
        setupFaqItem(R.id.faqTitle3, R.id.faqContent3, R.id.arrowIcon3)
        setupFaqItem(R.id.faqTitle4, R.id.faqContent4, R.id.arrowIcon4)
        setupFaqItem(R.id.faqTitle5, R.id.faqContent5, R.id.arrowIcon5)
    }

    private fun setupFaqItem(titleId: Int, contentId: Int, arrowId: Int) {
        val faqTitle: TextView = findViewById(titleId)
        val faqContent: TextView = findViewById(contentId)
        val arrowIcon: ImageView = findViewById(arrowId)

        faqTitle.setOnClickListener {
            if (faqContent.visibility == View.GONE) {
                faqContent.visibility = View.VISIBLE
                arrowIcon.rotation = 180f
            } else {
                faqContent.visibility = View.GONE
                arrowIcon.rotation = 0f
            }
        }
    }
}
