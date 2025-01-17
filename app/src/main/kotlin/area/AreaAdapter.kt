package area

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import icons.iconTypeface
import map.enableDarkModeIfNecessary
import map.showPolygons
import okhttp3.HttpUrl
import org.btcmap.databinding.ItemAreaElementBinding
import org.btcmap.databinding.ItemContactBinding
import org.btcmap.databinding.ItemMapBinding
import org.locationtech.jts.geom.Polygon

class AreaAdapter(
    private val listener: Listener,
) : ListAdapter<AreaAdapter.Item, AreaAdapter.ItemViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Map -> VIEW_TYPE_MAP
            is Item.Contact -> VIEW_TYPE_CONTACT
            is Item.Element -> VIEW_TYPE_ELEMENT
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val binding = when (viewType) {
            VIEW_TYPE_MAP -> {
                ItemMapBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            }

            VIEW_TYPE_CONTACT -> {
                ItemContactBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            }

            VIEW_TYPE_ELEMENT -> {
                ItemAreaElementBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ).apply {
                    icon.typeface = parent.context.iconTypeface()
                }
            }

            else -> throw Exception()
        }

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            listener = listener,
        )
    }

    sealed class Item {

        data class Map(
            val polygons: List<Polygon>,
            val paddingPx: Int,
        ) : Item()

        data class Contact(
            val website: HttpUrl?,
            val twitter: HttpUrl?,
            val telegram: HttpUrl?,
            val discord: HttpUrl?,
            val youtube: HttpUrl?,
        ) : Item()

        data class Element(
            val id: String,
            val iconId: String,
            val name: String,
            val status: String,
            val colorResId: Int,
            val showCheckmark: Boolean,
        ) : Item()
    }

    class ItemViewHolder(
        private val binding: ViewBinding,
    ) : RecyclerView.ViewHolder(
        binding.root,
    ) {

        fun bind(
            item: Item,
            listener: Listener,
        ) {
            if (item is Item.Map && binding is ItemMapBinding) {
                binding.map.enableDarkModeIfNecessary()
                binding.map.showPolygons(item.polygons, item.paddingPx)
                binding.root.setOnClickListener { listener.onMapClick() }
            }

            if (item is Item.Element && binding is ItemAreaElementBinding) {
                binding.apply {
                    icon.text = item.iconId
                    title.text = item.name
                    checkmark.isVisible = item.showCheckmark
                    subtitle.text = item.status
                    val attrs = root.context.theme.obtainStyledAttributes(intArrayOf(item.colorResId))
                    subtitle.setTextColor(attrs.getColor(0, 0))
                    root.setOnClickListener { listener.onElementClick(item) }
                }
            }

            if (item is Item.Contact && binding is ItemContactBinding) {
                binding.apply {
                    website.isVisible = item.website != null
                    website.setOnClickListener { item.website?.let { listener.onUrlClick(it) } }

                    twitter.isVisible = item.twitter != null
                    twitter.setOnClickListener { item.twitter?.let { listener.onUrlClick(it) } }

                    telegram.isVisible = item.telegram != null
                    telegram.setOnClickListener { item.telegram?.let { listener.onUrlClick(it) } }

                    discord.isVisible = item.discord != null
                    discord.setOnClickListener { item.discord?.let { listener.onUrlClick(it) } }

                    youtube.isVisible = item.youtube != null
                    youtube.setOnClickListener { item.youtube?.let { listener.onUrlClick(it) } }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(
            oldItem: Item,
            newItem: Item,
        ): Boolean {
            return if (oldItem is Item.Element && newItem is Item.Element) {
                oldItem.id == newItem.id
            } else (oldItem is Item.Map && newItem is Item.Map)
                    || (oldItem is Item.Contact && newItem is Item.Contact)
        }

        override fun areContentsTheSame(
            oldItem: Item,
            newItem: Item,
        ): Boolean {
            if (oldItem is Item.Map && newItem is Item.Map) {
                return true
            }

            if (oldItem is Item.Contact && newItem is Item.Contact) {
                return true
            }

            return newItem == oldItem
        }
    }

    interface Listener {

        fun onMapClick()

        fun onElementClick(item: Item.Element)

        fun onUrlClick(url: HttpUrl)
    }

    companion object {
        const val VIEW_TYPE_MAP = 0
        const val VIEW_TYPE_CONTACT = 1
        const val VIEW_TYPE_ELEMENT = 2
    }
}