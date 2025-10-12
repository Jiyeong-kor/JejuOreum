package com.jeong.jejuoreum.core.common

/**
 * Marker interface for immutable UI state models exposed to the presentation layer.
 */
interface UiState

/**
 * Marker interface for user-driven events propagated from the UI layer.
 */
interface UiEvent

/**
 * Marker interface for one-off side effects such as navigation or toast messages.
 */
interface UiEffect
