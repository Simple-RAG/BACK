package simplerag.ragback.global.response

data class ApiResponse<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> ok(data: T?, message: String = "성공") =
            ApiResponse(true, "OK", message, data)

        fun fail(code: String, message: String) =
            ApiResponse<Nothing>(false, code, message)
    }
}
