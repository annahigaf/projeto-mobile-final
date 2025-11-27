package com.example.aplicativotranport.ui.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.AcitivityCadastroBinding


class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: AcitivityCadastroBinding
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Database(this)

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                val values = ContentValues().apply {
                    put("nome", nome)
                    put("email", email)
                    put("senha", senha)
                }
                val result = db.insert("usuarios", values)
                if (result > 0) {
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao cadastrar. Tente outro e-mail.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}