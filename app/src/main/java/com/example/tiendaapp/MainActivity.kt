package com.example.tiendaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Configurar el botón para navegar a la actividad de productos
        binding.buttonGoToProduct.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        binding.imageViewGoToProduct.setOnClickListener {
            // Aquí se navega a la actividad de productos al hacer clic en la imagen
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }
    }
}
