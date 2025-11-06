# CI/CD 파이프라인 구축

## Docker (컨테이너 기반 가상화 기술)

**Docker**는 애플리케이션을 실행에 필요한 모든 환경(라이브러리, 설정, 코드 등)을 **하나의 컨테이너**로 묶어 어디서나 동일한 환경에서 동작하도록 하는 기술이다.

---

### 주요 개념

- **Image**  
  실행 가능한 환경과 코드를 담은 **템플릿**으로, 컨테이너의 **설계도** 역할을 한다.

- **Container**  
  이미지를 실제로 **실행한 인스턴스**로, 실행 중인 **애플리케이션**을 의미한다.

- **Dockerfile**  
  이미지를 **자동으로 생성하기 위한 설정 파일**로, 어떤 환경과 명령으로 이미지를 만들지 정의한다.

---

## Docker 설치 및 기본 명령어
```bash
# 도커 설치
sudo apt install docker.io -y

# 서비스 등록 및 시작
sudo systemctl enable docker
sudo systemctl start docker

# 실행 상태 확인
sudo systemctl status docker

# 사용자 권한 등록
sudo usermod -aG docker ubuntu
# → 적용을 위해 재접속 필요 (종료후 putty 재접속)
```

## 자주 사용하는 명령어
```bash
# 실행 중인 컨테이너 목록
docker ps

# 모든 컨테이너 목록
docker ps -a

# 실행 중인 이미지 목록
docker images

# 전체 이미지 목록
docker images -a

# 컨테이너 중지 / 삭제
docker stop [컨테이너명]
docker rm [컨테이너명]

# 이미지 삭제
docker rmi [이미지ID]
```

---

## GitHub Secrets 설정

GitHub Actions에서 서버 접속 정보나 키를 직접 코드에 넣는 것은 보안상 매우 위험하다.  
이를 위해 **Settings → Secrets and variables → Actions** 에 환경 변수를 등록해 사용한다.

| 이름 | 설명 | 예시 |
|------|------|------|
| **EC2_HOST** | 배포 대상 EC2의 IP | `43.202.xxx.xxx` |
| **EC2_USER** | EC2 계정명 | `ubuntu` |
| **EC2_KEY** | EC2 SSH 개인키 (.pem 파일 전체 복사) | `-----BEGIN OPENSSH PRIVATE KEY----- ...` |
| **AWS_ACCESS_KEY_ID** | AWS 액세스 키 | `AKIA...` |
| **AWS_SECRET_ACCESS_KEY** | AWS 시크릿 키 | `Jd8kL...` |
| **AWS_S3_BUCKET** | S3 버킷명 | `kok-bucket` |
| **AWS_REGION** | AWS 리전 | `ap-northeast-2` |

---

## GitHub Actions (워크플로우 자동화 도구)

**GitHub Actions**는 GitHub에서 제공하는 **CI/CD 서비스**로,  
`.github/workflows/` 폴더에 `.yml` 파일을 작성하여 배포 과정을 자동화한다.

예를 들어, **`ec2_aws_deploy.yml`** 파일을 작성하면 다음 과정을 자동으로 수행한다.

1. **master 브랜치**에 코드가 푸시되면 워크플로우가 실행된다.  
2. **EC2 서버에 SSH로 접속**한다.  
3. 최신 코드를 **전송하고 Docker로 빌드**한다.  
4. 이전 컨테이너를 **중단하고 새 컨테이너를 실행**한다.

---

## Workflow 파일 예시
```bash
name: ec2_aws_deploy

on:
  push:
    branches:
      - master

jobs:
  deploy:
    runs-on: ubuntu-22.04

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up SSH
        run: |
          mkdir -p ~/.ssh
          echo "${{ secrets.EC2_KEY }}" > ~/.ssh/id_rsa
          chmod 600 ~/.ssh/id_rsa
          ssh-keyscan -H ${{ secrets.EC2_HOST }} >> ~/.ssh/known_hosts

      - name: Copy project to EC2
        run: |
          rsync -avz -e "ssh -i ~/.ssh/id_rsa" . ${{ secrets.EC2_USER }}@${{ secrets.EC2_HOST }}:/home/ubuntu/docker-kok

      - name: Build & Run Docker on EC2
        run: |
          ssh -i ~/.ssh/id_rsa ${{ secrets.EC2_USER }}@${{ secrets.EC2_HOST }} << 'EOF'
            cd docker-kok
            docker stop app || true
            docker rm app || true
            docker rmi $(docker images -q) || true
            docker build -t docker-kok .
            docker run -d -p 10000:10000 --name app docker-kok
          EOF
```

## Dockerfile
```bash
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

ARG EMAIL_ID
ENV EMAIL_ID=${EMAIL_ID}

ARG EMAIL_PASSWORD
ENV EMAIL_PASSWORD=${EMAIL_PASSWORD}

ARG EC2_DATABASE_PASSWORD
ENV EC2_DATABASE_PASSWORD=${EC2_DATABASE_PASSWORD}

# 작업 디렉토리 설정
WORKDIR /docker-kok

# Gradle wrapper 및 프로젝트 파일 복사
# COPY <src> <dest>
# <src>: 호스트(현재 디렉토리)의 경로
# <dest>: 컨테이너 내부의 경로
COPY . .

# Gradle 빌드 실행 (build/libs/*.jar 생성됨)
RUN chmod +x ./gradlew && ./gradlew build -x test

# 2단계: 실제 실행 이미지 (최종 이미지)
FROM eclipse-temurin:17-jre

# 타임존 설정 (한국 시간)
ENV TZ=Asia/Seoul

# JAR 복사 (위 단계에서 생성된 JAR)
COPY --from=build /docker-kok/build/libs/kok-0.0.1-SNAPSHOT.jar kok.jar

# 포트 오픈 (Spring Boot 기본 포트)
EXPOSE 10000

# 실행 명령
ENTRYPOINT ["java", "-jar", "kok.jar"]
```

## CI/CD 파이프라인 흐름
<img width="1920" height="1080" alt="509945194-f95425b9-dcf0-43b4-b5e1-5d1038444235" src="https://github.com/user-attachments/assets/0054d23f-9f31-4cbb-8b18-6596a6729bda" />

