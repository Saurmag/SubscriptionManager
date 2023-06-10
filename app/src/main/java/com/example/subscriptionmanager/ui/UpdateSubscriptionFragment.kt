package com.example.subscriptionmanager.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.subscriptionmanager.R
import com.example.subscriptionmanager.databinding.UpdateSubscriptionFragmentBinding
import com.example.subscriptionmanager.model.Subscription
import com.example.subscriptionmanager.viewmodels.UpdateSubscriptionViewModel
import com.example.subscriptionmanager.viewmodels.UpdateSubscriptionViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

private const val UPDATE_SUBSCRIPTION = "UPDATE_SUBSCRIPTION"

class UpdateSubscriptionFragment : Fragment() {

    private var _binding : UpdateSubscriptionFragmentBinding? = null
    private val binding : UpdateSubscriptionFragmentBinding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null."
        }

    private val args : UpdateSubscriptionFragmentArgs by navArgs()

    private val updateSubscriptionViewModel : UpdateSubscriptionViewModel by viewModels {
        UpdateSubscriptionViewModelFactory(args.subscriptionID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {

                binding.apply {

                    if ((editPrice.text.isBlank()) || (editPrice.text.toString() == "0") || (editName.text.isBlank())) {

                        Toast.makeText(context, "Проверьте введенные вами данные", Toast.LENGTH_SHORT).show()

                    } else {

                        findNavController().popBackStack(R.id.updateSubscriptionFragment, true)
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UpdateSubscriptionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            editName.doOnTextChanged { text, _, _, _ ->

                updateSubscriptionViewModel.updateSubscription { subscription ->

                    subscription.copy(title  = text.toString())
                }
            }

            editPrice.doOnTextChanged { text, _, _, _ ->

                if (text.toString().isNotBlank()) {

                    updateSubscriptionViewModel.updateSubscription { subscription ->

                        subscription.copy(price = text.toString().toInt())
                    }
                }
            }

            addButton.setOnClickListener {

                if ((editPrice.text.isBlank()) || (editPrice.text.toString() == "0") || (editName.text.isBlank())) {

                    Toast.makeText(context, "Check the data you entered", Toast.LENGTH_SHORT).show()

                } else {

                    findNavController().popBackStack(R.id.updateSubscriptionFragment, true)
                }
            }

            deleteButton.setOnClickListener {

                deleteSubscription(args.subscriptionID)

                findNavController().popBackStack(R.id.updateSubscriptionFragment, true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){

                updateSubscriptionViewModel.subscription.collect{ subscription ->

                    subscription?.let {

                        updateUi(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUi(subscription: Subscription){

        binding.apply {

            if(editName.text.toString() != subscription.title){

                editName.setText(subscription.title)
            }

            if ((editPrice.text.isBlank())
                && (editPrice.text.toString() != subscription.price.toString())
                && (subscription.price != 0)) {

                editPrice.setText(subscription.price.toString())
            }

            Log.d(UPDATE_SUBSCRIPTION, "VALUES: TITLE = ${subscription.title} ; PRICE = ${subscription.price} ; UUID = ${subscription.id}")
        }
    }

    private fun deleteSubscription(id: UUID) {

        viewLifecycleOwner.lifecycleScope.launch {

            updateSubscriptionViewModel.deleteSubscription(id)
        }

        findNavController().popBackStack(R.id.createSubscriptionFragment, true)
    }
}