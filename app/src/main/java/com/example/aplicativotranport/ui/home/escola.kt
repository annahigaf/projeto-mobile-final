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

    private val lista = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private var isEditing = false
    private var editingId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEscolasBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarLista()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        binding.btnSalvarEscola.setOnClickListener {

            val nome = binding.edtNomeEscola.text.toString().trim()
            val endereco = binding.edtEnderecoEscola.text.toString().trim()

            if (nome.isEmpty() || endereco.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("endereco", endereco)
            }

            if (isEditing) {
                db.update("escolas", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Escola atualizada!", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("escolas", values)
                Toast.makeText(requireContext(), "Escola salva!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarLista()
        }

        binding.listViewEscolas.setOnItemClickListener { _, _, position, _ ->
            val item = db.getAll("escolas")[position]

            editingId = item["id"]!!.toInt()
            binding.edtNomeEscola.setText(item["nome"])
            binding.edtEnderecoEscola.setText(item["endereco"])

            isEditing = true
            binding.btnSalvarEscola.text = "Atualizar"
        }

        binding.listViewEscolas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("escolas")[position]["id"]!!.toInt()
            db.delete("escolas", id)

            Toast.makeText(requireContext(), "Escola removida!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarLista()
            true
        }
    }

    private fun carregarLista() {
        val data = db.getAll("escolas")
        lista.clear()

        data.forEach {
            lista.add("${it["id"]} - ${it["nome"]}\n📍 ${it["endereco"]}")
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewEscolas.adapter = adapter
    }

    private fun limparCampos() {
        binding.edtNomeEscola.text.clear()
        binding.edtEnderecoEscola.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarEscola.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
