# Spring WebFlux WebClient TEST

### 목적

- Spring MVC에서 WebClient 설정 및 사용 테스트 목적이다.
  
- 한계는 있겠지만, Blocking을 사용할 필요가 없는 작업(호출 후 망각,   
  fire and forget 작업)에서, Mono, Flux를 받아서 비동기로 처리하면,  
  일반적인 멀티 쓰레딩 보다 효율적이고 적은 자원으로 (심지어 병렬 작업까지)  
  처리할 수 있다. (TestWcBeanAsyncController 참조)
  
#### WebClientUtil 개발 방향

- retry, fallback은 서비스 영역에서 Spring Retry, resilience4j와 같은 lib 사용하면 된다.  
  (Web Flux인 경우에는 당연히 더 고민해야 한다. subscribe의 onError event시 retry를 해야 되니까...)

- 보통 WebClient에서 호출하는 api응답값의 갯수는 단일값이거나 수가 정해져 있는 경우가 많다.  
  따라서 Default로 Mono를 사용한다.

- 다만 Spring WebFlux가 아닌 Spring MVC를 사용하는 상황에서 Mono.block()이나   
  Flux.blockFirst()와 같은 blocking 함수를 사용하면, 모든 호출이 main thread에서 호출되기 때문에,  
  Reactive Pipeline을 사용하는 장점이 없어진다.

- 대안으로 완벽한 Reactive 호출은 아니지만, Lazy Subscribe를 통해 Stream또는 Iterable로 변환시킬수  
  있는 Flux.toStream(),Flux.toIterable() 함수를 사용하는 것을 권장한다.  
  https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#toStream--  
  Mono인 경우 bodyToMono후 Flux()로 변경후 사용해야 한다. 번거롭기 때문에 Mono<List 형식으로   
  반환하는 default method외에 Flux로 반환하는 Flux 형식의 method도 제공한다.   
  (단일값은 당연히 상관없다.)  
  Lazy Subscribe는 별거 아니고 Stream의 특성으로, 호출시 실행하는 건데 이것들도 결국 클라입장에서는 Blocking 이다.  
  그래도 block() 보다는 위 방식을 더 추천한다.
  
- util/WebClientUtil : WebClient Util
  
### Config 설명

- WebClientConfig : 권장 WebClient bean 설정
- WebClientPoolConfig : react netty 글로벌 리소스를 사용하지 않고, 별도 pool을 설정하여 사용할때 설정    
  동기, 비동기가 혼합되면 제성능을 발휘하지 못한다. 이런 경우 비동기용 별도 pool을 설정해서  
  해당 pool에서는 비동기만 실행하게 하면 좋은 성능을 얻을 수 있다.
- CbConfig : Circuit Breaker Bean 설정 (resilence4j 사용)

### Test Controller 설명

- TestRcvController : API Target Controller
- TestWcController : Default WebClient를 사용한 예제
- TestWcBeanController : Bean설정으로 WebClient를 사용한 예제
- TestWcBeanAsyncController : Bean설정으로 Async하게 WebClient를 사용한 예제
- TestCbAnnoController : Circuit Breaker, fallback을 annotation으로 사용한 예제
- TestCbBeanController : Circuit Breaker, fallback을 Bean으로 사용한 예제

### TEST 사항
- HTTP METHOD(GET, POST, PUT, DELETE)
- 동기 호출
- 비동기 호출
- TIMEOUT
- ERROR
- Circuit Break, Fallback
- 글로벌 리소스, 전용 리소스 동시 사용

### 참고

- TestWc.http : IntelliJ Rest API Test Tool을 이용한 호출 설정 파일
- Pool 설정이 WebClient나 React 버전에 따라 사용하는 메소드 방식이 다르다.  
  구글 예제에 다양한 종류가 있지만, deprecated된 메소드도 많다.  
  여기서는 현재 최신 버전인 Spring Boot 2.5x 버전을 기준으로 했다.  
- ParameterizedTypeReference와 같은 Java Super Token에 대해 모르신다면, 다음 링크 참조  
  https://homoefficio.github.io/2016/11/30/%ED%81%B4%EB%9E%98%EC%8A%A4-%EB%A6%AC%ED%84%B0%EB%9F%B4-%ED%83%80%EC%9E%85-%ED%86%A0%ED%81%B0-%EC%88%98%ED%8D%BC-%ED%83%80%EC%9E%85-%ED%86%A0%ED%81%B0/
  


