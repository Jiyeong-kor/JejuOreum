#!/usr/bin/env bash
# 🔎 JejuOreum 리팩터링 검증 스크립트 (읽기 전용)
# bash에서 그대로 실행하세요. 파일을 수정/삭제하지 않습니다.

set -u

# ========== 헬퍼 함수 ==========
ok(){ echo "✅ $1"; }
ng(){ echo "❌ $1"; }

# 문자열 잔존 여부 확인용
check_absent(){
  # $1=label, $2=grep-cmd
  if eval "$2" >/dev/null 2>&1; then
    ng "$1 (잔존 발견)"
    eval "$2" | sed -e 's/^/   → /'
  else
    ok "$1 (없음)"
  fi
}

echo "=== 1) 패키지/임포트 문자열 잔존 점검 ==="
check_absent "구 패키지명 com.jeong.jejuoreum 문자열" "grep -R 'com\\.jeong\\.jjoreum' ."
check_absent "구 도메인 import com.jeong.jejuoreum.domain" "grep -R 'import com\\.jeong\\.domain' ."
check_absent "구 데이터 import com.jeong.jejuoreum.data" "grep -R 'import com\\.jeong\\.data' ."
check_absent "구 core 패키지 import com.jeong.jejuoreum.core" "grep -R 'import com\\.jeong\\.core' ."

echo -e "\n=== 2) app 설정/파일 위치 검증 ==="
for f in \
  app/google-services.json \
  app/build.gradle.kts \
  app/src/main/java/com/jeong/jejuoreum/app/JJOreumApplication.kt \
  app/src/main/java/com/jeong/jejuoreum/app/di/ApiModule.kt \
  app/src/main/java/com/jeong/jejuoreum/app/di/AppModule.kt \
  app/src/main/java/com/jeong/jejuoreum/app/di/UserRepositoryModule.kt \
  app/src/main/java/com/jeong/jejuoreum/app/network/DefaultNetworkMonitor.kt \
  app/src/main/java/com/jeong/jejuoreum/app/presentation/main/MainActivity.kt; do
  [ -f "$f" ] && ok "$f 존재" || ng "$f 없음"
done

echo -e "\n=== 3) core/navigation NavHost 보정 확인 ==="
[ -f core/navigation/src/main/java/com/jeong/jejuoreum/core/navigation/NavHost.kt ] && ok "NavHost.kt 존재" || ng "NavHost.kt 없음"
grep -q "NavHost as ComposeNavHost" core/navigation/src/main/java/com/jeong/jejuoreum/core/navigation/NavHost.kt && ok "ComposeNavHost 별칭 사용" || ng "ComposeNavHost 별칭 누락"
grep -q "ComposeNavHost" core/navigation/src/main/java/com/jeong/jejuoreum/core/navigation/NavHost.kt && ok "ComposeNavHost 호출 존재" || ng "ComposeNavHost 호출 없음"

echo -e "\n=== 4) core/testing 모듈 확인 ==="
[ -f core/testing/build.gradle.kts ] && ok "core/testing/build.gradle.kts 생성" || ng "core/testing/build.gradle.kts 없음"
grep -q "com.android.library" core/testing/build.gradle.kts && ok "core/testing plugin 확인" || ng "core/testing plugin 누락"

echo -e "\n=== 5) 디자인 리소스 이동 확인 ==="
for res in \
  core/designsystem/src/main/res/drawable/oreum_favorite_selected.xml \
  core/designsystem/src/main/res/drawable/oreum_favorite_unselected.xml \
  core/designsystem/src/main/res/drawable/oreum_marker_selected.png \
  core/designsystem/src/main/res/drawable/oreum_marker_unselected.png \
  core/designsystem/src/main/res/drawable/oreum_profile_placeholder.xml \
  app/src/main/res/drawable/ic_list.xml \
  app/src/main/res/drawable/ic_map.xml \
  app/src/main/res/drawable/ic_my_selected.xml \
  app/src/main/res/drawable/ic_my_unselected.xml; do
  [ -f "$res" ] && ok "$res" || ng "$res 없음"
done

echo -e "\n=== 6) feature 모듈 구조/파일 확인 ==="
for f in \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/mapper/OreumUiMapper.kt \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/model/OreumUiModel.kt \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/model/OreumSummaryUiModel.kt \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/oreum/OreumScreen.kt \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/oreum/OreumUiContract.kt \
  feature/map/src/main/java/com/jeong/jejuoreum/feature/map/presentation/oreum/OreumViewModel.kt \
  feature/detail/src/main/java/com/jeong/jejuoreum/feature/detail/domain/OreumDetailInteractor.kt \
  feature/profile/src/main/java/com/jeong/jejuoreum/feature/profile/presentation/favorite/MyFavoriteScreen.kt \
  feature/profile/src/main/java/com/jeong/jejuoreum/feature/profile/presentation/favorite/MyFavoriteViewModel.kt \
  feature/profile/src/main/java/com/jeong/jejuoreum/feature/profile/presentation/stamp/MyStampScreen.kt \
  feature/onboarding/src/main/java/com/jeong/jejuoreum/feature/onboarding/presentation/JoinViewModel.kt \
  feature/splash/src/main/java/com/jeong/jejuoreum/feature/splash/presentation/SplashViewModel.kt \
  feature/splash/src/main/java/com/jeong/jejuoreum/feature/splash/domain/usecase/PrepareSplashUseCase.kt; do
  [ -f "$f" ] && ok "$f" || ng "$f 없음"
done

echo -e "\n=== 7) 삭제 대상 잔존 여부 확인 ==="
check_absent "feature/src" "ls feature/src"
check_absent "feature/oreum" "ls feature/oreum"
check_absent "feature/main" "ls feature/main"
check_absent "feature/join" "ls feature/join"

echo -e "\n=== 8) 최종 잔존 금지 패턴 ==="
check_absent "com.jeong.feature 패키지 선언" "grep -R 'package com\\.jeong\\.feature' ."
check_absent "임포트 com.jeong.feature" "grep -R 'import com\\.jeong\\.feature' ."

echo -e "\n=== 9) 상태 확인 ==="
git status -s
