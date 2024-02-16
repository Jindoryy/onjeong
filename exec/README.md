# 포팅 매뉴얼

## 1. git clone 소스 클론 이후 빌드 & 배포할 수 있도록 정리한 문서

### 1-1. 개발환경

- kotlin 1.9.22
- Android Studio 17.0.7
- Java 17
- IntelliJ 17.0.9
- Spring boot 3.2.1
- Spring security 5.5.0

### 1-2. 환경변수

- gitignore
    
    .env
    

- docker-compose.yml

```xml
version: '3'

services:
  database:
    container_name: allfriend-mysql
    image: mysql
    restart: always
    env_file:
      - .env
    environment:
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - ./mysql/data:/var/lib/mysql
      - ./mysql/conf.d:/etc/mysql/conf.d
    command:
      - "mysqld"
      - "--character-set-server=utf8mb4"
      - "--collation-server=utf8mb4_unicode_ci"

  application:
    container_name: allfriend-backend
    restart: always
    image: jindoryy/allfriend
    volumes:
        - ./onjeong-firebase-admin-sdk.json:/app/onjeong-firebase-admin-sdk.json
    ports:
      - "8080:8080"
    env_file:
      - .env
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://allfriend-mysql:3306/${MYSQL_DATABASE}?useSSL=false&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
    depends_on:
      - database
```

- .env

```xml
SPRING_DATASOURCE_URL=<데이터 베이스 주소>
SPRING_DATASOURCE_USERNAME=<데이터 베이스 계정 이름>
SPRING_DATASOURCE_PASSWORD=<데이터 베이스 계정 비밀번호>
MYSQL_ROOT_PASSWORD=<데이터 베이스 계정 비밀번호>
MYSQL_DATABASE=<데이터 베이스 이름>
KAKAO_REST_KEY=<카카오 로그인 KEY 값>
KAKAO_REDIRECT_URI=<카카오 로그인 URI>
JWT_SECRET_KEY=<JWT KEY 값>
JWT_ISSUER=<JWT ISSUER 값>
PHONE_API_KEY=<전화번호 인증 API KEY 값>
PHONE_API_SECRET_KEY=<전화번호 인증 SECRETKEY 값>
PHONE_DOMAIN=<전화 번호 인증 주소>
CLOUD_AWS_CREDENTIALS_ACCESSKEY=<AWS S3 ACCESSKEY 값>
CLOUD_AWS_CREDENTIALS_SECRETKEY=<AWS S3 SECRETKEY 값>
OPENVIDU_SECRET=<OPENVIDU SECRET 값>
FIREBASE_ID=<FIREBASE ID 값>
WEATHER_API_KEY=<날씨 API KEY 값>
```

### ① EC2 서버 터미널에서 jenkins, java, docker, docker-compose 설치

```xml
sudo apt-get update

// jenkins
sudo apt-get install jenkins

// docker
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install docker-ce

// docker-compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

### ② app 폴더 생성 후 위의 .env, docker-compose.yml, 필요한 json 파일 생성

```xml
mkdir app
cd app

vi .env
vi docker-compose.yml
vi [필요한 json 파일명].json
```

### ③ jenkins pipeline script 작성 후 실행

```xml
pipeline {
    agent any
    
    environment {
        backendImageName = [docker hub image name]
        registryCredential = [jenkins에서 설정한 docker 접근 권한]
        
        releaseServerAccount = [EC2 계정]
        releaseServerUri = [배포 도메인]
    }
    
    tools {
        gradle "gradle8.5"
    }
        
    stages {
        stage('Git Clone') {
            steps {
                git branch: [branch name],
                    credentialsId: [jenkins에서 설정한 git 접근 권한],
                    url: [git url]
            }
        }
        stage('Jar Build') {
            steps {
                dir ('OnJeong_BE') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew build'
                }
            }
        }
        stage('Backend Image Build & DockerHub Push') {
            steps {
                dir('OnJeong_BE') {
                    script {
                        docker.withRegistry('', registryCredential) {
                            sh "docker buildx create --use --name mybuilder"
                            sh "docker buildx build --platform linux/amd64,linux/arm64 -t $backendImageName:$BUILD_NUMBER --push ."
                            sh "docker buildx build --platform linux/amd64,linux/arm64 -t $backendImageName:latest --push ."
                        }
                    }
                }
            }
        }
        stage('Before Service Stop') {
            steps {
                script {
                    sshagent(credentials: [[jenkins에서 설정한 EC2 접근 권한]]) {
                    sh '''
                    ssh -o StrictHostKeyChecking=no $releaseServerAccount@$releaseServerUri "cd app; sudo docker-compose down"
                    ssh -o StrictHostKeyChecking=no $releaseServerAccount@$releaseServerUri "cd app; sudo docker rmi $backendImageName:latest"
                    '''
                }
            }
        }
        stage('Service Start') {
            steps {
                script {
                    sshagent(credentials: [[jenkins에서 설정한 EC2 접근 권한]) {
                        sh '''
                            ssh -o StrictHostKeyChecking=no $releaseServerAccount@$releaseServerUri "cd app; sudo docker-compose -f docker-compose.yml up -d"
                        '''
                    }
                }
            }
        }
    }
}
```

## 2. 프로젝트에서 사용하는 외부 서비스 정보를 정리한 문서

- 소셜 로그인
    - 카카오 로그인 API
        
        https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
        
- Open API
    - 날씨
        
        https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084
        
        https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15059468
        
    - 복지 서비스
        
        [https://www.data.go.kr/data/15113968/openapi.do#/API 목록/getsupportConditions](https://www.data.go.kr/data/15113968/openapi.do#/API%20%EB%AA%A9%EB%A1%9D/getsupportConditions)
        
- 알림
    - Firebase
        
        https://firebase.google.com/products/cloud-messaging?hl=ko

# ※ 노션 링크
https://shelled-twill-2d3.notion.site/fa2d34ef8b0b460f9cd948740d7ff283?pvs=4
