package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentReesponsaveisBinding

class ResponsaveisFragment : Fragment() {

    private var _binding: FragmentReesponsaveisBinding? = null
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
        _binding = FragmentReesponsaveisBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarLista()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        binding.btnSalvarResponsavel.setOnClickListener {

            val nome = binding.edtNomeResponsavel.text.toString().trim()
            val telefone = binding.edtTelefoneResponsavel.text.toString().trim()

            if (nome.isEmpty() || telefone.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("telefone", telefone)
            }

            if (isEditing) {
                db.update("responsaveis", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Responsável atualizado!", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("responsaveis", values)
                Toast.makeText(requireContext(), "Responsável salvo!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarLista()
        }

        binding.listViewResponsaveis.setOnItemClickListener { _, _, position, _ ->
            val item = db.getAll("responsaveis")[position]

            editingId = item["id"]!!.toInt()
            binding.edtNomeResponsavel.setText(item["nome"])
            binding.edtTelefoneResponsavel.setText(item["telefone"])

            isEditing = true
            binding.btnSalvarResponsavel.text = "Atualizar"
        }

        binding.listViewResponsaveis.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("responsaveis")[position]["id"]!!.toInt()
            db.delete("responsaveis", id)

            Toast.makeText(requireContext(), "Responsável removido!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarLista()
            true
        }
    }

    private fun carregarLista() {
        val data = db.getAll("responsaveis")
        lista.clear()

        data.forEach {
            lista.add("${it["id"]} - ${it["nome"]} (📞 ${it["telefone"]})")
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewResponsaveis.adapter = adapter
    }

    private fun limparCampos() {
        binding.edtNomeResponsavel.text.clear()
        binding.edtTelefoneResponsavel.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarResponsavel.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
