package com.example.subscriptionmanager.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.subscriptionmanager.databinding.SubscriptionItemListBinding
import com.example.subscriptionmanager.model.Subscription
import java.util.*

class SubscriptionsAdapter(
    private val subscriptions: List<Subscription>,
    private val onSubscriptionClicked: (id: UUID) -> Unit
) : RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>() {

    class SubscriptionViewHolder(
        private val binding: SubscriptionItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription, onSubscriptionClicked: (id: UUID) -> Unit){

            binding.apply {

                subscriptionTitle.text = subscription.title

                subscriptionPrice.text = subscription.price.toString() + "$"

                if (subscription.title.isNotBlank()) firsLetter.text = subscription.title[0].toString()

                root.setOnClickListener {

                    onSubscriptionClicked(subscription.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = SubscriptionItemListBinding.inflate(inflater, parent, false)

        return SubscriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {

        val subscription = subscriptions[position]

        holder.bind(subscription, onSubscriptionClicked)
    }

    override fun getItemCount() = subscriptions.size

}