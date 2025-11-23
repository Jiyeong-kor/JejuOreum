# 🌋 JejuOreum - 제주 오름 탐방 앱

**제주의 숨겨진 보물, 오름을 지도와 스탬프로 즐기자!**  
제주도 오름 정보를 시각화하여 탐색하고, 인증하며, 기록할 수 있는 Android 앱입니다.

---

## 📱 프로젝트 개요

- **앱 이름**: JejuOreum  
- **개발 기간**: 2025.09 ~ (Compose & Hilt로 마이그레이션 ``feat/compose-di-migration`` 브랜치에 완료, 멀티모듈을 사용한 Clean Architecture 마이그레이션 ``feat/hybrid-architecture-migration`` 브랜치에 진행 중)
- **플랫폼**: Android  
- **개발 언어**: Kotlin  
- **개발 환경**: Android Studio  
- **데이터**: 제주 오름 Open API + Firebase Firestore  

## 🤷‍♀️ 기획 의도  
올레길보다 검색량이 많지만 관광 요소로 저평가된 '오름'을 탐방·기록할 수 있는 앱을 통해 제주 관광 활성화를 도모하고자 개발했습니다.

---

## 🛠 사용 기술 스택

| 구분 | 기술 |
|----------|------------|
| Language | Kotlin |
| Architecture | MVVM, Repository Pattern, ViewModelFactory (manual DI) |
| Async Programming | Kotlin Coroutines, StateFlow, SharedFlow |
| Navigation | Jetpack Navigation |
| Location | Android Location Services |
| Local Storage | SharedPreferences |
| Networking | Retrofit2, OkHttp, Firebase Firestore, Firebase Auth, Gson |
| Map SDK | Kakao Map SDK |
| Image Loading | Coil |
| Tools | Git, Figma |

---

## 📱 주요 기능 시연 및 설명

### 📍 1. 지도 기반 오름 탐색

| 지도 전체 탐색 | 오름 선택 후 상세 이동 | 아이콘 변화 및 정보 표시 |
|:--:|:--:|:--:|
| <img src="./screenshots/지도%20검색%20기반%20오름%20찾기%20-%20오름%20상세%20화면%20이동.gif" width="250"/> | <img src="./screenshots/지도%20아이콘%20기반%20오름%20선택%20-%20오름%20상세%20화면%20이동.gif" width="250"/>  | <img src="./screenshots/지도%20기반%20오름%20검색%20화면%20-%20오름%20아이콘%20변화%20및%20상세%20정보%20등장.gif" width="250"/>  |
|카카오맵 기반 지도에서 오름 위치를 검색할 수 있습니다.|오름 아이콘을 선택하면 상세 화면으로 이동하여 정보를 확인할 수 있습니다.|오름 상태에 따라 아이콘이 변하며, 상세 정보도 함께 표시됩니다.|

---

### 📋 2. 리스트 기반 오름 탐색

| 오름 리스트 보기 | 관심 오름 등록 |
|:--:|:--:|
| <img src="./screenshots/목록%20기반%20오름%20선택.gif" width="250"/> | <img src="./screenshots/관심%20오름%20등록.gif" width="250"/>  |
| 리스트 형태로 오름을 탐색하고 상세 정보를 확인할 수 있습니다.| 즐겨찾기 기능을 통해 관심 오름을 등록할 수 있습니다.|

---

### 🧭 3. 스탬프 기능

| 위치 기반 스탬프 인증 | 스탬프 규칙 확인 |
|:--:|:--:|
| <img src="./screenshots/위치%20기반%20스탬프%20인증.gif" width="250"/> | <img src="./screenshots/스탬프%20찍기%20-%20위치%20확인,%20스탬프%20규칙%20확인.gif" width="250"/>  |
|GPS를 활용하여 방문한 오름을 인증하고 스탬프를 획득할 수 있습니다.|위치 조건 및 스탬프 인증 규칙을 사전에 안내합니다.|

---

### 📝 4. 리뷰 기능

| 후기 확인 | 후기 작성 | 후기 삭제 |
|:--:|:--:|:--:|
| <img src="./screenshots/상세%20화면의%20오름%20후기%20확인.gif" width="250"/>  | <img src="./screenshots/후기%20작성.gif" width="250"/> | <img src="./screenshots/후기%20제거.gif" width="250"/>  |
|다른 사용자의 오름 방문 후기를 열람할 수 있습니다.|오름에 대한 나만의 후기를 직접 작성할 수 있습니다. |작성한 리뷰를 자유롭게 삭제할 수 있습니다.|

---

### ❤️ 5. 마이페이지

| 관심/스탬프 오름 목록 |
|:--:|
| <img src="./screenshots/유저%20관심%20오름%20및%20스탬프%20확인%20-%20후기%20저장%20및%20삭제.gif" width="250"/>  |
|내가 등록한 관심 오름, 획득한 스탬프, 작성한 리뷰를 한눈에 확인할 수 있습니다.|

---

### 👤 6. 회원가입 흐름

| 닉네임 설정 | 약관 동의 |
|:--:|:--:|
| <img src="./screenshots/회원가입%20화면%20-%20닉네임%20규칙%20확인.gif" width="250"/>  | <img src="./screenshots/회원가입%20화면%20-%20약관%20동의.gif" width="250"/>  |
|닉네임 규칙을 확인하고 설정하여 회원가입을 시작할 수 있습니다.|간편한 약관 동의 과정을 통해 회원가입을 마칠 수 있습니다.|

---

### 🚀 7. 스플래시 화면

| 앱 실행 |
|:--:|
| <img src="./screenshots/스플래시%20화면.gif" width="250"/>  |
|앱 실행 시 깔끔한 스플래시 화면을 통해 자연스럽게 시작됩니다.|
