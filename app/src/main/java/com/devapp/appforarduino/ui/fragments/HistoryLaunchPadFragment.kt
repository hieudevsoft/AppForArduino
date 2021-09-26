package com.devapp.appforarduino.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.db.LocalDataBase
import com.devapp.appforarduino.databinding.FragmentHistoryLaunchpadBinding
import com.devapp.appforarduino.domain.app_repository.AppRepository
import com.devapp.appforarduino.domain.app_repository.AppRepositoryImpl
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepository
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepositoryImpl
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.ui.activities.LaunchPadActivity
import com.devapp.appforarduino.ui.adapter.HistoryLaunchPadAdapter
import com.devapp.appforarduino.ui.viewmodels.LaunchPadViewModel
import com.devapp.appforarduino.ui.viewmodels.LaunchPadViewModelFactory
import com.devapp.appforarduino.util.Util
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class HistoryLaunchPadFragment: Fragment(R.layout.fragment_history_launchpad) {

		private lateinit var _binding:FragmentHistoryLaunchpadBinding
		private val  binding get() = _binding!!

		private lateinit var localDataBase: LocalDataBase
		private lateinit var localDataRepository: LocalDataRepository
		private lateinit var appRepository: AppRepository
		private lateinit var deleteLaunchPadUseCase: DeleteLaunchPadUseCase
		private lateinit var deleteAllLaunchPadUseCase: DeleteAllLaunchPadUseCase
		private lateinit var getAllLaunchPadUseCase: GetAllTextLaunchPadUseCase
		private lateinit var getAllSearchedLaunchPadUseCase: GetSearchedLaunchPadUseCase
		private lateinit var saveLaunchPadUseCase: SaveLaunchPadUseCase
		private lateinit var launchPadViewModel: LaunchPadViewModel

		private lateinit var historyLaunchPadAdapter: HistoryLaunchPadAdapter
		private lateinit var dialogBuilder: MaterialDialog

		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentHistoryLaunchpadBinding.inflate(inflater)
				setUpViewModel()

				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				super.onViewCreated(view, savedInstanceState)
				setupLaunchPadAdapter()
				lifecycleScope.launchWhenStarted {
						launchPadViewModel.getAllLaunchPad
								?.collect {
										if (it.isNotEmpty()) {
												showOrHideNoData(false)
												historyLaunchPadAdapter.submitList(it)
										} else showOrHideNoData(true)
								}
				}
				binding.btnTest.setOnClickListener {
						openDeleteAllDataDialog()
				}

				launchPadViewModel.currentQueryText.observe(viewLifecycleOwner, { query ->
						lifecycleScope.launchWhenStarted {
								launchPadViewModel.getSearchedLaunchPad(query)?.collect {
										if(it.isNotEmpty()){
												showOrHideNoData(false)
												historyLaunchPadAdapter.submitList(it)
										}else showOrHideNoData(true)

								}
						}
				})

				launchPadViewModel.deleteAllLaunchPadState.observe(viewLifecycleOwner, {
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
				var job: Job?=null
				binding.edtSearch.addTextChangedListener { editable ->
						job?.cancel()
						job = lifecycleScope.launchWhenStarted {
								delay(Util.SEARCH_DEFAULT_DELAY)
								if (editable != null) {
										launchPadViewModel.currentQueryText.value = editable.toString()
								}
						}
				}
		}


		private fun openDeleteAllDataDialog() {
				dialogBuilder = MaterialDialog.Builder(requireActivity())
						.setTitle("Xóa ?", TextAlignment.CENTER)
						.setMessage("Bạn chắc chắn xóa toàn bộ hình vẽ ?", TextAlignment.CENTER)
						.setCancelable(false)
						.setPositiveButton(
								"OK", R.drawable.ic_delete_dialog
						) { dialogInterface, which ->
								launchPadViewModel.deleteAllLaunchPad()
								dialogInterface.dismiss()
						}
						.setNegativeButton(
								"Hủy", R.drawable.ic_close
						) { dialogInterface, which -> dialogInterface.dismiss() }
						.setAnimation(R.raw.delete)
						.build()
				dialogBuilder.show()
		}

		private val receiver = object: BroadcastReceiver(){
				override fun onReceive(context: Context?, intent: Intent?) {
						if (intent != null) {
								if(intent.action== LaunchPadActivity.ACTION_RESET_LAUNCHPAD){
										lifecycleScope.launch{
												launchPadViewModel.getAllLaunchPad
														?.collect {
																if (it.isNotEmpty()) {
																		withContext(Dispatchers.Main){
																				showOrHideNoData(false)
																		}
																		historyLaunchPadAdapter.submitList(it)
																} else withContext(Dispatchers.IO){
																		showOrHideNoData(true)
																}
														}
										}
								}
						}
				}
		}

		override fun onResume() {
				val intentFilter= IntentFilter(LaunchPadActivity.ACTION_RESET_LAUNCHPAD)
				requireActivity().registerReceiver(receiver,intentFilter)
				super.onResume()
		}

		override fun onDestroy() {
				requireActivity().unregisterReceiver(receiver)
				super.onDestroy()
		}

		private fun setupLaunchPadAdapter() {
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
								val item = historyLaunchPadAdapter.getItemAtPosition(position)
								launchPadViewModel.deleteLaunchPad(item)
								launchPadViewModel.deleteLaunchPadState.observe(viewLifecycleOwner, {
										it?.let {
												if (it) {
														Snackbar.make(
																binding.root,
																"Bạn đã xóa hình vẽ  này",
																Snackbar.LENGTH_LONG,
														).apply {
																setAction("Khôi phục") {
																		launchPadViewModel.saveLaunchPad(item)
																}
														}.show()
												} else {
														Snackbar.make(
																binding.root,
																"Bạn đã xóa hình vẽ  này thất bại",
																Snackbar.LENGTH_LONG,
														)
																.show()
												}
										}
								})
						}

				}
				historyLaunchPadAdapter = HistoryLaunchPadAdapter()
				binding.recyclerView.apply {
						adapter = historyLaunchPadAdapter
						layoutManager = LinearLayoutManager(requireContext())
						itemAnimator = null
				}
				ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.recyclerView)
				historyLaunchPadAdapter.setOnItemClickListener {
						val intent = Intent(requireContext(),LaunchPadActivity::class.java)
						intent.putExtra("data",it)
						startActivity(intent)
						requireActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_right)
				}
		}


		private fun setUpViewModel(){
				localDataBase = LocalDataBase(requireContext())
				localDataRepository = LocalDataRepositoryImpl(localDataBase.getDao())
				appRepository = AppRepositoryImpl(null,localDataRepository)
				saveLaunchPadUseCase = SaveLaunchPadUseCase(appRepository)
				deleteAllLaunchPadUseCase = DeleteAllLaunchPadUseCase(appRepository)
				deleteLaunchPadUseCase = DeleteLaunchPadUseCase(appRepository)
				getAllLaunchPadUseCase = GetAllTextLaunchPadUseCase(appRepository)
				getAllSearchedLaunchPadUseCase = GetSearchedLaunchPadUseCase(appRepository)

				launchPadViewModel = ViewModelProvider(requireActivity(),LaunchPadViewModelFactory(
						requireActivity().application,
						saveLaunchPadUseCase=saveLaunchPadUseCase,
						deleteAllLaunchPadUseCase = deleteAllLaunchPadUseCase,
						deleteLaunchPadUseCase = deleteLaunchPadUseCase,
						getAllTextLaunchPadUseCase = getAllLaunchPadUseCase,
						getSearchedLaunchPadUseCase = getAllSearchedLaunchPadUseCase,
						)
				).get(LaunchPadViewModel::class.java)
		}

		override fun onDestroyView() {
				launchPadViewModel.currentQueryText.value=""
				launchPadViewModel.deleteLaunchPadState.value = null
				launchPadViewModel.setNullForDeleteAllTextAndColorState()
				super.onDestroyView()
		}

		private fun showOrHideNoData(isShow:Boolean){
				binding.lyContent.visibility = if(isShow) View.GONE else View.VISIBLE
				binding.imgNoData.visibility = if(isShow) View.VISIBLE else View.GONE
		}

}
