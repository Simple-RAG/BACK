package simplerag.ragback.global.error

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
