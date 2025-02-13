package ir.greendex.mafia.game.general

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.greendex.mafia.databinding.FragmentMafiaVisitationBsBinding
import ir.greendex.mafia.entity.game.general.InGameUsersDataEntity
import ir.greendex.mafia.entity.game.nato.NatoMafiaVisitationEntity
import ir.greendex.mafia.game.adapter.general.NatoMafiaVisitationAdapter

@AndroidEntryPoint
class MafiaVisitationBsFragment(
    private val mafiaList: List<NatoMafiaVisitationEntity>,
    private val users: List<InGameUsersDataEntity.InGameUserData>
) : BottomSheetDialogFragment() {

    private var _binding: FragmentMafiaVisitationBsBinding? = null
    private val binding get() = _binding
    private val rvList by lazy { mutableListOf<InGameUsersDataEntity.InGameUserData>() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMafiaVisitationBsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED

        // initViews
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            mafiaList.forEach {
                users.find { find ->
                    it.userId == find.userId
                }?.let { let ->
                    rvList.add(let)
                }
            }

            // rv
            rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            rv.adapter = NatoMafiaVisitationAdapter(users = rvList,mafia = mafiaList)

            object :CountDownTimer(5000,1000){
                override fun onTick(millisUntilFinished: Long) {
                    txtCounter.text = (millisUntilFinished / 1000L).toFloat().toInt().toString()
                }

                override fun onFinish() {
                    txtCounter.text = "0"
                    dismiss()
                }

            }.start()
        }


    }


}