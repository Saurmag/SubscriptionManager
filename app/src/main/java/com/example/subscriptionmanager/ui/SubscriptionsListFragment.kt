package com.example.subscriptionmanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.subscriptionmanager.databinding.SubscriptionListFragmentBinding
import com.example.subscriptionmanager.viewmodels.SubscriptionListViewModel
import kotlinx.coroutines.launch

class SubscriptionsListFragment : Fragment() {

    private var _binding: SubscriptionListFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null."
        }

    private val subscriptionViewModel: SubscriptionListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SIZE", "Size = ${subscriptionViewModel.subscriptions.value.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SubscriptionListFragmentBinding.inflate(inflater, container, false)

        binding.subscriptionRecyclerView.layoutManager = GridLayoutManager(context, 2)

        binding.subscriptionRecyclerView.addItemDecoration(SubscriptionMargin())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                subscriptionViewModel.subscriptions.collect{ subscriptions ->

                    binding.subscriptionRecyclerView.adapter = SubscriptionsAdapter(subscriptions) { subscriptionID ->

                        findNavController().navigate(

                            SubscriptionsListFragmentDirections.showUpdateSubscription(subscriptionID)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}