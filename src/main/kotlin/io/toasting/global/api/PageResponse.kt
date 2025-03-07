package io.toasting.api

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
) {
    companion object {
        fun <T> of(
            content: List<T>,
            totalElements: Long,
            totalPages: Int,
        ): PageResponse<T> =
            PageResponse(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
            )
    }
}
