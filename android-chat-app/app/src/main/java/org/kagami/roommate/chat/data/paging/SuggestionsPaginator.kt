package org.kagami.roommate.chat.data.paging

import org.kagami.roommate.chat.util.ApiResult

class SuggestionsPaginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadFinished: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> ApiResult<List<Item>>,
    private inline val getNextKey: suspend () -> Key,
    private inline val onError: suspend (String?) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
) : Paginator<Key, Item> {

    private var isMakingRequest = false
    private var currentKey = initialKey

    override suspend fun load() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadFinished(true)
        val request = onRequest(currentKey)
        isMakingRequest = false
        println("request: ${request.data} ${request.message}")
        if (request.data == null) {
            onError(request.message)
            onLoadFinished(false)
            return
        }
        currentKey = getNextKey()
        onSuccess(request.data, currentKey)
        onLoadFinished(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}