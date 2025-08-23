package simplerag.ragback.domain.prompt.entity.enums

enum class PreSet(
    val description: String,
    val defaultSystemPrompt: String
) {
    CUSTOM(
        "User-defined custom prompt",
        "TODO: custom system prompt here"
    ),

    // Retrieval / Q&A
    RAG_QA(
        "Question Answering with retrieved context",
        "TODO: system prompt for RAG_QA"
    ),
    RAG_SUMMARIZER(
        "Summarize documents retrieved via RAG",
        "TODO: system prompt for RAG_SUMMARIZER"
    ),

    // Code related
    CODE_REVIEW_BACKEND(
        "Backend code review (Java/Kotlin/Spring)",
        "TODO: system prompt for CODE_REVIEW_BACKEND"
    ),
    CODE_REVIEW_GENERAL(
        "General code review and best practices",
        "TODO: system prompt for CODE_REVIEW_GENERAL"
    ),

    // Language tasks
    TRANSLATION_EN_KO(
        "English ↔ Korean translation",
        "TODO: system prompt for TRANSLATION_EN_KO"
    ),
    TRANSLATION_MULTI(
        "Multi-language translation",
        "TODO: system prompt for TRANSLATION_MULTI"
    ),
    PROOFREAD_KR(
        "Korean proofreading / grammar correction",
        "TODO: system prompt for PROOFREAD_KR"
    ),

    // Content tasks
    TEXT_SUMMARIZER(
        "General text summarization",
        "TODO: system prompt for TEXT_SUMMARIZER"
    ),
    DATA_CLEANER(
        "Text cleaning and preprocessing",
        "TODO: system prompt for DATA_CLEANER"
    ),
    EMAIL_WRITER(
        "Email / message generation",
        "TODO: system prompt for EMAIL_WRITER"
    ),

    // Safety / Guardrails
    SAFETY_FILTERED(
        "Safe response (avoid harmful or unsafe outputs)",
        "TODO: system prompt for SAFETY_FILTERED"
    )
}
