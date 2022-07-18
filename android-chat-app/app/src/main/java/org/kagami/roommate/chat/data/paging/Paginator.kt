package org.kagami.roommate.chat.data.paging

interface Paginator<Key, Item> {
    suspend fun load()
    fun reset()
}