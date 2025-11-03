# 1단계: 빌드용 이미지
FROM eclipse-temurin:17-jdk AS build

# 빌드 시 필요한 정보 받아오기
ARG EC2_HOST
ENV EC2_HOST=${EC2_HOST}

ARG EC2_DATABASE_HOST
ENV EC2_DATABASE_HOST=${EC2_DATABASE_HOST}

ARG JWT_SECRET
ENV JWT_SECRET=${JWT_SECRET}

ARG AWS_ACCESS_KEY_ID
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}

ARG AWS_SECRET_ACCESS_KEY
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

ARG AWS_S3_BUCKET
ENV AWS_S3_BUCKET=${AWS_S3_BUCKET}

ARG AWS_REGION
ENV AWS_REGION=${AWS_REGION}

ARG KAKAO_CLIENT_ID
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}

ARG KAKAO_CLIENT_SECRET
ENV KAKAO_CLIENT_SECRET=${KAKAO_CLIENT_SECRET}

ARG NAVER_CLIENT_ID
ENV NAVER_CLIENT_ID=${NAVER_CLIENT_ID}

ARG NAVER_CLIENT_SECRET
ENV NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper 및 프로젝트 파일 복사
# COPY <src> <dest>
# <src>: 호스트(현재 디렉토리)의 경로
# <dest>: 컨테이너 내부의 경로
COPY . .

# Gradle 빌드 실행 (build/libs/*.jar 생성됨)
RUN chmod +x ./gradlew && ./gradlew build

# 2단계: 실제 실행 이미지 (최종 이미지)
FROM eclipse-temurin:17-jre

# 타임존 설정 (한국 시간)
ENV TZ=Asia/Seoul

# JAR 복사 (위 단계에서 생성된 JAR)
COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

# 포트 오픈 (Spring Boot 기본 포트)
EXPOSE 10000

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]