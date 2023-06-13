package com.example.subscriptionmanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.subscriptionmanager.R
import com.example.subscriptionmanager.databinding.FragmentProfileBinding
import com.example.subscriptionmanager.viewmodels.ProfileFragmentUiState
import com.example.subscriptionmanager.viewmodels.ProfileFragmentViewModel
import kotlinx.coroutines.launch

private const val TAG = "PROFILE FRAGMENT"

class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding : FragmentProfileBinding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null."
        }
    private val profileFragmentViewModel: ProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            userProfit.doOnTextChanged { text, _, _, _ ->

                if (text.toString().isNotBlank())

                    profileFragmentViewModel.updateProfit(text.toString().toInt())

            }

            viewLifecycleOwner.lifecycleScope.launch {

                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                    profileFragmentViewModel.uiState.collect { state ->

                        uiState(state)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "ON DESTROY")
    }

    private fun uiState(state: ProfileFragmentUiState){

        val profit = state.profit

        val fullName = state.fullName

        val amount = state.amountPrice

        val percentage = profileFragmentViewModel.spendOnSubscriptions(state)

        binding.apply {

            if ((profit != 0) && (userProfit.text.isBlank()) && (userProfit.text.toString() != profit.toString())) {

                userProfit.setText(profit.toString())
            }

            if (fullName.isNotBlank()) testAvatar.text = fullName.first().toString()

            Log.d(TAG, "Percentage - $percentage / Amount - $amount / Profit - $profit")

            percentageText.text = String.format(getString(R.string.percentage_symbol), percentage)

            profileInfo.text = fullName

            subscriptionPercentage.progress = percentage

            amountSubscriptions.text = amount.toString()
        }
    }
}