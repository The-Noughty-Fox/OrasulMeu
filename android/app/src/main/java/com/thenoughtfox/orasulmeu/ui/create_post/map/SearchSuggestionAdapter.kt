package com.thenoughtfox.orasulmeu.ui.create_post.map


import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapbox.search.result.SearchAddress
import com.mapbox.search.result.SearchSuggestion
import com.thenoughtfox.orasulmeu.common.BaseAdapter
import com.thenoughtfox.orasulmeu.databinding.ItemSearchSuggestionBinding

class SearchSuggestionAdapter : BaseAdapter<SearchSuggestion, ItemSearchSuggestionBinding>() {

    private var onItemClicked: ((SearchSuggestion) -> Unit)? = null

    fun setOnItemClicked(onItemClicked: (SearchSuggestion) -> Unit) {
        this.onItemClicked = onItemClicked
    }

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemSearchSuggestionBinding =
        ItemSearchSuggestionBinding.inflate(inflater, parent, false)

    override fun ItemSearchSuggestionBinding.onBindItem(item: SearchSuggestion, position: Int) {
        val shortAddress = item.address?.formattedAddress(SearchAddress.FormatStyle.Short)
        val address = if (shortAddress.isNullOrEmpty()) item.name else shortAddress
        textViewName.text = address
        textViewFullname.text = item.fullAddress
        root.setOnClickListener { onItemClicked?.invoke(item) }
    }
}