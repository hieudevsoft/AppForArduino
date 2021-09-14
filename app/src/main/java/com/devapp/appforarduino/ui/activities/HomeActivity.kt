package com.devapp.appforarduino.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.db.LocalDataBase
import com.devapp.appforarduino.databinding.ActivityHomeBinding
import com.devapp.appforarduino.domain.app_repository.AppRepository
import com.devapp.appforarduino.domain.app_repository.AppRepositoryImpl
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseRepository
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseRepositoryImpl
import com.devapp.appforarduino.domain.firebase_data_source.FireBaseService
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepository
import com.devapp.appforarduino.domain.local_data_source.LocalDataRepositoryImpl
import com.devapp.appforarduino.domain.usecases.*
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.ui.viewmodels.HomeViewModelFactory
import com.devapp.appforarduino.util.Util.deselectAllItems
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.FirebaseDatabase



class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var firebaseService: FireBaseService
    private lateinit var firebaseRepository: FireBaseRepository
    private lateinit var localDataBase: LocalDataBase
    private lateinit var localDataRepository: LocalDataRepository
    private lateinit var appRepository: AppRepository
    private lateinit var updateTextAndColorToFirebaseUseCase: UpdateTextAndColorToFirebaseUseCase
    private lateinit var updateOptionUseCase: UpdateOptionUseCase
    private lateinit var saveTextAndColorUseCase: SaveTextAndColorUseCase
    private lateinit var deleteTextAndColorUseCase: DeleteTextAndColorUseCase
    private lateinit var deleteAllTextAndColorUseCase: DeleteAllTextAndColorUseCase
    private lateinit var searchedTextAndColorUseCase: GetSearchedTextAndColorUseCase
    private lateinit var getAllTextAndColorUseCase: GetAllTextAndColorUseCase
    lateinit var homeViewModel: HomeViewModel

    private lateinit var mGestureDetector: GestureDetector
    private lateinit var tvOptionText: TextView
    private lateinit var tvOptionImage: TextView
    private lateinit var tvOptionLaunchPad: TextView
    private lateinit var tvCancel: TextView
    var mBottomSheetBehavior: BottomSheetBehavior<View>? = null

    companion object{
        var optionFeature = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        setupInitUpdateTextAndColor()
        mappingElement()
        setupNavBottom()
        intStateClickEvent()
        initGestureDetector()
    }

    private fun mappingElement() {
        tvOptionText = findViewById(R.id.tvOptionText)
        tvOptionImage = findViewById(R.id.tvOptionImage)
        tvOptionLaunchPad = findViewById(R.id.tvOptionDrawLed)
        tvCancel = findViewById(R.id.tv3)
    }

    private fun initGestureDetector() {
        mGestureDetector = GestureDetector(this,
            object : SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (mBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                        return true
                    }
                    return super.onSingleTapConfirmed(e)
                }
            })
    }

    private fun setupInitUpdateTextAndColor() {
        firebaseService = FireBaseService(FirebaseDatabase.getInstance())
        firebaseRepository = FireBaseRepositoryImpl(firebaseService)
        localDataBase = LocalDataBase(this)
        localDataRepository = LocalDataRepositoryImpl(localDataBase.getDao())
        appRepository = AppRepositoryImpl(firebaseRepository, localDataRepository)
        updateTextAndColorToFirebaseUseCase = UpdateTextAndColorToFirebaseUseCase(appRepository)
        saveTextAndColorUseCase = SaveTextAndColorUseCase(appRepository)
        updateOptionUseCase = UpdateOptionUseCase(appRepository)
        deleteTextAndColorUseCase = DeleteTextAndColorUseCase(appRepository)
        deleteAllTextAndColorUseCase = DeleteAllTextAndColorUseCase(appRepository)
        searchedTextAndColorUseCase = GetSearchedTextAndColorUseCase(appRepository)
        getAllTextAndColorUseCase = GetAllTextAndColorUseCase(appRepository)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                application,
                updateTextAndColorToFirebaseUseCase,
                saveTextAndColorUseCase,
                deleteTextAndColorUseCase,
                deleteAllTextAndColorUseCase,
                getAllTextAndColorUseCase,
                searchedTextAndColorUseCase,
                updateOptionUseCase
            )
        ).get(HomeViewModel::class.java)
    }

    private fun setupNavBottom() {
        binding.navBottom.deselectAllItems()
        mBottomSheetBehavior =
            BottomSheetBehavior.from(findViewById(R.id.layout_bottomSheetBehavior))
        mBottomSheetBehavior!!.peekHeight = 0
        binding.navBottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_history -> {
                    optionFeature = 1
                    openBottomDialogHistory()
                }
                R.id.menu_infor -> openFragmentInfor()
                else -> false
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event)
    }

    private fun openBottomDialogHistory(): Boolean {
        if (mBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED;
        }
        mBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.navBottom.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                } else {
                    binding.navBottom.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        return true
    }

    private fun intStateClickEvent() {
        tvOptionText.setOnClickListener {
            if(optionFeature==1)
            openFragmentHistoryText() else
                openFragmentText()
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        tvOptionImage.setOnClickListener {
            if(optionFeature==1)
                openFragmentHistoryImage()
            else openFragmentImage()
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        tvOptionLaunchPad.setOnClickListener {
            if(optionFeature==1)
                openFragmentHistoryLaunchPad()
            else openActivityLaunchPad()
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        tvCancel.setOnClickListener {
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.fab.setOnClickListener {
            optionFeature = 0
            openBottomDialogHistory()
        }
    }

    private fun openFragmentImage() {
        navHostFragment.findNavController().navigate(R.id.action_global_imageFragment)
    }

    private fun openFragmentHistoryImage() {

    }

    private fun openActivityLaunchPad() {
        Intent(this,LaunchPadActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_left)
        }
    }

    private fun openFragmentHistoryLaunchPad() {
        navHostFragment.findNavController().navigate(R.id.action_global_historyLaunchPadFragment)
    }

    private fun openFragmentText() {
        navHostFragment.findNavController().navigate(R.id.action_global_textFragment)
    }

    private fun openFragmentHistoryText() {
        navHostFragment.findNavController().navigate(R.id.action_global_historyText)
    }

    private fun openFragmentInfor(): Boolean {
        navHostFragment.findNavController().navigate(R.id.action_global_informationFragment)
        return true
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
}