class GetMessageCountResponse(
    val count: Int,
) {
    companion object {
        fun mock() =
            GetMessageCountResponse(
                count = 10,
            )
    }
}
