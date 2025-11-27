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

    private lateinit var adapter: ArrayAdapter<String>
    private val listaRotas = mutableListOf<String>()
    private var condutores = listOf<Map<String, String>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRotasBinding.inflate(inflater, container, false)
        db = Database(requireContext())

        carregarCondutores()
        carregarRotas()

        binding.btnSalvarRota.setOnClickListener {
            val nome = binding.edtNomeRota.text.toString()
            if (nome.isEmpty() || condutores.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("nome", nome)
                put("condutor_id", condutores[binding.spinnerCondutor.selectedItemPosition]["id"])
            }

            db.insert("rotas", values)
            Toast.makeText(requireContext(), "Rota salva com sucesso!", Toast.LENGTH_SHORT).show()
            binding.edtNomeRota.text.clear()
            carregarRotas()
        }

        binding.listViewRotas.setOnItemClickListener { _, _, position, _ ->
            val rota = db.getAll("rotas")[position]
            val rotaId = rota["id"]!!.toInt()
            abrirDialogAlunos(rotaId)
        }

        binding.listViewRotas.setOnItemLongClickListener { _, _, position, _ ->
            val id = db.getAll("rotas")[position]["id"]!!.toInt()
            db.delete("rotas", id)
            carregarRotas()
            true
        }

        return binding.root
    }

    private fun carregarCondutores() {
        condutores = db.getAll("condutores")
        val nomes = condutores.map { it["nome"] ?: "Sem condutor" }
        binding.spinnerCondutor.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nomes)
    }

    private fun carregarRotas() {
        val data = db.getAll("rotas")
        listaRotas.clear()
        data.forEach {
            val condutor = db.getById("condutores", it["condutor_id"]?.toIntOrNull() ?: -1)?.get("nome") ?: "-"
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
            .setTitle("Vincular Alunos à Rota")
            .setMultiChoiceItems(nomes.toTypedArray(), selecionados) { _, which, isChecked ->
                val alunoId = todos[which]["id"]!!.toInt()
                if (isChecked) db.insertAlunoNaRota(alunoId, rotaId)
                else db.removeAlunoDaRota(alunoId, rotaId)
            }
            .setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(requireContext(), "Vínculos atualizados!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}