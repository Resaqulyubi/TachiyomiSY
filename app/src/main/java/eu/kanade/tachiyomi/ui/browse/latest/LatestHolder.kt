package eu.kanade.tachiyomi.ui.browse.latest

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.LatestAdapter
import eu.kanade.tachiyomi.util.view.gone
import eu.kanade.tachiyomi.util.view.visible
import kotlinx.android.synthetic.main.latest_controller_card.progress
import kotlinx.android.synthetic.main.latest_controller_card.recycler
import kotlinx.android.synthetic.main.latest_controller_card.source_card
import kotlinx.android.synthetic.main.latest_controller_card.title
import kotlinx.android.synthetic.main.latest_controller_card.title_wrapper

/**
 * Holder that binds the [LatestItem] containing catalogue cards.
 *
 * @param view view of [LatestItem]
 * @param adapter instance of [LatestAdapter]
 */
class LatestHolder(view: View, val adapter: LatestAdapter) :
    BaseFlexibleViewHolder(view, adapter) {

    /**
     * Adapter containing manga from search results.
     */
    private val mangaAdapter = LatestCardAdapter(adapter.controller)

    private var lastBoundResults: List<LatestCardItem>? = null

    init {
        // Set layout horizontal.
        recycler.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = mangaAdapter

        title_wrapper.setOnClickListener {
            adapter.getItem(bindingAdapterPosition)?.let {
                adapter.titleClickListener.onTitleClick(it.source)
            }
        }
    }

    /**
     * Show the loading of source search result.
     *
     * @param item item of card.
     */
    fun bind(item: LatestItem) {
        val source = item.source
        val results = item.results

        val titlePrefix = if (item.highlighted) "▶ " else ""
        val langSuffix = if (source.lang.isNotEmpty()) " (${source.lang})" else ""

        // Set Title with country code if available.
        title.text = titlePrefix + source.name + langSuffix

        when {
            results == null -> {
                progress.visible()
                showHolder()
            }
            results.isEmpty() -> {
                progress.gone()
                hideHolder()
            }
            else -> {
                progress.gone()
                showHolder()
            }
        }
        if (results !== lastBoundResults) {
            mangaAdapter.updateDataSet(results)
            lastBoundResults = results
        }
    }

    /**
     * Called from the presenter when a manga is initialized.
     *
     * @param manga the initialized manga.
     */
    fun setImage(manga: Manga) {
        getHolder(manga)?.setImage(manga)
    }

    /**
     * Returns the view holder for the given manga.
     *
     * @param manga the manga to find.
     * @return the holder of the manga or null if it's not bound.
     */
    private fun getHolder(manga: Manga): LatestCardHolder? {
        mangaAdapter.allBoundViewHolders.forEach { holder ->
            val item = mangaAdapter.getItem(holder.bindingAdapterPosition)
            if (item != null && item.manga.id!! == manga.id!!) {
                return holder as LatestCardHolder
            }
        }

        return null
    }

    private fun showHolder() {
        title_wrapper.visible()
        source_card.visible()
    }

    private fun hideHolder() {
        title_wrapper.gone()
        source_card.gone()
    }
}