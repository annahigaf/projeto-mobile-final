package com.example.aplicativotranport

import androidx.activity.addCallback
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.aplicativotranport.ui.home.AlunosFragment
import com.google.android.material.navigation.NavigationView
import com.example.aplicativotranport.ui.login.LoginActivity
import com.example.aplicativotranport.ui.home.*
import com.example.aplicativotranport.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                finish()
            }
        }

        val tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "OUTRO"

        supportActionBar?.title = "Escola Conecta"

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        val bundle = Bundle()
        bundle.putString("tipoUsuario", tipoUsuario)


        val fragment = AvisosFragment()
        fragment.arguments = Bundle().apply {
            putString("tipoUsuario", tipoUsuario)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_home -> {
                val tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "OUTRO"
                val bundle = Bundle()
                bundle.putString("tipoUsuario", tipoUsuario)

                val fragment = AvisosFragment()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
            }

            R.id.nav_alunos -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AlunosFragment()).commit()

            R.id.nav_responsaveis -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ResponsaveisFragment()).commit()

            R.id.nav_turmas -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TurmasFragment()).commit()

            R.id.nav_escolas -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EscolasFragment()).commit()

            R.id.nav_condutores -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CondutoresFragment()).commit()

            R.id.nav_rotas -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RotasFragment())
                .commit()

            R.id.nav_embarque -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, EmbarqueFragment())
                .commit()

            R.id.nav_integrantes -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IntegrantesFragment()).commit()

            R.id.nav_logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
