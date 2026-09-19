package com.example.ai

import com.example.BuildConfig

/**
 * Enforces Milestone v0.1 AI safeguard constraint:
 * Any potential AI invocation MUST check BuildConfig.AI_ENABLED beforehand.
 * In release and milestone v0.1 offline builds, BuildConfig.AI_ENABLED is false.
 */
object AiFeatureGuard {
    val isAiEnabled: Boolean
        get() = BuildConfig.AI_ENABLED

    suspend fun <T> runIfAiEnabled(action: suspend () -> T): Result<T> {
        return if (isAiEnabled) {
            try {
                Result.success(action())
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("AI features are disabled in this build variant (BuildConfig.AI_ENABLED is false)."))
        }
    }
}
