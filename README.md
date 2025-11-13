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
https://github.com/kimdaae328/actions-app/blob/master/.github/workflows/ec2_aws_deploy.yml

## Dockerfile
https://github.com/kimdaae328/actions-app/blob/master/Dockerfile

## CI/CD 파이프라인 흐름
<img width="1920" height="1080" alt="509945194-f95425b9-dcf0-43b4-b5e1-5d1038444235" src="https://github.com/user-attachments/assets/0054d23f-9f31-4cbb-8b18-6596a6729bda" />

