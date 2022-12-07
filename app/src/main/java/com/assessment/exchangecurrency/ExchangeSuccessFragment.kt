package com.assessment.exchangecurrency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.assessment.exchangecurrency.databinding.FragmentExchangeSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeSuccessFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentExchangeSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exchange_success, container, false)

        binding.txtSuccessTitle.text = getString(R.string.lbl_success, viewModel.targetAmount.value.toString(), viewModel.targetCurr.value.toString())

        val rateFinal = 1/(viewModel.ratesEntity.value?.value!!)
        val rate:Double = String.format("%.2f", rateFinal).toDouble()

        binding.txtSuccessDesc.text = getString(R.string.lbl_success_desc, "1/$rate")

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_exchangeSuccessFragment_to_exchangeCurrencyHomeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        return binding.root
    }
}