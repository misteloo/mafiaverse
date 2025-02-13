package ir.greendex.mafia.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentLearnBinding
import ir.greendex.mafia.entity.routing.Routing
import ir.greendex.mafia.util.base.BaseFragment

@AndroidEntryPoint
class LearnFragment : BaseFragment() {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding
    private val vm: LearnVm by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // store routing
        storeRouting()

        // characters
        vm.addCharacters()
    }

    private fun storeRouting() {
        vm.storeRouting(route = Routing.LEARN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init views
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            // back
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            vm.getCharacters.observe(viewLifecycleOwner) {
                rv.adapter = LearnAdapter(items = it)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}