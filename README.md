# CI/CD 파이프라인 구축

## Docker (컨테이너 기반 가상화 기술)

**Docker**는 애플리케이션을 실행에 필요한 모든 환경(라이브러리, 설정, 코드 등)을  
**하나의 컨테이너**로 묶어 어디서나 동일한 환경에서 동작하도록 하는 기술이다.  
즉, *"내 로컬에서는 되는데 서버에서 안 돼"* 같은 문제를 해결한다.

### 주요 개념

| 개념 | 설명 |
|------|------|
| **Image** | 실행 가능한 환경과 코드를 담은 템플릿 (컨테이너의 설계도) |
| **Container** | 이미지를 실제로 실행한 인스턴스 (실행 중인 애플리케이션) |
| **Dockerfile** | 이미지를 자동으로 생성하기 위한 설정 파일 |
| **Docker Hub** | 도커 이미지를 공유하거나 배포할 수 있는 저장소 |

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
# → 적용을 위해 재접속 필요 (putty 재접속)
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

## CI/CD (지속적 통합 & 지속적 배포)

| 개념 | 설명 |
|------|------|
| **CI (Continuous Integration)** | 코드 변경 사항을 주기적으로 통합하고 자동으로 빌드 및 테스트하는 과정 |
| **CD (Continuous Deployment)** | 빌드된 애플리케이션을 자동으로 서버에 배포하는 과정 |

**CI/CD**는 개발자가 코드를 `git push` 하는 순간  
자동으로 **빌드(Build) → 테스트(Test) → 배포(Deploy)** 가 진행되는 자동화 파이프라인을 의미한다.

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

> 모든 민감 정보는 `.yml` 파일 내부에서 `${{ secrets.변수명 }}` 형태로 참조한다.

## GitHub Actions (워크플로우 자동화 도구)

**GitHub Actions**는 GitHub에서 제공하는 **CI/CD 서비스**로,  
`.github/workflows/` 폴더에 `.yml` 파일을 작성하여 배포 과정을 자동화한다.

예를 들어, **`ec2_aws_deploy.yml`** 파일을 작성하면 다음 과정을 자동으로 수행한다.

1. **master 브랜치**에 코드가 푸시되면 워크플로우가 실행된다.  
2. **EC2 서버에 SSH로 접속**한다.  
3. 최신 코드를 **전송하고 Docker로 빌드**한다.  
4. 이전 컨테이너를 **중단하고 새 컨테이너를 실행**한다.

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

## Dockerfile 예시
```bash
# 1단계: 빌드용 이미지
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build -x test

# 2단계: 실행용 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/kok-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## CI/CD 파이프라인 흐름
```bash
개발자가 코드 push
      ↓
GitHub Actions 트리거
      ↓
코드 빌드 & 테스트
      ↓
EC2 서버에 자동 배포
      ↓
Docker 컨테이너 재시작 및 서비스 업데이트
```

## 정리
- **Docker**로 환경 일관성 확보  
- **GitHub Actions**로 자동 빌드 & 배포  
- **Secrets**로 보안성 유지  
- **EC2**에서 Docker 실행으로 운영 자동화  

결과적으로, 단 한 번의 **`git push`** 만으로  
**“코드 → 서버 배포 → 서비스 실행”** 까지 완전 자동화된 파이프라인을 구축했다.
