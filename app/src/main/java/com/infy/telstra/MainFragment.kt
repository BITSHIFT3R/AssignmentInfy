package com.infy.telstra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.widget.Toast

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infy.telstra.adapter.FactAdapter
import com.infy.telstra.databinding.ActivityMainFragBinding
import com.infy.telstra.utlity.checkInternet
import com.infy.telstra.viewModel.FactViewModel


class MainFragment : Fragment() {
    private lateinit var binding: ActivityMainFragBinding;
    private lateinit var factViewModel: FactViewModel

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_main_frag, container, false)
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        factViewModel = ViewModelProvider(
            requireActivity()
        ).get(FactViewModel::class.java)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.rvFacts.layoutManager = layoutManager

        refreshRecords()
        binding.swipe.setOnRefreshListener {
            factViewModel.loadData()
            refreshRecords()
        }
        loadData()
    }

    private fun refreshRecords() {
        callAPI()
        binding.swipe.isRefreshing = true

    }

    private fun loadData() {


        factViewModel.getAllFacts?.observe(requireActivity(), {
            if (it != null) {
                val factList = it
                binding.swipe.isRefreshing = false
                factList.let {
                    binding.rvFacts.apply {
                        adapter = FactAdapter(factList)
                    }
                    getActivity()!!.setTitle(factViewModel.title);

                }
            } else {
                binding.swipe.isRefreshing = false
                Toast.makeText(activity, getString(R.string.no_data), Toast.LENGTH_LONG).show()
            }
        })


    }

    fun callAPI() {
        if (checkInternet(requireContext())) {
            factViewModel.loadData()
        }
    else
    {
        binding.swipe.isRefreshing = false
        Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_LONG).show()
        factViewModel.getAllFacts()
    }
}

}
