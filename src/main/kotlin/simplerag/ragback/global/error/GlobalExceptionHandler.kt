package simplerag.ragback.global.error

import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import simplerag.ragback.global.response.ApiResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = ex.bindingResult.allErrors.first().defaultMessage ?: "잘못된 요청"
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.code, message))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiResponse<Nothing>> {
        val message = ex.constraintViolations.firstOrNull()?.message ?: "잘못된 요청"
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.code, message))
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = ex.errorCode
        return ResponseEntity
            .status(errorCode.status)
            .body(ApiResponse.fail(errorCode.code, errorCode.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.INTERNAL_ERROR.status)
            .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR.code, ErrorCode.INTERNAL_ERROR.message))
    }
}
