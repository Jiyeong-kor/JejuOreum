package com.jeong.jjoreum.presentation.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * ViewBinding을 사용하는 Fragment의 기본 베이스 클래스
 * @param B ViewBinding 타입을 나타내는 제너릭
 * @property inflate 실제 ViewBinding 객체를 생성하기 위한 람다
 */
typealias FragmentInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class ViewBindingBaseFragment<B : ViewBinding>(
    private val inflate: FragmentInflate<B>
) : Fragment() {

    // ViewBinding 객체, onDestroyView()가 호출되면 null 처리됨
    private var _binding: B? = null

    /**
     * 자식 클래스에서 접근하는 바인딩 프로퍼티
     * onDestroyView() 이후에는 접근 시 경고 로그를 남기고 null 반환
     */
    protected open val binding: B?
        get() {
            if (_binding == null) {
                Log.e(
                    "ViewBindingBaseFragment",
                    "경고: ViewBinding이 onDestroyView() 이후에 접근되었습니다."
                )
            }
            return _binding
        }

    /**
     * Fragment의 뷰를 생성하는 단계에서 ViewBinding 객체를 초기화
     * @return 생성된 View 객체
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        return _binding!!.root
    }

    /**
     * Fragment의 뷰가 사라질 때 ViewBinding 객체를 해제
     */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
