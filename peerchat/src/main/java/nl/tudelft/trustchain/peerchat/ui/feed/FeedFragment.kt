package nl.tudelft.trustchain.peerchat.ui.feed

import android.bluetooth.BluetoothManager
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattskala.itemadapter.Item
import com.mattskala.itemadapter.ItemAdapter
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import nl.tudelft.ipv8.Peer
import nl.tudelft.ipv8.util.toHex
import nl.tudelft.trustchain.common.ui.BaseFragment
import nl.tudelft.trustchain.common.util.viewBinding
import nl.tudelft.trustchain.peerchat.R
import nl.tudelft.trustchain.peerchat.community.PeerChatCommunity
import nl.tudelft.trustchain.peerchat.databinding.FragmentContactsBinding
import nl.tudelft.trustchain.peerchat.databinding.FragmentFeedBinding
import nl.tudelft.trustchain.peerchat.db.PeerChatStore
import nl.tudelft.trustchain.peerchat.entity.ChatMessage
import nl.tudelft.trustchain.peerchat.entity.Contact
import nl.tudelft.trustchain.peerchat.ui.conversation.ConversationFragment

@OptIn(ExperimentalCoroutinesApi::class)
class FeedFragment : BaseFragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)

    private val store by lazy {
        PeerChatStore.getInstance(requireContext())
    }

    private val postRepository by lazy {
        PostRepository(getIpv8().getOverlay()!!, store)
    }

    private val adapter = ItemAdapter()

    private val items: LiveData<List<Item>> by lazy {
        liveData { emit(listOf<Item>()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.registerRenderer(PostItemRenderer({
            postRepository.likePost(it.block)
        }, {
            val args = Bundle()
            args.putString(NewPostFragment.ARG_HASH, it.block.calculateHash().toHex())
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment, args)
        }))

        lifecycleScope.launchWhenResumed {
            while (isActive) {
                // Refresh peer status periodically
                val items = postRepository.getPostsByFriends()
                adapter.updateItems(items)
                binding.imgEmpty.isVisible = items.isEmpty()
                delay(1000L)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        items.observe(viewLifecycleOwner, Observer {
            adapter.updateItems(it)
            binding.imgEmpty.isVisible = it.isEmpty()
        })
    }
}
