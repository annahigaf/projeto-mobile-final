package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentReesponsaveisBinding

class ResponsaveisFragment : Fragment() {
    private var _binding: FragmentReesponsaveisBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database
    private lateinit var adapter: ArrayAdapter<String>
    private val lista = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReesponsaveisBinding.inflate(inflater, container, false)
        db = Database(requireContext())
        carregarLista()

        binding.btnSalvarResponsavel.setOnClickListener {
            val nome = binding.edtNomeResponsavel.text.toString()
            val telefone = binding.edtTelefoneResponsavel.text.toString()
            if (nome.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val values = ContentValues().apply {
                put("nome", nome)
                put("telefone", telefone)
            }
            db.insert("responsaveis", values)
            Toast.makeText(requireContext(), "Responsável salvo!", Toast.LENGTH_SHORT).show()
            binding.edtNomeResponsavel.text.clear()
            binding.edtTelefoneResponsavel.text.clear()
            carregarLista()
        }

        binding.listViewResponsaveis.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("responsaveis")[position]["id"]!!.toInt()
            db.delete("responsaveis", id)
            carregarLista()
            true
        }

        return binding.root
    }

    private fun carregarLista() {
        val data = db.getAll("responsaveis")
        lista.clear()
        data.forEach { lista.add("${it["id"]} - ${it["nome"]} (📞 ${it["telefone"]})") }
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewResponsaveis.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}