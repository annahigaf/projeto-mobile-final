package com.example.aplicativotranport.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.aplicativotranport.data.Database
import com.example.aplicativotranport.databinding.FragmentEmbarqueBinding

class EmbarqueFragment : Fragment() {

    private var _binding: FragmentEmbarqueBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: Database
    private var rotas = listOf<Map<String, String>>()
    private var alunos = listOf<Map<String, String>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEmbarqueBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarRotas()

        binding.spinnerRotas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (rotas.isNotEmpty()) {
                    val rotaId = rotas[position]["id"]!!.toInt()
                    carregarAlunos(rotaId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return binding.root
    }

    private fun carregarRotas() {
        rotas = db.getAll("rotas")
        val nomes = rotas.map { it["nome"] ?: "Sem nome" }
        binding.spinnerRotas.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nomes)
    }

    private fun carregarAlunos(rotaId: Int) {
        alunos = db.getAlunosPorRota(rotaId)
        val adapter = object : ArrayAdapter<Map<String, String>>(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, alunos) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_2, parent, false)
                val aluno = alunos[position]
                val nome = aluno["nome"] ?: "Sem nome"
                val embarcado = aluno["embarcado"] == "1"

                view.findViewById<TextView>(android.R.id.text1).text = nome
                view.findViewById<TextView>(android.R.id.text2).text =
                    if (embarcado) "✅ Embarcado" else "❌ Não embarcado"

                view.setOnClickListener {
                    alternarStatus(aluno["id"]!!.toInt(), embarcado)
                    carregarAlunos(rotaId)
                }
                return view
            }
        }
        binding.listViewEmbarque.adapter = adapter
    }

    private fun alternarStatus(alunoId: Int, embarcadoAtual: Boolean) {
        val novoStatus = if (embarcadoAtual) 0 else 1
        val values = ContentValues().apply {
            put("embarcado", novoStatus)
        }
        db.update("alunos", values, "id=?", arrayOf(alunoId.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}