package com.example.subscriptionmanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.subscriptionmanager.R
import com.example.subscriptionmanager.databinding.CreateSubscriptionFragmentBinding
import com.example.subscriptionmanager.model.Subscription
import com.example.subscriptionmanager.viewmodels.CreateSubscriptionViewModel
import com.example.subscriptionmanager.viewmodels.CreateSubscriptionViewModelFactory
import com.example.subscriptionmanager.viewmodels.SubscriptionListViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "CREATE_SUBSCRIPTION_FRAGMENT"

class CreateSubscriptionFragment : Fragment() {

    private var _binding: CreateSubscriptionFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null."
        }

    //Bad idea, maybe
    private val viewModelForAddSubscription: SubscriptionListViewModel by viewModels()

    private val subscription = Subscription(
        id = UUID.randomUUID(),
        title = "",
        price = 0
    )

    private val viewModel: CreateSubscriptionViewModel by viewModels {
        CreateSubscriptionViewModelFactory(subscriptionID = subscription.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModelForAddSubscription.addSubscription(subscription = subscription)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateSubscriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomReconfiguration()

        binding.apply {

            editName.doOnTextChanged { text, _, _, _ ->
                viewModel.updateSubscription { oldSubscription ->
                    oldSubscription.copy(title = text.toString())
                }
            }

            editPrice.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotBlank()) {
                    viewModel.updateSubscription { oldSubscription ->
                        oldSubscription.copy(price = text.toString().toInt())
                    }
                }
            }

            addButton.setOnClickListener {
                if (viewModel.isValid()) {
                    viewModel.isAdd = true
                    val navController = findNavController()
                    addButtonNavigation(navController)
                } else {
                    Toast.makeText(context, "Check the data you entered", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.subscription.collect { currentSubscription ->
                    currentSubscription?.let {
                        updateUi(it)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!viewModel.isValid() || !viewModel.isAdd) {
            Log.d(TAG, "THIS SUBSCRIPTION WILL BE DELETED - ${viewModel.subscription.value}")
            defaultDeleteSubscription(viewModel.subscription.value?.id!!)
            Log.d(TAG, "THIS SUBSCRIPTION WAS DELETED - ${viewModel.subscription.value}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.d("ON_DESTROY | $TAG", "CURRENT SUBSCRIPTION - ${subscription.id}")
        Log.d(TAG, "$viewModel")

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController()

        bottomNavigationView?.setupWithNavController(navController)
        _binding = null
    }

    private fun updateUi(subscription: Subscription) {
        Log.d("$TAG | COLLECT SUBSCRIPTION UUID", "UUID - ${subscription.id}")
        Log.d("$TAG | COLLECT SUBSCRIPTION", "TITLE - ${subscription.title} | PRICE - ${subscription.price}")
    }

    private fun addButtonNavigation(navController: NavController) {

        val startDestination = navController.graph.startDestinationId
        val previousDestination = navController.previousBackStackEntry?.destination?.id

        if (previousDestination != startDestination) {

            navController.navigate(
                R.id.subscriptionsListFragment, null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.profileFragment, true)
                    .build()
            )
        }
        else {
            navController.popBackStack(R.id.createSubscriptionFragment, true)
        }
    }

    private fun defaultDeleteSubscription(id: UUID){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteSubscription(id)
        }
    }

    private fun bottomReconfiguration() {

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView?.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {

                R.id.subscriptionsListFragment -> {

                    findNavController().popBackStack(R.id.createSubscriptionFragment, true)
                    true
                }

                R.id.createSubscriptionFragment -> {

                    findNavController().navigate(R.id.createSubscriptionFragment)
                    true
                }

                R.id.profileFragment -> {

                    findNavController().navigate(R.id.profileFragment, null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.subscriptionsListFragment, true)
                            .build()
                    )
                    true
                }
                else -> false
            }
        }
    }
}