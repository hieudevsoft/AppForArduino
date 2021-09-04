package com.devapp.appforarduino.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.databinding.FragmentTextBinding
import com.devapp.appforarduino.ui.activities.HomeActivity
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.util.ColorSheetSingleTon
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextFragment : Fragment(R.layout.fragment_text) {
    private var _binding: FragmentTextBinding? = null
    private lateinit var model: HomeViewModel
    private lateinit var connectivityManager:ConnectivityManager
    private val binding get() = _binding?.let { it }!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTextBinding.inflate(inflater)
        model = (activity as HomeActivity).homeViewModel
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return binding.root
    }

    override fun onResume() {
        connectivityManager.registerDefaultNetworkCallback(object:ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                requireActivity().runOnUiThread {
                    showOrHideNoInternet(false)
                }
                super.onAvailable(network)
            }

            override fun onLost(network: Network) {
                requireActivity().runOnUiThread{
                    showOrHideNoInternet(true)
                }
                super.onLost(network)
            }

            override fun onUnavailable() {
                requireActivity().runOnUiThread{
                    showOrHideNoInternet(true)
                    Snackbar.make(binding.root,"Không hỗ trợ internet",Snackbar.LENGTH_LONG).show()
                }
                super.onUnavailable()
            }
        })
        super.onResume()
    }

    private fun showOrHideNoInternet(isShow:Boolean){
        binding.lyContent.visibility = if(isShow) View.GONE else View.VISIBLE
        binding.lyNoInternetConnect.visibility = if(isShow) View.VISIBLE else View.GONE
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribesObserver()
        binding.btnChooseColor.setOnClickListener {
            openBottomSheetColor()
        }
        binding.btnPush.setOnClickListener {
            val intColor = binding.edtText.currentTextColor
            val textData = TextData(
                binding.edtText.text.toString(),
                String.format(
                    "#%06X", (0xFFFFFF and intColor)
                )
            )
            model.updateTextAndColorToFireBase(textData)
            model.saveTextAndColor(textData)
        }
    }

    private fun subscribesObserver() {
        model.stateTextEditText.observe(viewLifecycleOwner, {
            it?.let {
                binding.edtText.setText(it[0])
                it[1]?.let { it1 -> binding.edtText.setTextColor(it1.toInt()) }
            }
        })
        lifecycleScope.launchWhenStarted {
            model.updateState.collect {
                when(it){
                    is HomeViewModel.UpdateState.Loading->{
                        withContext(Dispatchers.Main){
                            binding.loadingDots.visibility = View.VISIBLE
                        }

                    }
                    is HomeViewModel.UpdateState.Error->{
                        withContext(Dispatchers.Main){
                            binding.loadingDots.visibility = View.GONE
                            Snackbar.make(binding.root,it.message,Snackbar.LENGTH_LONG).show()
                        }
                    }
                    is HomeViewModel.UpdateState.Success->{
                        binding.loadingDots.visibility = View.GONE
                        Snackbar.make(binding.root,"Cập nhật chữ thành công",Snackbar.LENGTH_LONG).show()
                        model.setEmptyForUpdateState()
                    }
                    else->{

                    }
                }
            }
        }
    }

    private fun openBottomSheetColor() {
        val colorSheetSingleTon = ColorSheetSingleTon.getInstance(requireContext())
        colorSheetSingleTon.setListener(object : ColorSheetSingleTon.ColorSheetListener {
            override fun onColorSelected(color: Int) {
                binding.edtText.setTextColor(color)

            }
        })
        colorSheetSingleTon.createColorSheet(resources.getIntArray(R.array.ColorSource))
        colorSheetSingleTon.show(childFragmentManager)
    }

    override fun onDestroyView() {
        model.setStateText(
            hashMapOf(
                0 to binding.edtText.text.toString(),
                1 to binding.edtText.currentTextColor.toString()
            )
        )
        _binding = null
        model.setEmptyForUpdateState()
        super.onDestroyView()
    }
}