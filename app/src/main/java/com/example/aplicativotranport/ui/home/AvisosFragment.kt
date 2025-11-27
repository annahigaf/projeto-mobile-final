package com.example.aplicativotranport.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentAvisosBinding
import com.example.aplicativotranport.model.Aviso
import com.example.aplicativotranport.ui.home.adapters.AvisosAdapter

class AvisosFragment : Fragment() {

    private lateinit var binding: FragmentAvisosBinding
    private lateinit var database: Database

    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tipoUsuario = arguments?.getString("tipoUsuario") ?: "OUTRO"
        binding = FragmentAvisosBinding.inflate(inflater, container, false)
        database = Database(requireContext())


        isAdmin = tipoUsuario == "ADMIN"

        binding.fabAddAviso.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.fabAddAviso.setOnClickListener {
            startActivity(Intent(requireContext(), AddAvisoActivity::class.java))
        }

        carregarAvisos()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        carregarAvisos()
    }

    private fun carregarAvisos() {
        val lista: List<Aviso> = database.listarAvisos()

        binding.recyclerAvisos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAvisos.adapter = AvisosAdapter(lista)
    }
}