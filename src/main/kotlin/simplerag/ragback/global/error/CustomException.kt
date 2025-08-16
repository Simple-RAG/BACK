package simplerag.ragback.global.error

open class CustomException(
    open val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)

class S3Exception(
    override val errorCode: ErrorCode,
    override val cause: Throwable? = null,
) : CustomException(errorCode, errorCode.message, cause)

class FileException(
    override val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : CustomException(errorCode, message, cause)