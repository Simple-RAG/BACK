package simplerag.ragback.global.error

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.InvalidNullException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException
import simplerag.ragback.global.response.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ApiResponse<Nothing> {
        val message = ex.bindingResult.allErrors.first().defaultMessage ?: "잘못된 요청"
        return ApiResponse.fail(ErrorCode.INVALID_INPUT.code, message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ApiResponse<Nothing> {
        val message = ex.constraintViolations.firstOrNull()?.message ?: "잘못된 요청"
        return ApiResponse.fail(ErrorCode.INVALID_INPUT.code, message)
    }

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun handleMissingPart(e: MissingServletRequestPartException): ApiResponse<Nothing> {
        val msg = "필수 '${e.requestPartName}' 가 없습니다."
        return ApiResponse.fail(ErrorCode.FILE_PART_MISSING.code, message = msg)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(e: HttpMessageNotReadableException): ApiResponse<Nothing> {
        val cause = e.cause

        val msg = when (cause) {
            is InvalidNullException -> {
                val field = cause.path.lastOrNull()?.fieldName ?: "unknown"
                "'$field' 값이 비어있습니다."
            }

            is InvalidFormatException -> {
                val field = cause.path.lastOrNull()?.fieldName ?: "unknown"
                "'$field' 값 형식이 올바르지 않습니다."
            }

            is MismatchedInputException -> {
                val field = cause.path.lastOrNull()?.fieldName ?: "unknown"
                "'$field' 값 타입이 올바르지 않습니다."
            }

            is JsonParseException -> {
                // JSON 문법 오류 (콤마, 따옴표 누락 등)
                "JSON 문법이 올바르지 않습니다."
            }

            else -> "유효하지 않은 요청입니다."
        }

        return ApiResponse.fail(ErrorCode.INVALID_JSON.code, message = msg)
    }

    @ExceptionHandler(FileException::class)
    fun handleCustomException(ex: FileException): ApiResponse<Nothing> {
        val errorCode = ex.errorCode
        return ApiResponse.fail(errorCode.code, "${errorCode.message} sha256: ${ex.message}")
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ApiResponse<Nothing> {
        val errorCode = ex.errorCode
        return ApiResponse.fail(errorCode.code, errorCode.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ApiResponse<Nothing> {
        log.error("Unhandled exception", ex)

        return ApiResponse.fail(ErrorCode.INTERNAL_ERROR.code, ErrorCode.INTERNAL_ERROR.message)
    }
}
