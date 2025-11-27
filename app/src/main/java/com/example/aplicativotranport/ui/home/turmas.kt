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
    private lateinit var adapter: ArrayAdapter<String>
    private val lista = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTurmasBinding.inflate(inflater, container, false)
        db = Database(requireContext())
        carregarLista()

        binding.btnSalvarTurma.setOnClickListener {
            val nome = binding.edtNomeTurma.text.toString()
            val turno = binding.edtTurnoTurma.text.toString()
            if (nome.isEmpty() || turno.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val values = ContentValues().apply {
                put("nome", nome)
                put("turno", turno)
            }
            db.insert("turmas", values)
            Toast.makeText(requireContext(), "Turma salva!", Toast.LENGTH_SHORT).show()
            binding.edtNomeTurma.text.clear()
            binding.edtTurnoTurma.text.clear()
            carregarLista()
        }

        binding.listViewTurmas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("turmas")[position]["id"]!!.toInt()
            db.delete("turmas", id)
            carregarLista()
            true
        }

        return binding.root
    }

    private fun carregarLista() {
        val data = db.getAll("turmas")
        lista.clear()
        data.forEach { lista.add("${it["id"]} - ${it["nome"]} (${it["turno"]})") }
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, lista)
        binding.listViewTurmas.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}