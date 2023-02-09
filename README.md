# 깃썸 - 개발자를 위한 소셜 데이팅 웹앱<br>


![1](https://user-images.githubusercontent.com/110834052/217885730-dd289a50-7207-4a89-bfd5-f9f6bf34047f.png)

![2](https://user-images.githubusercontent.com/110834052/217886141-c7e8c1ed-2cc9-44ab-9d9f-0beff1091acc.png)

![3](https://user-images.githubusercontent.com/110834052/217886152-a73d1f18-0336-45f0-ba72-d9cad4eaafbe.png)

![4](https://user-images.githubusercontent.com/110834052/217886160-b36682c6-a2b1-4a3c-8475-ecb89c7f4a1f.png)

![5](https://user-images.githubusercontent.com/110834052/217886181-7d571d0e-6815-42d0-b25f-853f9cb5f88a.png)

## 개발자들을 위한 소셜 데이팅 웹

[깃썸 하러가기](https://main.d20iwpsyv6d6f7.amplifyapp.com/)

**[Front-End Github →](https://github.com/developer-dating/gitssum-FE)**

**[Back-End Github](https://github.com/developer-dating/gitssum-BE) [→](https://github.com/bokjiri/back-end)**

![Untitled](https://user-images.githubusercontent.com/110834052/217886345-3a8e1e6a-d3b7-47ec-8400-414d1e1bff70.png)


## 📚 아키텍쳐

![아키택쳐깃썸2 PNG](https://user-images.githubusercontent.com/110834052/217886501-8c32747e-704e-4fc0-9da7-299b8e33045d.png)



# 아키텍처 도입 배경

### FE

- **React Query**
    
    서버의 값을 클라이언트에 가져오거나, 캐싱, 값 업데이트, 에러핸들링 등 비동기 과정을 조금 더 편리하게 사용하기 위해 리액트 쿼리를 도입하였습니다.
    
- **Recoil**
    
    전역 상태 관리 라이브러리로는 리덕스에 비해 코드가 간결하고 직관적인 로직을 구성할 수 있으며, 리액트 쿼리와도 호환성 및 접근성이 뛰어난 리코일을 채택하였습니다.
    
- **Tailwind**
    
    또한 생산적인 디자인 시스템 구축을 위하여 코드의 유지보수에 뛰어나고 디자인툴인 피그마와도 잘 맞는 프레임워크인 테일윈드를 도입하였습니다.
    

### BE

- **WebSocket, Stomp**
    
    서버의 이벤트를 클라이언트로 보내는 방법 중에 서버에 부하가 덜 오게끔 양방향 통신이 가능한 websocket을 사용했습니다. 채팅 기능을 위해서 기반으로 동작하는 stomp를 이용해 topic을 생성하도록 했습니다.
    
- **Redis**
    
    비동기 통신을 하는 채팅 구현을 위해 메시지 브로커로는 레디스를 사용하였습니다. 여러 가지 메시지 브로커 중에 레디스를 선택한 이유는 Pub-sub구조여서 일대일 일대다 형태가 모두 가능하고 Key-Value를 사용하는 In-Memory 데이터 저장소이기 때문에 단기 메시지를 처리하기에는 매우 빠를 것이라고 판단하였기 때문입니다.
    
- **Oauth2**
    
    로그인을 구현할때 일반로그인은 유저들의 편의성을 떨어뜨린다고 생각하여 소셜 로그인 처리방식으로 편의성을 향상하고자 하였습니다.
    소셜 로그인 중, 가장 보편적인 카카오, 구글 그리고 개발자 데이팅앱이기 때문에 깃허브까지 선택하게되었습니다.

![Untitled](https://user-images.githubusercontent.com/110834052/217886569-9750dd90-74ce-4dc5-98c6-66f3fc2451bc.png)

## 👋 주요기술

**스**택과 지역 등 이용자의 취향 별 개발자 추천

맘에 드는 개발자에게 좋아요

상호 좋아요 일 경우 채팅 방 생성

실시간 채팅으로 상대와의 대화

![Untitled](%E1%84%80%E1%85%B5%E1%86%BA%E1%84%8A%E1%85%A5%E1%86%B7%20-%20%E1%84%80%E1%85%A2%E1%84%87%E1%85%A1%E1%86%AF%E1%84%8C%E1%85%A1%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%8B%E1%85%B1%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%89%E1%85%A9%E1%84%89%E1%85%A7%E1%86%AF%20%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%B5%E1%86%BC%20%E1%84%8B%E1%85%B0%E1%86%B8%E1%84%8B%E1%85%A2%E1%86%B8%20051c687f0b7245368ba77165a32264be/Untitled%202.png)

## 💯트러블슈팅

- profile 페이지 폼데이터 변환 과정 에러
    - 프로필 설정 부분 폼데이터 과정중 img가 [object] 형식으로 가는 에러
    - 해결 headers에 Content-Type를 multipart/form-data로 설정 후 에러를 해결 하였습니다.
- 추천 페이지 Internal server error 500에러
    - QueryCache 는 queryKey 만 같다면 error response도 캐싱하고 그 이유로 같은 key로 query를 실행하면 캐싱되어 있던 error response를 사용한다. 별다른 설정을 하지 않았다면 캐시된 값을 사용하는 동시에 API request를 보내기 때문에 아래 코드를 queryClient.clear() 사용해 캐시 삭제 후 해결 완료
- 유저테이블 구현 시 문제점
    
    소셜 로그인 시 받아오는 정보와 프로필 등록할 때 받아오는 정보를 User table 하나에 넣는 방법이 아니라 양방향 onetoone 연관 관계를 맺어서 서로 다른 테이블에 나눠서 넣으려고 시도하였습니다.
    양방향 onetoone 연관 관계를 맺으면 N+1 문제가 발생할 것을 방지하기 위해 하나의 테이블로 만들어 소셜 로그인 시 정보들을 받아온 후 프로필 등록 시 받는 정보들을 putmappping을 통해 받아오는 방법을 선택했습니다.
    
- TimeStamped 로컬시간 설정 에러
    
    timestamped 클래스에서 LocalDateTime을 통해 받아와서 mySQL db에 시간대를 저장할 때 9시간 차이가 나는 문제점이 생겨서 실시간 채팅 메시지가 보내진 시간 역시 잘못 되어 있었습니다.
    ubuntu 서버 상에 있는 localtime을 확인해보니 미국 기준이어서 "sudo timedatectl set-timezone Asia/Seoul"를 통해 한국 기준으로 바꾸어 해결하였습니다.
    

![Untitled](%E1%84%80%E1%85%B5%E1%86%BA%E1%84%8A%E1%85%A5%E1%86%B7%20-%20%E1%84%80%E1%85%A2%E1%84%87%E1%85%A1%E1%86%AF%E1%84%8C%E1%85%A1%E1%84%85%E1%85%B3%E1%86%AF%20%E1%84%8B%E1%85%B1%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%89%E1%85%A9%E1%84%89%E1%85%A7%E1%86%AF%20%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%B5%E1%86%BC%20%E1%84%8B%E1%85%B0%E1%86%B8%E1%84%8B%E1%85%A2%E1%86%B8%20051c687f0b7245368ba77165a32264be/Untitled%202.png)

[👩🏻‍💻제작자들🧑🏻‍💻](https://www.notion.so/55612a1e3fee471cb575b33fd59cb371)

