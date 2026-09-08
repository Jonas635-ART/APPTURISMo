package com.example.appturismo.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appturismo.data.model.PuntoTuristico
import com.example.appturismo.databinding.ItemPuntoBinding

class PuntoAdapter(
    private val isAdmin: Boolean = false,
    private val onPuntoClick: (PuntoTuristico) -> Unit,
    private val onFavoriteClick: (PuntoTuristico) -> Unit,
    private val onEditClick: ((PuntoTuristico) -> Unit)? = null,
    private val onDeleteClick: ((PuntoTuristico) -> Unit)? = null
) : ListAdapter<PuntoTuristico, PuntoAdapter.PuntoViewHolder>(PuntoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuntoViewHolder {
        val binding = ItemPuntoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PuntoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PuntoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PuntoViewHolder(private val binding: ItemPuntoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(punto: PuntoTuristico) {
            binding.tvNombre.text = punto.nombre
            binding.tvDescripcion.text = punto.descripcion
            binding.tvOrdenBadge.text = "Punto ${punto.orden} de 5"
            
            if (punto.fotos.isNotEmpty()) {
                Glide.with(binding.ivFoto.context)
                    .load(punto.fotos[0])
                    .into(binding.ivFoto)
            }

            val starIcon = if (punto.esFavorito) {
                android.R.drawable.btn_star_big_on
            } else {
                android.R.drawable.btn_star_big_off
            }
            binding.btnFavorite.setImageResource(starIcon)

            binding.btnFavorite.setOnClickListener { onFavoriteClick(punto) }
            binding.root.setOnClickListener { onPuntoClick(punto) }

            if (isAdmin) {
                binding.adminControls.visibility = android.view.View.VISIBLE
                binding.btnEdit.setOnClickListener { onEditClick?.invoke(punto) }
                binding.btnDelete.setOnClickListener { onDeleteClick?.invoke(punto) }
            } else {
                binding.adminControls.visibility = android.view.View.GONE
            }
        }
    }

    class PuntoDiffCallback : DiffUtil.ItemCallback<PuntoTuristico>() {
        override fun areItemsTheSame(oldItem: PuntoTuristico, newItem: PuntoTuristico): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PuntoTuristico, newItem: PuntoTuristico): Boolean = oldItem == newItem
    }
}
