# Math Move - 설계 문서

## 1. 앱 개요

**Math Move**는 초등학교 1~2학년을 대상으로 한 수학 학습 게임 앱입니다.
카메라로 사용자의 몸 동작(손/발 들기)을 인식하여 수학 문제의 답을 선택하는 방식으로,
놀이를 통해 수학 능력을 기를 수 있습니다.

- **회사명**: PinkMandarin
- **패키지명**: `com.pinkmandarin.mathmove`
- **플랫폼**: Android (Kotlin)
- **최소 SDK**: API 26 (Android 8.0)
- **타겟 SDK**: API 34 (Android 14)

---

## 2. 핵심 기능

### 2.1 스테이지 시스템
- 1단계부터 시작, 클리어 시 다음 단계 해금
- 단계 수는 무한 (알고리즘으로 자동 생성)
- 각 스테이지: 5문제 출제
- 전부 맞추면 클리어, 별(1~3개) 평가

### 2.2 수학 문제 생성
| 스테이지 | 난이도 | 문제 유형 |
|---------|--------|----------|
| 1~5 | 한 자리 + 한 자리 (합 ≤ 10) | 덧셈 |
| 6~10 | 한 자리 - 한 자리 (결과 ≥ 0) | 뺄셈 |
| 11~15 | 한 자리 + 한 자리 (합 ≤ 18) | 덧셈 |
| 16~20 | 두 자리 ± 한 자리 | 덧셈/뺄셈 혼합 |
| 21~30 | 두 자리 ± 두 자리 | 덧셈/뺄셈 혼합 |
| 31~40 | 한 자리 × 한 자리 | 곱셈 |
| 41~50 | 두 자리 ± 두 자리 + 곱셈 혼합 | 혼합 |
| 51+ | 난이도 점진적 증가 (범위 확장) | 혼합 |

- 같은 스테이지 내 문제는 동일 난이도, 랜덤 생성
- 보기는 4개 (정답 1개 + 오답 3개)
- 오답은 정답 ±1~5 범위에서 생성 (중복/음수 제외)

### 2.3 동작 인식 (Pose Detection)
- **ML Kit Pose Detection** 사용
- 인식 동작 4가지 (보기 4개에 매핑):
  - 왼손 들기 → 보기 A
  - 오른손 들기 → 보기 B
  - 왼발 들기 → 보기 C
  - 오른발 들기 → 보기 D
- 카메라 프리뷰에 실시간으로 사용자 모습 표시
- 동작 유지 1.5초 시 답 선택 확정 (오인식 방지)

### 2.4 게임 플로우
```
스플래시 → 로그인 → 홈(스테이지 선택) → 광고 시청 → 게임 플레이 → 결과 → 홈
```

1. **스플래시**: 앱 로고, 로딩
2. **로그인**: Google 로그인 (Firebase Auth)
3. **홈 화면**: 스테이지 맵 (스크롤), 현재 진행 상태, 랭킹 버튼
4. **광고**: 게임 시작 전 전면 광고 (AdMob Interstitial)
5. **게임 플레이**: 카메라 + 문제 + 보기 4개 + 타이머
6. **결과**: 점수, 별, 클리어 여부

### 2.5 랭킹 시스템
- 스테이지별 랭킹 (클리어 시간 + 정답률 기준)
- 전체 랭킹 (최고 클리어 스테이지 기준)
- Firebase Firestore에 기록 저장

### 2.6 다국어 지원
- 한국어 (기본)
- 영어
- 일본어
- Android strings resource 방식 적용

---

## 3. 기술 스택

| 구분 | 기술 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose |
| 아키텍처 | MVVM + Clean Architecture |
| DI | Hilt |
| 카메라 | CameraX |
| 포즈 인식 | ML Kit Pose Detection |
| 인증 | Firebase Authentication (Google Sign-In) |
| DB | Firebase Firestore |
| 광고 | Google AdMob |
| 비동기 | Kotlin Coroutines + Flow |
| 네비게이션 | Navigation Compose |
| 로컬 저장소 | DataStore Preferences |

---

## 4. 프로젝트 구조

```
app/src/main/java/com/pinkmandarin/mathmove/
├── MathMoveApp.kt                  # Application class (Hilt)
├── MainActivity.kt                  # Single Activity
├── di/                              # Hilt 모듈
│   ├── AppModule.kt
│   └── FirebaseModule.kt
├── data/                            # Data Layer
│   ├── repository/
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── GameRepositoryImpl.kt
│   │   └── RankingRepositoryImpl.kt
│   └── model/
│       ├── UserData.kt
│       ├── StageRecord.kt
│       └── RankingEntry.kt
├── domain/                          # Domain Layer
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── GameRepository.kt
│   │   └── RankingRepository.kt
│   ├── model/
│   │   ├── MathProblem.kt
│   │   ├── Stage.kt
│   │   ├── GameResult.kt
│   │   └── PoseAction.kt
│   └── usecase/
│       ├── GenerateProblemUseCase.kt
│       ├── CheckAnswerUseCase.kt
│       ├── GetStagesUseCase.kt
│       ├── SaveResultUseCase.kt
│       └── GetRankingUseCase.kt
├── presentation/                    # Presentation Layer
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── splash/
│   │   ├── SplashScreen.kt
│   │   └── SplashViewModel.kt
│   ├── login/
│   │   ├── LoginScreen.kt
│   │   └── LoginViewModel.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── StageItem.kt
│   ├── game/
│   │   ├── GameScreen.kt
│   │   ├── GameViewModel.kt
│   │   ├── CameraPreview.kt
│   │   ├── PoseAnalyzer.kt
│   │   ├── ProblemOverlay.kt
│   │   └── AnswerChoices.kt
│   ├── result/
│   │   ├── ResultScreen.kt
│   │   └── ResultViewModel.kt
│   ├── ranking/
│   │   ├── RankingScreen.kt
│   │   └── RankingViewModel.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── pose/                            # Pose Detection
│   ├── PoseDetectorProcessor.kt
│   └── PoseClassifier.kt
└── util/
    ├── AdManager.kt
    └── Constants.kt
```

