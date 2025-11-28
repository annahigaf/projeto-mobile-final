package com.example.aplicativotranport.ui.home

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentRotasBinding

class RotasFragment : Fragment() {

    private var _binding: FragmentRotasBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database

    private val listaRotas = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private var condutores = listOf<Map<String, String>>()

    private var isEditing = false
    private var editingId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRotasBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarCondutores()
        carregarRotas()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        // 🟧 BOTÃO SALVAR/EDITAR
        binding.btnSalvarRota.setOnClickListener {
            val nome = binding.edtNomeRota.text.toString().trim()

            if (nome.isEmpty() || condutores.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("condutor_id", condutores[binding.spinnerCondutor.selectedItemPosition]["id"])
            }

            if (isEditing) {
                db.update("rotas", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Rota atualizada!", Toast.LENGTH_SHORT).show()
            } else {
                db.insert("rotas", values)
                Toast.makeText(requireContext(), "Rota salva!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarRotas()
        }

        // 🟦 EDITAR ROTA (click simples)
        binding.listViewRotas.setOnItemClickListener { _, _, position, _ ->
            val rota = db.getAll("rotas")[position]

            editingId = rota["id"]!!.toInt()
            binding.edtNomeRota.setText(rota["nome"])

            val condutorId = rota["condutor_id"]?.toIntOrNull() ?: -1
            binding.spinnerCondutor.setSelection(
                condutores.indexOfFirst { it["id"]?.toInt() == condutorId }
            )

            isEditing = true
            binding.btnSalvarRota.text = "Atualizar"

            abrirDialogAlunos(editingId!!)
        }

        // 🟥 EXCLUIR ROTA (click longo)
        binding.listViewRotas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("rotas")[position]["id"]!!.toInt()
            db.delete("rotas", id)

            Toast.makeText(requireContext(), "Rota removida!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarRotas()
            true
        }
    }

    private fun carregarCondutores() {
        condutores = db.getAll("condutores")

        val nomes = condutores.map { it["nome"] ?: "Sem condutor" }

        binding.spinnerCondutor.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            nomes
        )
    }

    private fun carregarRotas() {
        val data = db.getAll("rotas")
        listaRotas.clear()

        data.forEach {
            val condutor = db.getById(
                "condutores",
                it["condutor_id"]?.toIntOrNull() ?: -1
            )?.get("nome") ?: "-"

            listaRotas.add("${it["id"]} - ${it["nome"]} (Condutor: $condutor)")
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaRotas)
        binding.listViewRotas.adapter = adapter
    }

    private fun abrirDialogAlunos(rotaId: Int) {
        val todos = db.getAll("alunos")
        val alunosDaRota = db.getAlunosPorRota(rotaId).map { it["id"] }

        val nomes = todos.map { it["nome"] ?: "Sem nome" }
        val selecionados = BooleanArray(todos.size) { i ->
            alunosDaRota.contains(todos[i]["id"])
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Vincular alunos à rota")
            .setMultiChoiceItems(nomes.toTypedArray(), selecionados) { _, which, checked ->

                val alunoId = todos[which]["id"]!!.toInt()

                if (checked) db.insertAlunoNaRota(alunoId, rotaId)
                else db.removeAlunoDaRota(alunoId, rotaId)
            }
            .setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(requireContext(), "Vínculos atualizados!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun limparCampos() {
        binding.edtNomeRota.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarRota.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
