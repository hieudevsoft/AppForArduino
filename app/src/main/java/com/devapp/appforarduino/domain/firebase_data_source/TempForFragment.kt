package com.devapp.appforarduino.domain.firebase_data_source

import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.util.Util
import com.google.android.material.snackbar.Snackbar

class TempForFragment {
//    private fun subscriberObservers() {
//        lifecycleScope.launchWhenStarted {
//            homeViewModel.updateState.collect {
//                when (it) {
//                    is HomeViewModel.UpdateState.Success ->
//                        Snackbar.make(
//                            binding.root,
//                            "Transfer content successfully",
//                            Snackbar.LENGTH_LONG
//                        ).show()
//                    is HomeViewModel.UpdateState.Error->
//                        if(it.message== Util.EVENT_STATE_NOT_INTERNET_CONNECTTED){
//                            Toast.makeText(applicationContext, Util.EVENT_STATE_NOT_INTERNET_CONNECTTED, Toast.LENGTH_SHORT).show()
//                        }else {
//                            Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
//                        }
//
//
//                    is HomeViewModel.UpdateState.Loading->{
//                        Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//            }
//        }
//    }

}