package com.jeong.jejuoreum.core.presentation

import com.jeong.jejuoreum.core.common.UiEffect
import com.jeong.jejuoreum.core.common.UiEvent
import com.jeong.jejuoreum.core.common.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CommonBaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_should_update_correctly() = runTest(testDispatcher) {
        val viewModel = TestViewModel(testDispatcher)

        viewModel.onEvent(TestEvent.Increase)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.count)
    }

    @Test
    fun handleError_should_emit_effect() = runTest(testDispatcher) {
        val viewModel = TestViewModel(testDispatcher)

        viewModel.onEvent(TestEvent.Fail("오류"))
        advanceUntilIdle()
        val effect = viewModel.effect.first()

        assertEquals("오류", (effect as TestEffect.ShowError).message)
    }

    private class TestViewModel(
        dispatcher: CoroutineDispatcher,
    ) : CommonBaseViewModel<TestState, TestEvent, TestEffect>(dispatcher) {

        override fun initialState(): TestState = TestState()

        override fun handleEvent(event: TestEvent) {
            when (event) {
                TestEvent.Increase -> launch { setState { copy(count = count + 1) } }
                is TestEvent.Fail -> handleError(IllegalStateException(event.message))
            }
        }

        override fun buildErrorEffect(message: String): TestEffect = TestEffect.ShowError(message)
    }

    private data class TestState(
        val count: Int = 0,
    ) : UiState

    private sealed interface TestEvent : UiEvent {
        data object Increase : TestEvent
        data class Fail(val message: String) : TestEvent
    }

    private sealed interface TestEffect : UiEffect {
        data class ShowError(val message: String) : TestEffect
    }
}
