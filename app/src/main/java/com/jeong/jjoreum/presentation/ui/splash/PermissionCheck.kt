package com.jeong.jjoreum.presentation.ui.splash

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.jeong.jjoreum.util.logMessage

/**
 * 앱에서 위치 권한을 체크하고 요청하는 클래스
 * @param context 애플리케이션 컨텍스트
 */
class PermissionCheck(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PermissionPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    /**
     * 위치 권한이 부여되었는지 확인하는 변수
     */
    var isPermission: Boolean
        get() = sharedPreferences.getBoolean("LOCATION_PERMISSION", false)
        set(value) {
            editor.putBoolean("LOCATION_PERMISSION", value).apply()
        }

    /**
     * 앱에서 필요한 위치 권한 목록
     */
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val permissionCheckList = mutableListOf<String>()

    /**
     * 현재 앱의 위치 권한이 부여되었는지 확인하는 함수
     * @return 모든 권한이 허용되었는지 여부 (true: 허용됨, false: 허용되지 않음)
     */
    fun checkSelfCurrentPermission(context: Context): Boolean {
        permissionCheckList.clear()
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionCheckList.add(permission)
            }
        }
        return permissionCheckList.isEmpty()
    }

    /**
     * 위치 권한을 요청하는 함수
     * @param owner 권한 요청을 수행할 ActivityResultLauncher
     */
    fun requestAppPermissions(owner: ActivityResultLauncher<Array<String>>) {
        logMessage(permissionCheckList.toString())
        owner.launch(permissionCheckList.toTypedArray())
    }

    /**
     * 권한 요청 결과를 확인하는 함수
     * @param grantResults 요청한 권한들의 결과 맵
     * @return 모든 권한이 허용되었는지 여부 (true: 허용됨, false: 허용되지 않음)
     */
    fun appPermissionResult(grantResults: Map<String, Boolean>): Boolean {
        return grantResults.values.all { it } // 모든 권한이 허용되었는지 확인
    }
}
