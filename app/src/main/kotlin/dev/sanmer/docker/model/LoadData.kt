package dev.sanmer.docker.model

sealed class LoadData<out V> {
    data object Pending : LoadData<Nothing>()
    data object Loading : LoadData<Nothing>()
    data class Success<out V>(val value: V) : LoadData<V>()
    data class Failure(val error: Throwable) : LoadData<Nothing>()

    val isLoading inline get() = this == Loading
    val isSuccess inline get() = this is Success
    val isFailure inline get() = this is Failure

    companion object Default {
        fun <V> Result<V>.asLoadData(): LoadData<V> {
            return when {
                isSuccess -> Success(getOrThrow())
                else -> Failure(requireNotNull(exceptionOrNull()))
            }
        }

        fun <V> LoadData<V>.getOrThrow(): V {
            return when (this) {
                Pending -> throw IllegalStateException("Pending")
                Loading -> throw IllegalStateException("Loading")
                is Success<V> -> value
                is Failure -> throw error
            }
        }

        inline fun <V, R> LoadData<V>.getValue(fallback: R, transform: (V) -> R): R {
            return (this as? Success)?.value?.let(transform) ?: fallback
        }

        inline fun <V, R> LoadData<V>.let(transform: (V) -> R): LoadData<R> {
            return when (this) {
                Pending -> Pending
                Loading -> Loading
                is Success<V> -> Success(transform(value))
                is Failure -> this
            }
        }
    }
}