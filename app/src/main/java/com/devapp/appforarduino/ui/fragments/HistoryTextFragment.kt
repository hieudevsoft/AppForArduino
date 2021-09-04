package com.devapp.appforarduino.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.R
import com.devapp.appforarduino.databinding.FragmentHistoryTextBinding
import com.devapp.appforarduino.ui.activities.HomeActivity
import com.devapp.appforarduino.ui.adapter.HistoryTextAdapter
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.util.Util
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class HistoryTextFragment : Fragment(com.devapp.appforarduino.R.layout.fragment_history_text) {
    private var _binding: FragmentHistoryTextBinding? = null
    private lateinit var historyTextAdapter: HistoryTextAdapter
    private val binding get() = _binding!!
    private lateinit var model: HomeViewModel
    private lateinit var dialogBuilder: MaterialDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryTextBinding.inflate(inflater)
        model = (activity as HomeActivity).homeViewModel
        return binding.root!!
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var job: Job? = null
        lifecycleScope.launchWhenStarted {

            model.getAllTextAndColorData.collect {
                Log.d("TAG", "onViewCreated: dataAll $it")
                if (it.isNotEmpty()) {
                    showOrHideNoData(false)
                    historyTextAdapter.submitList(it)
                } else showOrHideNoData(true)

            }
            model.updateState.collect {
                when (it) {
                    is HomeViewModel.UpdateState.Loading -> {

                    }
                    is HomeViewModel.UpdateState.Error -> {
                        withContext(Dispatchers.Main) {
                            Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    is HomeViewModel.UpdateState.Success -> {
                        Snackbar.make(binding.root, "Cập nhật chữ thành công", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {

                    }
                }
            }

        }
        model.currentQueryText.observe(viewLifecycleOwner, { query ->
            lifecycleScope.launchWhenStarted {
                model.getSearchedTextAndColor(query).collect {
                    if(it.isNotEmpty()){
                        Log.d("TAG", "onViewCreated: $query")
                        showOrHideNoData(false)
                        historyTextAdapter.submitList(it)
                    }else showOrHideNoData(true)

                }
            }
        })
            model.deleteAllTextAndColorState.observe(viewLifecycleOwner, {
                Log.d("TAG", "onViewCreated: deleteall $it")
                it?.let {
                    if (it) {
                        Snackbar.make(
                            binding.root,
                            "Bạn đã xóa thành công",
                            Snackbar.LENGTH_LONG,
                        ).show()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Bạn đã xóa thất bại",
                            Snackbar.LENGTH_LONG,
                        )
                            .show()
                    }
                }
            })

        binding.btnTest.setOnClickListener {
           openDeleteAllDataDialog()
        }
        binding.edtSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launchWhenStarted {
                delay(Util.SEARCH_DEFAULT_DELAY)
                if (editable != null) {
                    model.currentQueryText.value = editable.toString()
                }
            }
        }
        setupRecyclerView()
    }

    private fun openDeleteAllDataDialog() {
        dialogBuilder = MaterialDialog.Builder(requireActivity())
            .setTitle("Xóa ?", TextAlignment.CENTER)
            .setMessage("Bạn chắc chắn xóa toàn bộ chữ ?", TextAlignment.CENTER)
            .setCancelable(false)
            .setPositiveButton(
                "OK", R.drawable.ic_delete_dialog
            ) { dialogInterface, which ->
                model.deleteAllTextAndColor()
                dialogInterface.dismiss()
            }
            .setNegativeButton(
                "Hủy", R.drawable.ic_close
            ) { dialogInterface, which -> dialogInterface.dismiss() }
            .setAnimation(R.raw.delete)
            .build()
        dialogBuilder.show()
    }

    private fun setupRecyclerView() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT

        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = historyTextAdapter.getItemAtPosition(position)
                model.deleteTextAndColor(item)
                model.deleteTextAndColorState.observe(viewLifecycleOwner, {
                    it?.let {
                        if (it) {
                            Snackbar.make(
                                binding.root,
                                "Bạn đã xóa nội dung này",
                                Snackbar.LENGTH_LONG,
                            ).apply {
                                setAction("Khôi phục") {
                                    model.saveTextAndColor(item)
                                }
                            }.show()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Bạn đã xóa nội dung này thất bại",
                                Snackbar.LENGTH_LONG,
                            )
                                .show()
                        }
                    }
                })
            }

        }
        historyTextAdapter = HistoryTextAdapter()
        binding.recyclerView.apply {
            adapter = historyTextAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.recyclerView)
        historyTextAdapter.setOnItemClickListener {
            dialogBuilder = MaterialDialog.Builder(requireActivity())
                .setTitle("Hiển thị nội dung ?", TextAlignment.CENTER)
                .setMessage("Bạn chắc chắn hiển thị chữ này chứ?", TextAlignment.CENTER)
                .setCancelable(false)
                .setPositiveButton(
                    "OK", R.drawable.ic_delete_dialog
                ) { dialogInterface, which ->
                    model.updateTextAndColorToFireBase(it)
                    dialogInterface.dismiss()
                }
                .setNegativeButton(
                    "Hủy", R.drawable.ic_close
                ) { dialogInterface, which -> dialogInterface.dismiss() }
                .setAnimation(R.raw.update)
                .build()
            dialogBuilder.show()
        }
    }

    private fun showOrHideNoData(isShow:Boolean){
        binding.lyContent.visibility = if(isShow) View.GONE else View.VISIBLE
        binding.imgNoData.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        _binding = null
        model.deleteTextAndColorState.value = null
        model.currentQueryText.value = ""
        model.setNullForDeleteAllTextAndColorState()
        model.setEmptyForUpdateState()
        super.onDestroyView()
    }

}