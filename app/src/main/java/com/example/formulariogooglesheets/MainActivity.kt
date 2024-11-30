package com.example.formulariogooglesheets

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNombres: EditText = findViewById(R.id.etNombres)
        val etPrimerApellido: EditText = findViewById(R.id.etPrimerApellido)
        val etSegundoApellido: EditText = findViewById(R.id.etSegundoApellido)
        val etEdad: EditText = findViewById(R.id.etEdad)
        val etFechaNacimiento: EditText = findViewById(R.id.etFechaNacimiento)
        //val etEstado: EditText = findViewById(R.id.etEstado)
        val btnEnviar: Button = findViewById(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val nombres = etNombres.text.toString()
            val primerApellido = etPrimerApellido.text.toString()
            val segundoApellido = etSegundoApellido.text.toString()
            val edad = etEdad.text.toString()
            val fechaNacimiento = etFechaNacimiento.text.toString()
           // val estado = etEstado.text.toString()

            // Validar los campos
            if (nombres.isEmpty() || primerApellido.isEmpty() || edad.isEmpty() || fechaNacimiento.isEmpty() ) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener fecha y hora actuales
            val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            // Enviar los datos
            enviarDatos(
                nombres, primerApellido, segundoApellido, edad, fechaNacimiento, hora, fechaRegistro,
            )
        }
    }

    private fun enviarDatos(
        nombres: String,
        primerApellido: String,
        segundoApellido: String,
        edad: String,
        fechaNacimiento: String,
        hora: String,
        fechaRegistro: String,

    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://script.google.com/macros/s/AKfycbydOTxqDUIJ-FdoAVD1n4OocLa7JVkg0A3U2g91IieB4Gn7X_JIfwiuyUxfx9khFjQN/exec")
                val json = JSONObject()
                json.put("nombres", nombres)
                json.put("primerApellido", primerApellido)
                json.put("segundoApellido", segundoApellido)
                json.put("edad", edad)
                json.put("fechaNacimiento", fechaNacimiento)
                json.put("hora", hora)
                json.put("fechaRegistro", fechaRegistro)


                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(json.toString())
                outputStream.flush()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Datos enviados con éxito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error al enviar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
