package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentAlunosBinding

class AlunosFragment : Fragment() {

    private var _binding: FragmentAlunosBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database

    private lateinit var adapter: ArrayAdapter<String>
    private val alunosList = mutableListOf<String>()

    // listas para spinners
    private var responsaveis = listOf<Map<String, String>>()
    private var turmas = listOf<Map<String, String>>()
    private var escolas = listOf<Map<String, String>>()

    // controle de edição
    private var isEditing = false
    private var editingId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlunosBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarSpinners()
        carregarLista()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {

        // SALVAR ou ATUALIZAR
        binding.btnSalvarAluno.setOnClickListener {
            val nome = binding.edtNomeAluno.text.toString().trim()
            if (nome.isEmpty()) {
                Toast.makeText(requireContext(), "Informe o nome do aluno", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (responsaveis.isEmpty() || turmas.isEmpty() || escolas.isEmpty()) {
                Toast.makeText(requireContext(), "Cadastre responsável, turma e escola antes", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("responsavel_id", responsaveis[binding.spinnerResponsavel.selectedItemPosition]["id"])
                put("turma_id", turmas[binding.spinnerTurma.selectedItemPosition]["id"])
                put("escola_id", escolas[binding.spinnerEscola.selectedItemPosition]["id"])
                put("embarcado", 0)
            }

            if (isEditing) {
                // EDITAR
                db.update("alunos", values, "id=?", arrayOf(editingId.toString()))
                Toast.makeText(requireContext(), "Aluno atualizado!", Toast.LENGTH_SHORT).show()
            } else {
                // SALVAR
                db.insert("alunos", values)
                Toast.makeText(requireContext(), "Aluno salvo!", Toast.LENGTH_SHORT).show()
            }

            limparCampos()
            carregarLista()
        }

        // EDITAR aluno (clique simples)
        binding.listViewAlunos.setOnItemClickListener { _, _, position, _ ->
            val aluno = db.getAll("alunos")[position]

            editingId = aluno["id"]!!.toInt()
            binding.edtNomeAluno.setText(aluno["nome"])

            // Seleciona spinners corretos
            val respId = aluno["responsavel_id"]?.toIntOrNull()
            val turmaId = aluno["turma_id"]?.toIntOrNull()
            val escolaId = aluno["escola_id"]?.toIntOrNull()

            binding.spinnerResponsavel.setSelection(
                responsaveis.indexOfFirst { it["id"]?.toInt() == respId }
            )

            binding.spinnerTurma.setSelection(
                turmas.indexOfFirst { it["id"]?.toInt() == turmaId }
            )

            binding.spinnerEscola.setSelection(
                escolas.indexOfFirst { it["id"]?.toInt() == escolaId }
            )

            isEditing = true
            binding.btnSalvarAluno.text = "Atualizar"
        }

        // EXCLUIR aluno (clique longo)
        binding.listViewAlunos.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("alunos")[position]["id"]!!.toInt()
            db.delete("alunos", id)
            Toast.makeText(requireContext(), "Aluno removido!", Toast.LENGTH_SHORT).show()

            if (editingId == id) limparCampos()

            carregarLista()
            true
        }
    }

    private fun carregarSpinners() {
        responsaveis = db.getAll("responsaveis")
        turmas = db.getAll("turmas")
        escolas = db.getAll("escolas")

        val respNames = responsaveis.map { it["nome"] ?: "Sem nome" }
        val turmaNames = turmas.map { it["nome"] ?: "Sem nome" }
        val escolaNames = escolas.map { it["nome"] ?: "Sem nome" }

        binding.spinnerResponsavel.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, respNames)

        binding.spinnerTurma.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, turmaNames)

        binding.spinnerEscola.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, escolaNames)
    }

    private fun carregarLista() {
        val alunos = db.getAll("alunos")
        alunosList.clear()

        for (a in alunos) {
            val responsavel = db.getById("responsaveis", a["responsavel_id"]?.toIntOrNull() ?: -1)?.get("nome") ?: "-"
            val turma = db.getById("turmas", a["turma_id"]?.toIntOrNull() ?: -1)?.get("nome") ?: "-"
            val escola = db.getById("escolas", a["escola_id"]?.toIntOrNull() ?: -1)?.get("nome") ?: "-"

            alunosList.add(
                "${a["id"]} - ${a["nome"]}\nResponsável: $responsavel | Turma: $turma | Escola: $escola"
            )
        }

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, alunosList)
        binding.listViewAlunos.adapter = adapter
    }

    private fun limparCampos() {
        binding.edtNomeAluno.text.clear()

        isEditing = false
        editingId = null
        binding.btnSalvarAluno.text = "Salvar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
