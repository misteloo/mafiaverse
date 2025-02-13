package ir.greendex.mafia.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.MainActivity
import ir.greendex.mafia.databinding.FragmentTransactionBinding
import ir.greendex.mafia.util.base.BaseFragment
import javax.inject.Inject

@AndroidEntryPoint
class TransactionFragment : BaseFragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding
    private val vm: TransactionVm by viewModels()

    // injection
    @Inject
    lateinit var adapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // getList
        transactions()

        // init views
        initViews()
    }

    private fun transactions() {
        MainActivity.userToken?.let {
            vm.getTransactionHistory(it)
        }
    }

    private fun initViews() {
        binding?.apply {
            // back
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter

            // transactions
            vm.transactionHistory.observe(viewLifecycleOwner) {
                adapter.modifierItem(newItem = it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}