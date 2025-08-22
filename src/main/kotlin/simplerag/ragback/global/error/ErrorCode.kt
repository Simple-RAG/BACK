package simplerag.ragback.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String
) {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    ALREADY_FILE(HttpStatus.BAD_REQUEST, "ALREADY_FILE", "같은 내용의 파일이 이미 존재합니다."),
    FILE_PART_MISSING(HttpStatus.BAD_REQUEST, "FILE_PART_MISSING", "필수 파트가 존재하지 않습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "INVALID_JSON", "JSON이 유효하지 않습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_FILE_TYPE", "FILE TYPE이 유효하지 않습니다."),

    // S3
    S3_OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "S3_001", "S3 오브젝트를 찾을 수 없습니다."),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3_002", "S3 업로드 실패"),
    S3_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3_003", "S3 삭제 실패"),
    S3_INVALID_URL(HttpStatus.BAD_REQUEST, "S3_004", "유효하지 않은 S3 URL 입니다."),
    S3_EMPTY_FILE(HttpStatus.BAD_REQUEST, "S3_005", "빈 파일은 업로드할 수 없습니다."),
    S3_PRESIGN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3_006", "프리사인 URL 발급 실패"),
    S3_UNSUPPORTED_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "S3_007", "지원하지 않는 Content-Type 입니다."),

    // index
    OVERLAP_OVERFLOW(HttpStatus.BAD_REQUEST, "INDEX_001", "overlap 크기는 chunking 크기를 넘을 수 없습니다.")
}
