package com.devapp.appforarduino.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.databinding.FragmentHistoryImageBinding
import com.devapp.appforarduino.ui.activities.HomeActivity
import com.devapp.appforarduino.ui.adapter.HistoryImageAdapter
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import kotlinx.coroutines.flow.collect

class HistoryImageFragment:Fragment(R.layout.fragment_history_image) {
		private lateinit var _binding:FragmentHistoryImageBinding
		private val binding get() = _binding!!
		private lateinit var model:HomeViewModel
		private lateinit var historyImageAdapter: HistoryImageAdapter
		private lateinit var dialogBuilder:MaterialDialog
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentHistoryImageBinding.inflate(inflater)
				model = (requireActivity() as HomeActivity).homeViewModel
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				setupRecyclerView()
				lifecycleScope.launchWhenStarted {
						model.getAllImage.collect {
								historyImageAdapter.submitList(it)
						}
				}
				historyImageAdapter.setOnItemLongClickListener {
						openDialogBuilderDelete(it)
				}
				historyImageAdapter.setOnItemClickListener {

				}
				binding.fab.setOnClickListener {
						openDialogBuilderDeleteAll()
				}
				super.onViewCreated(view, savedInstanceState)
		}
		private fun setupRecyclerView(){
				historyImageAdapter = HistoryImageAdapter(requireContext())
				binding.recyclerView.apply {
						adapter = historyImageAdapter
						layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
				}
		}
		private fun openDialogBuilderDelete(imageData: ImageData){
				dialogBuilder = MaterialDialog.Builder(requireActivity())
						.setTitle("Xóa ?", TextAlignment.CENTER)
						.setMessage("Bạn chắc chắn xóa hình ảnh ?", TextAlignment.CENTER)
						.setCancelable(false)
						.setPositiveButton(
								"OK", R.drawable.ic_delete_dialog
						) { dialogInterface, which ->
								try {
										model.deleteImage(imageData)
										Snackbar.make(binding.root,"Bạn đã xóa thành công ", Snackbar
												.LENGTH_LONG).setActionTextColor(Color.RED)
												.show()
								}
								catch (e:Exception){
										Snackbar.make(binding.root,"Bạn đã xóa không thành công ", Snackbar
												.LENGTH_LONG).setActionTextColor(Color.RED)
												.show()
								}
								dialogInterface.dismiss()
						}
						.setNegativeButton(
								"Hủy", R.drawable.ic_close
						) { dialogInterface, which -> dialogInterface.dismiss() }
						.setAnimation(R.raw.delete)
						.build()
				dialogBuilder.show()
		}
		private fun openDialogBuilderDeleteAll(){
				dialogBuilder = MaterialDialog.Builder(requireActivity())
						.setTitle("Xóa ?", TextAlignment.CENTER)
						.setMessage("Bạn chắc chắn xóa toàn bộ hình ảnh  ?", TextAlignment.CENTER)
						.setCancelable(false)
						.setPositiveButton(
								"OK", R.drawable.ic_delete_dialog
						) { dialogInterface, which ->
								try {
										model.deleteAllImage()
										Snackbar.make(binding.root,"Bạn đã xóa thành  công ", Snackbar
												.LENGTH_LONG).setActionTextColor(Color.RED)
												.show()
								}
								catch (e:Exception){
										Snackbar.make(binding.root,"Bạn đã xóa không thành công ", Snackbar
												.LENGTH_LONG).setActionTextColor(Color.RED)
												.show()
								}
								dialogInterface.dismiss()
						}
						.setNegativeButton(
								"Hủy", R.drawable.ic_close
						) { dialogInterface, which -> dialogInterface.dismiss() }
						.setAnimation(R.raw.delete)
						.build()
				dialogBuilder.show()
		}
}