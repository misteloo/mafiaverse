package ir.greendex.mafia.ui.edit_profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.R
import ir.greendex.mafia.databinding.FragmentEditAnimBsBinding
import ir.greendex.mafia.entity.edit_profile.UserItemsEntity
import ir.greendex.mafia.ui.edit_profile.adapter.EditAnimAdapter
import ir.greendex.mafia.ui.edit_profile.vm.EditAnimBsVm
import ir.greendex.mafia.util.BASE_URL
import ir.greendex.mafia.util.extension.hideLoading
import ir.greendex.mafia.util.extension.loading
import javax.inject.Inject

@AndroidEntryPoint
class EditAnimBsFragment(private val currentAnim: String) : BottomSheetDialogFragment() {
    private var _binding: FragmentEditAnimBsBinding? = null
    private val binding get() = _binding
    private val vm: EditAnimBsVm by viewModels()
    private lateinit var selectedAnim: UserItemsEntity.UserItemData.UserItemsList

    // injection
    @Inject
    lateinit var adapter: EditAnimAdapter

    override fun getTheme() = R.style.BottomSheetDialogThemeNoFloating

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAnimBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // init
        initViews()

        // get items
        getItems()

    }

    private fun getItems() {
        MainActivity.userToken?.let {
            vm.getUserItems(type = "animation", token = it)
        }
    }

    private fun initViews() {
        binding?.apply {
            // current
            animPrv.setAnimationFromUrl(BASE_URL.plus(currentAnim))
            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = adapter

            // observing
            vm.getUserItemLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.modifierItem(newItem = it)
                } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
            }

            // activating
            adapter.onActiveCallback { item ->
                // prv
                animPrv.setAnimationFromUrl(BASE_URL.plus(item.file))
                // bind
                selectedAnim = item

                btnSave.also {
                    it.isEnabled = true
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red500
                        )
                    )
                }

            }

            // save
            btnSave.setOnClickListener {
                if (::selectedAnim.isInitialized) {
                    MainActivity.userToken?.let { token ->
                        btnSave.loading(progress = progress)
                        vm.saveChange(id = selectedAnim.id, token = token) {
                            btnSave.hideLoading(progress = progress)
                            if (it != null) {
                                if (it.status) {
                                    onSaved?.let {
                                        it(selectedAnim.file)
                                    }
                                    dismiss()
                                }
                            } else Toast.makeText(context, "عدم ارتباط", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private var onSaved: ((String) -> Unit)? = null
    fun onSavedCallback(it: (String) -> Unit) {
        onSaved = it
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}