package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentTurmasBinding

class TurmasFragment : Fragment() {

    private var _binding: FragmentTurmasBinding? = null
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
        _binding = FragmentTurmasBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarLista()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        binding.btnSalvarTurma.setOnClickListener {

            val nome = binding.edtNomeTurma.text.toString().trim()
            val turno = binding.edtTurnoTurma.text.toString().trim()

            if (nome.isEmpty() || turno.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("turno", turno)
            }

            if (isEditing) {
                db.update("turmas", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Turma atualizada!", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("turmas", values)
                Toast.makeText(requireContext(), "Turma salva!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarLista()
        }

        binding.listViewTurmas.setOnItemClickListener { _, _, position, _ ->
            val item = db.getAll("turmas")[position]

            editingId = item["id"]!!.toInt()
            binding.edtNomeTurma.setText(item["nome"])
            binding.edtTurnoTurma.setText(item["turno"])

            isEditing = true
            binding.btnSalvarTurma.text = "Atualizar"
        }

        binding.listViewTurmas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("turmas")[position]["id"]!!.toInt()
            db.delete("turmas", id)

            Toast.makeText(requireContext(), "Turma removida!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarLista()
            true
        }
    }

    private fun carregarLista() {
        val data = db.getAll("turmas")
        lista.clear()

        data.forEach {
            lista.add("${it["id"]} - ${it["nome"]} (${it["turno"]})")
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewTurmas.adapter = adapter
    }

    private fun limparCampos() {
        binding.edtNomeTurma.text.clear()
        binding.edtTurnoTurma.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarTurma.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
