package com.jeong.jejuoreum.feature.detail.presentation.review

import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.core.presentation.CommonBaseViewModel
import com.jeong.jejuoreum.domain.review.usecase.BuildReviewItemUseCase
import com.jeong.jejuoreum.domain.review.usecase.DeleteReviewUseCase
import com.jeong.jejuoreum.domain.review.usecase.FetchReviewsUseCase
import com.jeong.jejuoreum.domain.review.usecase.ObserveReviewsUseCase
import com.jeong.jejuoreum.domain.review.usecase.ToggleReviewLikeUseCase
import com.jeong.jejuoreum.domain.review.usecase.WriteReviewUseCase
import com.jeong.jejuoreum.domain.user.usecase.EnsureAnonymousUserUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetCurrentUserIdUseCase
import com.jeong.jejuoreum.domain.user.usecase.GetCurrentUserNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

private const val DEFAULT_ERROR_MESSAGE = "요청 처리 중 오류가 발생했어요."

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val observeReviewsUseCase: ObserveReviewsUseCase,
    private val fetchReviewsUseCase: FetchReviewsUseCase,
    private val writeReviewUseCase: WriteReviewUseCase,
    private val toggleReviewLikeUseCase: ToggleReviewLikeUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val buildReviewItemUseCase: BuildReviewItemUseCase,
    private val ensureAnonymousUserUseCase: EnsureAnonymousUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getCurrentUserNicknameUseCase: GetCurrentUserNicknameUseCase,
    @Named("ioDispatcher") ioDispatcher: CoroutineDispatcher,
) : CommonBaseViewModel<WriteReviewUiState, WriteReviewUiEvent, WriteReviewUiEffect>(ioDispatcher) {

    private var observeJob: Job? = null

    override fun initialState(): WriteReviewUiState = WriteReviewUiState()

    override fun handleEvent(event: WriteReviewUiEvent) {
        when (event) {
            is WriteReviewUiEvent.Initialize -> initialize(event.oreumIdx, event.oreumName)
            is WriteReviewUiEvent.ReviewTextChanged -> setState { copy(reviewInput = event.text) }
            is WriteReviewUiEvent.SaveClicked -> submitReview(event.defaultNickname)
            is WriteReviewUiEvent.LikeClicked -> toggleLike(event.review)
            is WriteReviewUiEvent.DeleteClicked -> deleteReview(event.review)
            WriteReviewUiEvent.RefreshRequested -> refreshReviews()
        }
    }

    override fun buildErrorEffect(message: String): WriteReviewUiEffect =
        WriteReviewUiEffect.ShowMessage(message.ifBlank { DEFAULT_ERROR_MESSAGE })

    private fun initialize(oreumIdx: Int, oreumName: String) {
        val idx = oreumIdx.takeIf { it >= 0 }?.toString() ?: return
        if (currentState.oreumIdx == idx) return

        setState { copy(oreumIdx = idx, oreumName = oreumName) }
        loadCurrentUser()
        observeReviews(idx)
        refreshReviews()
    }

    private fun loadCurrentUser() {
        val currentUserId = getCurrentUserIdUseCase()
        setState { copy(currentUserId = currentUserId) }
    }

    private fun observeReviews(oreumIdx: String) {
        observeJob?.cancel()
        observeJob = launch {
            observeReviewsUseCase(oreumIdx).collectLatest { resource ->
                when (resource) {
                    Resource.Loading -> setState { copy(isLoading = true) }
                    is Resource.Success -> setState {
                        copy(isLoading = false, reviews = resource.data.toUiModels())
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        sendError(resource.throwable)
                    }
                }
            }
        }
    }

    private fun refreshReviews(showLoading: Boolean = true) {
        val oreumIdx = currentState.oreumIdx ?: return
        launch {
            if (showLoading) setState { copy(isLoading = true) }
            fetchReviewsUseCase(oreumIdx)
                .onSuccess { reviews ->
                    setState { copy(isLoading = false, reviews = reviews.toUiModels()) }
                }
                .onFailure { error ->
                    setState { copy(isLoading = false) }
                    sendError(error)
                }
        }
    }

    private fun submitReview(defaultNickname: String) {
        val oreumIdx = currentState.oreumIdx ?: return
        val content = currentState.reviewInput.trim()
        if (content.isBlank()) return

        launch {
            setState { copy(isSubmitting = true) }
            ensureAnonymousUserUseCase()
                .onSuccess { account ->
                    val nicknameResult = getCurrentUserNicknameUseCase()
                    nicknameResult.onFailure { sendError(it) }
                    val nickname = nicknameResult.getOrDefault(defaultNickname)

                    val review = buildReviewItemUseCase(
                        userId = account.id,
                        nickname = nickname,
                        content = content,
                        createdAt = System.currentTimeMillis()
                    )
                    writeReviewUseCase(oreumIdx, review)
                        .onSuccess {
                            setState {
                                copy(reviewInput = "", isSubmitting = false, currentUserId = account.id)
                            }
                            refreshReviews(showLoading = false)
                        }
                        .onFailure { error ->
                            setState { copy(isSubmitting = false) }
                            sendError(error)
                        }
                }
                .onFailure { error ->
                    setState { copy(isSubmitting = false) }
                    sendError(error)
                }
        }
    }

    private fun toggleLike(review: ReviewUiModel) {
        val oreumIdx = currentState.oreumIdx ?: return
        launch {
            toggleReviewLikeUseCase(oreumIdx, review.userId)
                .onSuccess { refreshReviews(showLoading = false) }
                .onFailure { sendError(it) }
        }
    }

    private fun deleteReview(review: ReviewUiModel) {
        val oreumIdx = currentState.oreumIdx ?: return
        launch {
            deleteReviewUseCase(oreumIdx, review.userId)
                .onSuccess { refreshReviews(showLoading = false) }
                .onFailure { sendError(it) }
        }
    }

    private fun sendError(throwable: Throwable?) {
        val message = when (throwable) {
            is DomainError -> throwable.message
            else -> throwable?.message
        } ?: DEFAULT_ERROR_MESSAGE
        sendErrorEffect(message)
    }
}
