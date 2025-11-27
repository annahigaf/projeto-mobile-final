package com.example.aplicativotranport.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativotranport.MainActivity
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Database(this)

        // 🔹 Ação do botão de login
        binding.btnLogin.setOnClickListener {
            val usuario = binding.edtUsuario.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (usuario.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                val usuarios = db.getAll("usuarios")
                val user = usuarios.find { it["email"] == usuario && it["senha"] == senha }

                if (user != null) {

                    // 🔥 DEFINIR TIPO DE USUÁRIO AQUI
                    val tipoUsuario = when {
                        usuario.endsWith("@adm.com") -> "ADMIN"
                        usuario.endsWith("@aluno.com") -> "ALUNO"
                        else -> "OUTRO"
                    }

                    Toast.makeText(this, "Bem-vindo, ${user["nome"]}!", Toast.LENGTH_SHORT).show()

                    // 👉 ENVIA PARA A MAIN ACTIVITY
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("tipoUsuario", tipoUsuario)
                    startActivity(intent)

                    finish()

                } else {
                    Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 🔹 Ação do texto "Criar conta"
        binding.txtCriarConta.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}
