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
import com.example.aplicativotranport.databinding.FragmentCondutoresBinding

class CondutoresFragment : Fragment() {

    private var _binding: FragmentCondutoresBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database
    private lateinit var adapter: ArrayAdapter<String>
    private val lista = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCondutoresBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarLista()

        binding.btnSalvarCondutor.setOnClickListener {
            val nome = binding.edtNomeCondutor.text.toString()
            val placa = binding.edtPlacaCondutor.text.toString()

            if (nome.isEmpty() || placa.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("placa", placa)
            }

            db.insert("condutores", values)
            Toast.makeText(requireContext(), "Condutor salvo com sucesso!", Toast.LENGTH_SHORT).show()
            binding.edtNomeCondutor.text.clear()
            binding.edtPlacaCondutor.text.clear()
            carregarLista()
        }

        binding.listViewCondutores.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("condutores")[position]["id"]!!.toInt()
            db.delete("condutores", id)
            carregarLista()
            true
        }

        return binding.root
    }

    private fun carregarLista() {
        val data = db.getAll("condutores")
        lista.clear()
        data.forEach { lista.add("${it["id"]} - ${it["nome"]} (${it["placa"]})") }
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewCondutores.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}