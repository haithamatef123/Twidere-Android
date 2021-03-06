/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.mariotaku.twidere.R
import org.mariotaku.twidere.adapter.iface.ILoadMoreSupportAdapter
import org.mariotaku.twidere.model.ItemCounts
import org.mariotaku.twidere.model.ParcelableUserList
import org.mariotaku.twidere.util.view.display
import org.mariotaku.twidere.view.holder.SimpleUserListViewHolder

class SimpleParcelableUserListsAdapter(
        context: Context
) : BaseArrayAdapter<ParcelableUserList>(context, R.layout.list_item_simple_user_list) {

    override val itemCounts: ItemCounts = ItemCounts(2)

    override fun getItemId(position: Int): Long {
        when (itemCounts.getItemCountIndex(position)) {
            0 -> {
                return getItem(position - itemCounts.getItemStartPosition(0)).hashCode().toLong()
            }
            1 -> {
                return Integer.MAX_VALUE + 1L
            }
        }
        throw UnsupportedOperationException()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        when (getItemViewType(position)) {
            0 -> {
                val view = super.getView(position, convertView, parent)
                val holder = view.tag as? SimpleUserListViewHolder ?: run {
                    val h = SimpleUserListViewHolder(view)
                    view.tag = h
                    return@run h
                }
                val userList = getItem(position)
                holder.display(userList, mediaLoader, userColorNameManager, profileImageEnabled)
                return view
            }
            1 -> {
                val view = createViewFromResource(position, convertView, parent, R.layout.list_item_load_indicator)
                return view
            }
        }
        throw UnsupportedOperationException()
    }

    override fun getItemViewType(position: Int): Int {
        return itemCounts.getItemCountIndex(position)
    }

    override fun getViewTypeCount(): Int {
        return itemCounts.size
    }

    override fun getCount(): Int {
        itemCounts[0] = super.getCount()
        itemCounts[1] = if (loadMoreIndicatorPosition and ILoadMoreSupportAdapter.END != 0L) 1 else 0
        return itemCounts.itemCount
    }

    fun setData(data: List<ParcelableUserList>?) {
        clear()
        if (data == null) return
        for (user in data) {
            //TODO improve compare
            if (findItemPosition(user.hashCode().toLong()) < 0) {
                add(user)
            }
        }
    }

}
