package com.example.aplicativotranport.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.ActivityAddAvisoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddAvisoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAvisoBinding
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAvisoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Database(this)

        binding.btnSalvarAviso.setOnClickListener {
            val titulo = binding.edtTituloAviso.text.toString().trim()
            val descricao = binding.edtDescricaoAviso.text.toString().trim()

            if (titulo.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            db.inserirAviso(titulo, descricao, data)

            Toast.makeText(this, "Aviso publicado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
