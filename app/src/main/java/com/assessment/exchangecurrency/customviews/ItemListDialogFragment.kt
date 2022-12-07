package com.assessment.exchangecurrency.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assessment.exchangecurrency.databinding.FragmentItemListDialogListDialogBinding
import com.assessment.exchangecurrency.databinding.FragmentItemListDialogListDialogItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val ARG_ITEM_COUNT = "country_list"

class ItemListDialogFragment(private var itemSelectedListener: onItemSelected) : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager =
            LinearLayoutManager(context)
        binding.list.adapter =
            arguments?.getStringArrayList(ARG_ITEM_COUNT)?.let { ItemAdapter(it) }
    }

    interface onItemSelected {

        fun OnSelected(code:String)
    }


    private inner class ViewHolder internal constructor(binding: FragmentItemListDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal val text: TextView = binding.text
    }

    private inner class ItemAdapter internal constructor(private val mItemCount: ArrayList<String>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(FragmentItemListDialogListDialogItemBinding.inflate(LayoutInflater.from(
                parent.context), parent, false))

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = mItemCount[position]

            holder.text.setOnClickListener {
                if (itemSelectedListener != null) {
                    itemSelectedListener.OnSelected(mItemCount[position])
                }
            }
        }

        override fun getItemCount(): Int {
            return mItemCount.size
        }
    }

    companion object {

        fun newInstance(itemCount: ArrayList<String>, onItemSelected: onItemSelected): ItemListDialogFragment =
            ItemListDialogFragment(onItemSelected).apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}