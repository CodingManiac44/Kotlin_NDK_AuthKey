package com.technonext.ndkauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.technonext.ndkauth.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.callButton.setOnClickListener {
            val baseUrl = binding.baseUrl.text?.toString()?.takeIf { it.isNotBlank() }
                ?: "https://jsonplaceholder.typicode.com/"
            callApi(baseUrl)
        }
    }

    private fun callApi(baseUrl: String) {
        binding.resultText.text = "Calling API..."
        val api = ApiClient.create(baseUrl)
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { api.getPost(1) }
                binding.resultText.text = "Success\n" + result.toString()
            } catch (t: Throwable) {
                binding.resultText.text = "Error: " + t.message
            }
        }
    }
}
