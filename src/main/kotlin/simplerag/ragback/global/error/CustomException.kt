package simplerag.ragback.global.error

open class CustomException(
    open val errorCode: ErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)

class S3Exception(
    override val errorCode: ErrorCode,
) : CustomException(errorCode)

class FileException(
    override val errorCode: ErrorCode,
    override val message: String,
) : CustomException(errorCode, message)