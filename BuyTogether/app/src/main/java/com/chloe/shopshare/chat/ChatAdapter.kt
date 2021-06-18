package com.chloe.shopshare.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chloe.shopshare.data.Chat
import com.chloe.shopshare.data.Message
import com.chloe.shopshare.databinding.ItemChatBinding
import com.chloe.shopshare.ext.getDayWeek
import com.chloe.shopshare.ext.toDisplayDateTimeFormat

class ChatAdapter(
    private val onClickListener: OnClickListener,
    private val viewModel: ChatViewModel
) : ListAdapter<Chat, ChatAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private var binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        private var _messageList = MutableLiveData<List<Message>>()

        fun bind(item: Chat, viewModel: ChatViewModel) {
            binding.lifecycleOwner = this
            binding.item = item
            item.chatRoom?.let {
                _messageList = viewModel.getLiveMessage(it.id)
            }

            _messageList.observe(this, Observer {
                it?.let {
                    if (it.isNotEmpty()){
                        Log.d("Chat", "_messageList = $it")
                        item.message = it
                        Log.d("Chat", "message = ${item.message}")
                        binding.item = item

                        val time: Long = it.last().time
                        val message: String? = it.last().message
                        val image: String? = it.last().image

                        binding.messageDate.text =
                            when (time) {
                                0L -> ""
                                else -> it.last().time.getDayWeek()
                            }

                        binding.messageContent.text =
                            when (message) {
                                null -> {
                                    when (image) {
                                        null -> ""
                                        else -> "照片已傳送"
                                    }
                                }
                                else -> message
                            }
                    }
                }
            })



            binding.executePendingBindings()
        }


        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }

    }

    class OnClickListener(val clickListener: (item: Chat) -> Unit) {
        fun onClick(item: Chat) = clickListener(item)
    }

}