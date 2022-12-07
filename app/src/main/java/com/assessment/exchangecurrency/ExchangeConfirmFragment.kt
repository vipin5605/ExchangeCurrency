package com.assessment.exchangecurrency

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.assessment.exchangecurrency.databinding.FragmentExchangeConfirmBinding
import com.assessment.exchangecurrency.utils.LifecycleAwareTimer
import com.assessment.exchangecurrency.utils.TimerCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeConfirmFragment : Fragment(), TimerCallback {


    private lateinit var binding: FragmentExchangeConfirmBinding
    private val viewModel: MainActivityViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exchange_confirm, container, false)
        binding.button.setOnClickListener {
            basicAlert(it)
        }

        viewModel.sourceAmount.observe(viewLifecycleOwner, Observer {

            binding.txtSourceCurrency.text = it.toString()
        })

        viewModel.targetAmount.observe(viewLifecycleOwner, Observer {

            binding.txtDestCurrency.text = it.toString()
        })


        startTimer()



        return binding.root
    }

    private var timer: LifecycleAwareTimer? = null

    fun startTimer(){
        timer?.discardTimer()
        timer = LifecycleAwareTimer(30000, 1000, this)
        timer?.startTimer()
    }

    override fun onTick(millisUntilFinished: Long) {
        /** seconds ticking **/
        binding.txtTimeLeft.text = (millisUntilFinished / 1000).toString()

    }
    override fun onTimeOut() {
        /** expired**/
        findNavController().navigate(R.id.action_exchangeConfirmFragment_to_exchangeCurrencyHomeFragment)

    }

    fun basicAlert(view: View){

        val builder = AlertDialog.Builder(activity)

        with(builder)
        {
            setTitle(getString(R.string.lbl_dlg_title))
            var desc = getString(R.string.lbl_dlg_desc, viewModel.targetAmount.value.toString(), viewModel.targetCurr.value.toString(), viewModel.sourceAmount.value.toString(), viewModel.sourceCurr.value.toString())
            setMessage(desc)
            setPositiveButton(getString(R.string.lbl_approve), DialogInterface.OnClickListener(positiveButtonClick))
            setNegativeButton(getString(R.string.lbl_cancel), negativeButtonClick)
            show()
        }


    }
    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
        findNavController().navigate(R.id.action_exchangeConfirmFragment_to_exchangeSuccessFragment)
    }
    private val negativeButtonClick = { dialog: DialogInterface, which: Int ->

        dialog.dismiss()
    }
}