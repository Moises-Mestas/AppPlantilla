package com.example.appfirst

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.appfirst.core.navigation.NavigationWrapper


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura la apariencia de la ventana
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Verificar y solicitar permisos en tiempo de ejecución
        checkPermissions()

        // Establecer el contenido de la aplicación
        setContent {
            NavigationWrapper()
        }
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        } else {
            Log.d("MainActivity", "Permiso de almacenamiento ya concedido.")
        }
    }

    // Manejar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Log.d("MainActivity", "Permiso concedido para almacenamiento.")
            } else {
                // Permiso denegado
                Log.d("MainActivity", "Permiso denegado para almacenamiento.")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1 // Código para la solicitud de permisos
    }
}
