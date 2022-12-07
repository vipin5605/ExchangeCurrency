package com.assessment.exchangecurrency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.assessment.exchangecurrency.customviews.ItemListDialogFragment
import com.assessment.exchangecurrency.databinding.FragmentExchangeCurrencyHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeCurrencyHomeFragment : Fragment(), ItemListDialogFragment.onItemSelected {

    private var toCurrencyListDlg: ItemListDialogFragment? = null
    private  var fromCurrencyListDlg: ItemListDialogFragment? = null
    private lateinit var binding: FragmentExchangeCurrencyHomeBinding
    private val viewModel: MainActivityViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExchangeCurrencyHomeBinding.inflate(layoutInflater)

        viewModel.getCountries()
        viewModel.countriesList.observe(viewLifecycleOwner) {

            if (it != null && it.isNotEmpty()) {
                binding.txtFromCurrency.text = it[0]

                if (it.size > 1) {
                    binding.txtToCurrency.text = it[1]
                }
            }
        }
        binding.txtFromCurrency.setOnClickListener{

            if (viewModel.countriesList.value != null && viewModel.countriesList.value!!.isNotEmpty()) {
                toCurrencyListDlg = null
                fromCurrencyListDlg = ItemListDialogFragment.newInstance(viewModel.countriesList.value!!, this)
                fromCurrencyListDlg!!.show(childFragmentManager, "TAG")
            }

        }

        binding.txtToCurrency.setOnClickListener{

            if (viewModel.countriesList.value != null && viewModel.countriesList.value!!.isNotEmpty()) {
                fromCurrencyListDlg = null
                toCurrencyListDlg = ItemListDialogFragment.newInstance(viewModel.countriesList.value!!, this)
                toCurrencyListDlg!!.show(childFragmentManager, "TAG")
            }
        }

        binding.button.setOnClickListener {

            if (binding.editTextNumber.text.isNotEmpty()
                && binding.editTextNumber.text.toString().toLong() > 0) {
                viewModel.convert(binding.editTextNumber.text.toString(), binding.txtFromCurrency.text.toString(), binding.txtToCurrency.text.toString())
                findNavController().navigate(R.id.action_exchangeCurrencyHomeFragment_to_exchangeConfirmFragment)

            } else {
                Toast.makeText(context, "Please enter a valid number!", Toast.LENGTH_SHORT).show()
            }

        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)


        return binding.root
    }


    override fun OnSelected(code: String) {
        if (fromCurrencyListDlg != null) {
            fromCurrencyListDlg!!.dismiss()
            binding.txtFromCurrency.text = code
        }

        if (toCurrencyListDlg != null) {
            toCurrencyListDlg!!.dismiss()
            binding.txtToCurrency.text = code
        }
    }

}