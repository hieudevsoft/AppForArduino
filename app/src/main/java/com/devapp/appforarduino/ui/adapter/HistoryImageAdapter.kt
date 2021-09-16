package com.devapp.appforarduino.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.databinding.ItemHistoryImageBinding


class HistoryImageAdapter(private val context: Context) : RecyclerView.Adapter<HistoryImageAdapter
.ViewHolder>() {

		inner class ViewHolder(val binding: ItemHistoryImageBinding) :
				RecyclerView.ViewHolder(binding.root) {
				fun bind(imageData: ImageData) {
						binding.img.setImageBitmap(imageData.data)
				}
		}

		private val diffUtilItemCallBack = object : DiffUtil.ItemCallback<ImageData>() {
				override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
						return oldItem.id == newItem.id
				}

				override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
						return oldItem.hashCode() == newItem.hashCode()
				}

		}

		private val differ = AsyncListDiffer(this, diffUtilItemCallBack)

		fun submitList(list: List<ImageData>) {
				differ.submitList(list)
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
				return ViewHolder(
						ItemHistoryImageBinding.inflate(
								LayoutInflater.from(parent.context),
								parent,
								false
						)
				)
		}

		fun getItemAtPosition(position: Int) = differ.currentList[position]

		override fun onBindViewHolder(holder: ViewHolder, position: Int) {
				val item = differ.currentList[position]
				holder.bind(item)
				holder.binding.root.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_image))
				holder.binding.root.setOnClickListener {
						onLickItemListener?.let { it(item) }
				}
				holder.binding.root.setOnLongClickListener {
						onLongLickItemListener?.let { it(item) }
						true
				}
		}

		private var onLickItemListener: ((ImageData) -> Unit)? = null
		private var onLongLickItemListener: ((ImageData) -> Unit)? = null

		fun setOnItemClickListener(listener: (ImageData) -> Unit) {
				onLickItemListener = listener
		}

		fun setOnItemLongClickListener(listener: (ImageData) -> Unit) {
				onLongLickItemListener = listener
		}

		override fun getItemCount(): Int {
				return differ.currentList.size
		}
}