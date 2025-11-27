package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentEscolasBinding

class EscolasFragment : Fragment() {
    private var _binding: FragmentEscolasBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database
    private lateinit var adapter: ArrayAdapter<String>
    private val lista = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEscolasBinding.inflate(inflater, container, false)
        db = Database(requireContext())
        carregarLista()

        binding.btnSalvarEscola.setOnClickListener {
            val nome = binding.edtNomeEscola.text.toString()
            val endereco = binding.edtEnderecoEscola.text.toString()
            if (nome.isEmpty() || endereco.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val values = ContentValues().apply {
                put("nome", nome)
                put("endereco", endereco)
            }
            db.insert("escolas", values)
            Toast.makeText(requireContext(), "Escola salva!", Toast.LENGTH_SHORT).show()
            binding.edtNomeEscola.text.clear()
            binding.edtEnderecoEscola.text.clear()
            carregarLista()
        }

        binding.listViewEscolas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("escolas")[position]["id"]!!.toInt()
            db.delete("escolas", id)
            carregarLista()
            true
        }

        return binding.root
    }

    private fun carregarLista() {
        val data = db.getAll("escolas")
        lista.clear()
        data.forEach { lista.add("${it["id"]} - ${it["nome"]}\n📍 ${it["endereco"]}") }
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewEscolas.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}