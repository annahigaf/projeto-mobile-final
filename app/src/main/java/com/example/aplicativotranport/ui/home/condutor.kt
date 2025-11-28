package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentCondutoresBinding

class CondutoresFragment : Fragment() {

    private var _binding: FragmentCondutoresBinding? = null
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
        _binding = FragmentCondutoresBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarLista()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        binding.btnSalvarCondutor.setOnClickListener {
            val nome = binding.edtNomeCondutor.text.toString().trim()
            val placa = binding.edtPlacaCondutor.text.toString().trim()

            if (nome.isEmpty() || placa.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("placa", placa)
            }

            if (isEditing) {
                db.update("condutores", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Condutor atualizado!", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("condutores", values)
                Toast.makeText(requireContext(), "Condutor salvo!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarLista()
        }

        binding.listViewCondutores.setOnItemClickListener { _, _, position, _ ->
            val item = db.getAll("condutores")[position]

            editingId = item["id"]!!.toInt()
            binding.edtNomeCondutor.setText(item["nome"])
            binding.edtPlacaCondutor.setText(item["placa"])

            isEditing = true
            binding.btnSalvarCondutor.text = "Atualizar"
        }

        binding.listViewCondutores.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("condutores")[position]["id"]!!.toInt()
            db.delete("condutores", id)

            Toast.makeText(requireContext(), "Condutor removido!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarLista()
            true
        }
    }

    private fun carregarLista() {
        val data = db.getAll("condutores")
        lista.clear()

        data.forEach {
            lista.add("${it["id"]} - ${it["nome"]} (${it["placa"]})")
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewCondutores.adapter = adapter
    }

    private fun limparCampos() {
        binding.edtNomeCondutor.text.clear()
        binding.edtPlacaCondutor.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarCondutor.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
