# 베이스 이미지 지정
FROM openjdk:17

# 컨테이너 내에서 사용할 변수 지정
ARG JAR_FILE=build/libs/*.jar

# 변수를 이용해 컨테이너의 app.jar로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작되었을 때 스크립트 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]