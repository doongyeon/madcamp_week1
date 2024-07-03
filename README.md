## MAD-CONNECT


## OUTLINE
![connect](https://github.com/doongyeon/madcamp_week1/assets/161582130/301c9c54-69b3-4a25-b168-17551ed7f212)
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/090bdb5e-325a-48b2-85b3-ffd2fbec2653" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/17e2ef59-fbb3-4422-89de-17ee4c46f55b" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/81e5b9b3-8ce4-40ed-9b15-cdaeb13a4bb7" width="333"/>

## TEAM

KAIST 전산학부 19학번 김동연 <a href="https://github.com/doongyeon" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"></a>

고려대학교 컴퓨터학과 22학번 안지형 <a href="https://github.com/gina0520" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white"></a>

## TECH STACK

**Front-end** : Java

**IDE** : Android Studio

## DETAILS
### INTRO
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/abc123ba-8268-4fdb-a5fb-60a6c7778f8b" width="500"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/0ea0f0fc-dd9a-4ffe-8bc8-8237a5bad747" width="500"/>

- 앱을 실행시에 2개의 `splash` 화면을 이용해 애니메이션 효과를 주었습니다.
- `TabLayout` 과 `viewpager2` 를 이용해 3가지 탭을 구성했습니다.

### TAB1: 연락처
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/090bdb5e-325a-48b2-85b3-ffd2fbec2653" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/e2c60b6d-d603-4eac-bfd0-a362d479a717" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/042f5513-40d4-4e77-8e57-69f45027c9a0" width="333"/>

- 함께 몰입캠프에 참여 중인 모든 분반 메이트 및 운영진의 연락처를 확인할 수 있는 탭입니다.

  →  `SharedPreferences` 를 이용해 로컬 저장소의 연락처(Json 객체)를 불러온 이후, 이  데이터를 `ListView` 로 보여주었습니다. 

  → 로컬 저장소의 데이터를 이용하기에 앱을 껐다 켜도 데이터가 날라가지 않습니다.

- 한글 초성 검색 기능과 필터 기능(운영진, 분반별 구분)을 통해 메이트를 쉽게 찾을 수 있습니다.
- 연락처는 가나다순으로 정렬되어 있습니다.
- 연락처를 클릭하면 상세페이지를 볼 수 있습니다.
- 상세페이지에서 전화앱과 메시지앱으로 연결되는 버튼을 이용해 해당 메이트에게   전화를 걸거나 메시지를 보낼 수 있습니다.
- 상세페이지에서 QR코드 버튼을 누르면 나오는 QR코드를 통해 직접 연락처를 입력하지 않아도 스캔을 통해 연락처를 추가할 수 있습니다.
    
    → QR코드는 `ZXing` 라이브러리를 통해 구현했습니다.
    
- 연락처는 수기로 작성/QR코드 스캔 이렇게 두가지 방법을 통해 추가할 수 있습니다.
- 연락처 삭제도 가능합니다.

### TAB2: 공유 갤러리
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/17e2ef59-fbb3-4422-89de-17ee4c46f55b" width="333"/>

- 스마트폰에 저장된 이미지 중 원하는 이미지들로 갤러리를 구성할 수 있는 탭입니다.

  → 이미지들은 `SharedPreferences` 를 통해 경로가 저장됩니다.

   → `RecyclerViewer` 를 이용해 이미지들을 보여줍니다. 

- 한 화면에 더 많은 사진을 보고 싶은 경우 4열 배치로 전환할 수 있습니다.
- 이미지를 클릭하면 상세페이지로 이동합니다. 양 옆으로 슬라이드 하면서 다른 이미지들을 볼 수 있고, zoom-in/zoom-out도 가능합니다.

  → `ViewPager2` 를 이용해 이미지 슬라이드를 구현했고, 효율적인 이미지 관리를 위해 `Glide` 라이브러리를 사용했습니다.

- 다시 화면을 한번 더 터치하면 사진이 촬영된 날짜와 위치를 확인할 수 있습니다.

   → 이미지의 좌표를 지도상의 위치로 표시해주기 위해서 `Geocoder` 클래스를 사용했습니다.

### TAB3: 공유 캘린더
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/81e5b9b3-8ce4-40ed-9b15-cdaeb13a4bb7" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/18bd08c4-23de-48a0-afac-d46989da3616" width="333"/>
<img src="https://github.com/doongyeon/madcamp_week1/assets/161582130/c7ad2ad6-6a99-481b-8101-cb5b70e38a81" width="333"/>


- 몰입캠프의 공식 일정과 몰입캠퍼들이 참여할 수 있는 사적 일정을 정리하기 위한 공유 캘린더 탭입니다.
    
  → 커스텀 가능한 캘린더를 구현하기 위해 `material-calendarview` 라이브러리를 사용했습니다. 이벤트가 있는 날에는 날짜 아래에 점이 표시됩니다.
    
  →  이벤트도 연락처와 마찬가지로 `SharedPreferences` 를 이용해 로컬 저장소의 이벤트(Json 객체)를 불러온 이후, 이  데이터를 `ListView` 로 보여주었습니다. 
    

  → 로컬 저장소의 데이터를 이용하기에 앱을 껐다 켜도 데이터가 날라가지 않습니다.

- 공적인 일정과 사적인 공유 일정을 색깔로 구분하였고, 이벤트들은 시간대별로 정렬되어 있습니다.
- 지정한 날짜에 원하는 이벤트를 자유롭게 추가할 수 있습니다.
- 관심 이벤트를 등록하고 관심 이벤트만 확인할 수 있습니다.
- 해당하는 이벤트를 터치해 이벤트의 상세 정보를 확인할 수 있습니다.
- 이벤트 상세 정보의 작성자 이름을 클릭하면 작성자의 프로필로 이동합니다.
- 연락처에 있는 메이트들에게 초대장을 보낼 수 있습니다.

## APP LINK
[다운로드 링크](https://drive.google.com/file/d/1etD6dgV8-uE15ry57D0RGYDgZyZKLwFI/view?usp=sharing)
