package ir.greendex.mafia.ui.group.tap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentGroupGameHistoryBinding

@AndroidEntryPoint
class GroupGameHistoryFragment : Fragment() {
    private var _binding: FragmentGroupGameHistoryBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupGameHistoryBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // views
        initViews()
    }

    private fun initViews() {
        binding?.apply {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}