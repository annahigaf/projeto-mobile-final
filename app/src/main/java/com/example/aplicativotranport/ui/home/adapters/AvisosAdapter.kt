package com.example.aplicativotranport.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativotranport.databinding.ItemAvisoBinding
import com.example.aplicativotranport.model.Aviso

class AvisosAdapter(private val lista: List<Aviso>) :
    RecyclerView.Adapter<AvisosAdapter.AvisoViewHolder>() {

    inner class AvisoViewHolder(val binding: ItemAvisoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvisoViewHolder {
        val binding = ItemAvisoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AvisoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvisoViewHolder, position: Int) {
        val aviso = lista[position]
        holder.binding.txtTituloAviso.text = aviso.titulo
        holder.binding.txtDescricaoAviso.text = aviso.descricao
        holder.binding.txtDataAviso.text = aviso.data
    }

    override fun getItemCount() = lista.size
}