---

## 5. Firebase 구조 (Firestore)

```
users/
  {userId}/
    displayName: string
    photoUrl: string
    maxClearedStage: number
    createdAt: timestamp

stageRecords/
  {userId}/
    records/
      {stageId}/
        stage: number
        cleared: boolean
        stars: number          # 1~3
        bestTime: number       # milliseconds
        correctCount: number
        totalCount: number
        playedAt: timestamp

rankings/
  stages/
    {stageId}/
      entries/
        {userId}/
          displayName: string
          photoUrl: string
          time: number
          stars: number
          playedAt: timestamp
  global/
    entries/
      {userId}/
        displayName: string
        photoUrl: string
        maxStage: number
        totalStars: number
```

---

## 6. 게임 화면 레이아웃 (GameScreen)

```
┌──────────────────────────────┐
│  Stage 3    ⏱ 00:45    ❤❤❤  │  ← 상단바: 스테이지, 타이머, 라이프
│                              │
│  ┌────────────────────────┐  │
│  │                        │  │
│  │    카메라 프리뷰        │  │
│  │    (사용자 모습)        │  │
│  │                        │  │
│  │   ┌──────────────┐     │  │
│  │   │  3 + 5 = ?   │     │  │  ← 문제 (카메라 위 오버레이)
│  │   └──────────────┘     │  │
│  │                        │  │
│  └────────────────────────┘  │
│                              │
│  🤚 A: 7   ✋ B: 8          │  ← 보기 (동작 아이콘 + 답)
│  🦶 C: 9   🦶 D: 6          │
│                              │
│  [동작 인식 상태 표시바]      │  ← 현재 인식된 동작 + 진행 바
└──────────────────────────────┘
```

---

## 7. 동작 인식 상세

### ML Kit Pose Detection 활용
- 33개 관절 포인트 중 사용하는 것:
  - 왼쪽 손목 (LEFT_WRIST), 왼쪽 어깨 (LEFT_SHOULDER)
  - 오른쪽 손목 (RIGHT_WRIST), 오른쪽 어깨 (RIGHT_SHOULDER)
  - 왼쪽 발목 (LEFT_ANKLE), 왼쪽 엉덩이 (LEFT_HIP)
  - 오른쪽 발목 (RIGHT_ANKLE), 오른쪽 엉덩이 (RIGHT_HIP)

### 동작 판별 로직
```
왼손 들기: LEFT_WRIST.y < LEFT_SHOULDER.y - threshold
오른손 들기: RIGHT_WRIST.y < RIGHT_SHOULDER.y - threshold
왼발 들기: LEFT_ANKLE.y < LEFT_HIP.y - threshold (또는 x 이동량)
오른발 들기: RIGHT_ANKLE.y < RIGHT_HIP.y - threshold (또는 x 이동량)
```

### 안정성 처리
- 동작 감지 후 1.5초 유지 시 확정
- 진행 바(Progress Bar)로 시각적 피드백
- 동작 중단 시 리셋
- 한 번에 하나의 동작만 인식 (우선순위: 손 > 발)

---

## 8. 광고 전략

- **전면 광고 (Interstitial)**: 게임 시작 전 매번 표시
- AdMob 사용
- 아이 대상이므로 COPPA 준수 설정 필요
- `tagForChildDirectedTreatment(true)` 설정

---

## 9. 다국어 리소스 구조

```
res/
├── values/           # 기본 (한국어)
│   └── strings.xml
├── values-en/        # 영어
│   └── strings.xml
└── values-ja/        # 일본어
    └── strings.xml
```

---

## 10. 구현 우선순위 (Phase)

### Phase 1: 기본 구조 + 인증
- [x] 프로젝트 생성 (Kotlin, Compose)
- [ ] Hilt DI 설정
- [ ] Firebase 연동 (Auth + Firestore)
- [ ] Google 로그인
- [ ] 스플래시 / 로그인 화면

### Phase 2: 게임 코어
- [ ] 수학 문제 생성 알고리즘
- [ ] 스테이지 시스템
- [ ] 게임 화면 UI (문제 + 보기)
- [ ] 타이머 + 라이프 시스템

### Phase 3: 카메라 + 포즈 인식
- [ ] CameraX 프리뷰
- [ ] ML Kit Pose Detection 연동
- [ ] 동작 판별 로직
- [ ] 동작 → 답 선택 연결

### Phase 4: 부가 기능
- [ ] 결과 화면 + 별 시스템
- [ ] 스테이지 맵 (홈 화면)
- [ ] 랭킹 시스템
- [ ] AdMob 광고

### Phase 5: 다국어 + 마무리
- [ ] 다국어 리소스 (ko, en, ja)
- [ ] UI/UX 폴리시
- [ ] 사운드 효과
- [ ] 테스트 + 버그 수정
