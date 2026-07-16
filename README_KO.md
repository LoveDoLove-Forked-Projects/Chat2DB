<div align="center">
  <h1>Chat2DB</h1>
  <p><strong>개발자, DBA, 분석가 및 데이터 팀을 위한 AI 기반 데이터베이스 클라이언트이자 SQL 워크스페이스입니다.</strong></p>
</div>
<div align="center">
<a href="https://trendshift.io/repositories/11808" target="_blank"><img src="https://trendshift.io/api/badge/repositories/11808" alt="OtterMind%2FChat2DB | Trendshift" style="width: 250px; height: 55px;" width="250" height="55"/></a>
</div>

<div align="center">

[![ModelScope][Modelscope-image]][Modelscope-url]
[![Discord][discord-image]][discord-url]
[![X][x-image]][x-url]

[Modelscope-image]: https://img.shields.io/badge/modelscope-blue?style=flat-square\&logo=modelscope
[Modelscope-url]: https://modelscope.cn/
[discord-image]: https://img.shields.io/badge/-Join%20us%20on%20Discord-%237289DA.svg?style=flat&logo=discord&logoColor=white
[discord-url]: https://discord.gg/uNjb3n5JVN
[x-image]: https://img.shields.io/badge/X-%40Chat2DB_AI-000000?style=flat&logo=x&logoColor=white
[x-url]: https://x.com/Chat2DB_AI

</div>

<div align="center">
  <a href="./README.md"><img alt="README in English" src="https://img.shields.io/badge/English-d9d9d9"></a>
  <a href="./README_CN.md"><img alt="简体中文版自述文件" src="https://img.shields.io/badge/简体中文-d9d9d9"></a>
  <a href="./README_JA.md"><img alt="日本語のREADME" src="https://img.shields.io/badge/日本語-d9d9d9"></a>
  <a href="./README_ES.md"><img alt="README en español" src="https://img.shields.io/badge/Español-d9d9d9"></a>
  <a href="./README_KO.md"><img alt="한국어 README" src="https://img.shields.io/badge/한국어-d9d9d9"></a>

</div>

> 📣 **Chat2DB 팀의 새로운 소식입니다!**
> AI 네이티브 애플리케이션을 위한 새로운 배포 계층 **[Nubase](https://github.com/ottermind/nubase)**를 오픈 소스로 공개했습니다!
> Chat2DB가 데이터 관리를 간편하게 해 준다면, Nubase는 AI 코딩 도구와 에이전트를 마찰 없이 배포할 수 있도록 지원합니다.
>
> 오픈 소스 여정을 응원해 주시려면 **[GitHub에서 Nubase에 ⭐️를 남겨 주세요](https://github.com/ottermind/nubase)**!

## 기능 비교

Community, Pro, Enterprise는 동일한 데이터베이스 클라이언트 기본 기능을 공유합니다. 상용 버전은 별도의 데이터베이스 엔진을 제공하는 것이 아니라 이 기본 기능에 공식 AI, 클라우드 저장소, 다중 기기 사용, 협업 및 거버넌스를 추가합니다.

| 기능 | Community | Pro(온라인 개인용) | Enterprise |
| --- | --- | --- | --- |
| 데이터소스 연결 및 자격 증명 관리 | ✅ | ✅ | ✅ |
| 데이터베이스 플러그인 및 메타데이터 탐색 | ✅ | ✅ | ✅ |
| SQL 편집, 자동 완성, 서식 지정 및 실행 | ✅ | ✅ | ✅ |
| DDL/DML, 객체 관리 및 데이터 편집 | ✅ | ✅ | ✅ |
| 데이터 가져오기 및 내보내기 | ✅ | ✅ | ✅ |
| 저장된 SQL, 기록 및 작업 | ✅ | ✅ | ✅ |
| 대시보드 및 차트 | ✅ | ✅ | ✅ |
| CLI 및 MCP | ✅ | ✅ | ✅ |
| 사용자 지정 AI 모델 | ✅ | ✅ | ✅ |
| 공식 AI 크레딧 및 클라우드 채팅 기록 | ❌ | ✅ | ✅ |
| 계정 및 ID 체계 | ❌ | ✅ | ✅ |
| 클라우드 및 다중 기기 | ❌ | ✅ | ✅ |
| 팀 데이터소스 및 공유 자산 | ❌ | ❌ | ✅ |
| 구성원 및 역할 | ❌ | ❌ | ✅ |
| 권한, 승인 및 감사 | ❌ | ❌ | ✅ |
| 조직 데이터 컨텍스트 및 기업 지식 베이스 | ❌ | ❌ | ✅ |

## 다운로드 및 설치

Chat2DB는 Windows, macOS 및 Linux를 지원하는 크로스 플랫폼 애플리케이션입니다. 다음 링크에서 Chat2DB를 다운로드할 수 있습니다.

- [Pro 버전 다운로드](https://chat2db.ai/download)
- [Local 버전 다운로드](https://chat2db.ai/download)
- [Community (Source Available) 다운로드](https://github.com/OtterMind/Chat2DB/releases)

## Community Edition 보안 경계

> [!WARNING]
> Chat2DB Community는 단일 사용자용 로컬 우선 애플리케이션입니다. 사용자 계정,
> 테넌트 격리 또는 여러 사용자 사이의 권한 경계를 제공하지 않습니다. HTTP 서비스는
> `127.0.0.1` 또는 `::1`에 바인딩하고 다른 사용자나 신뢰할 수 없는 네트워크에 직접
> 노출하지 마세요.
>
> 사용자 지정 JDBC 드라이버는 실행 가능한 Java 코드입니다. 신뢰할 수 있는 출처의
> 드라이버만 설치하세요. 가져온 구성 파일, 압축 파일, SQL 파일, 데이터베이스 내용과
> AI 응답은 계속 신뢰할 수 없는 데이터로 취급해야 합니다. 전체 신뢰 경계와 취약점
> 신고 절차는 [보안 정책](SECURITY.md)을 참조하세요.

## Community Edition Docker 설치

### 시스템 요구 사항

Chat2DB를 설치하기 전에 시스템이 다음 요구 사항을 충족하는지 확인하세요.

- Docker 19.03.0 이상
- Docker Compose 2.0.0 이상(Compose V2)
- CPU 코어 2개 이상
- RAM 4GiB 이상

```bash
# 저장소 체크아웃에서 한 번 실행하세요. 다시 실행하면 기존의 유효한 키를 재사용합니다.
./script/security/init-community-encryption-key.sh

docker run --detach \
  --name chat2db-community \
  --restart unless-stopped \
  --publish 127.0.0.1:10825:10825 \
  --volume "$HOME/.chat2db-community-docker:/root/.chat2db-community" \
  --env CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE=/run/secrets/chat2db-community-encryption.key \
  --volume "$HOME/.config/chat2db-community/encryption.key:/run/secrets/chat2db-community-encryption.key:ro" \
  chat2db/chat2db:latest

docker logs --follow chat2db-community
```

`http://localhost:10825`를 여세요. 컨테이너를 업데이트하려면 새 이미지를 가져오고 기존 컨테이너를 제거한 다음 위 명령을 다시 실행하세요.

Chat2DB Community 5.3.0은 독립적인 `/root/.chat2db-community` 디렉터리를 사용합니다. `/root/.chat2db`를 사용하던 이전 이미지의 데이터는 자동으로 마이그레이션하지 않습니다.

컨테이너를 다시 빌드할 때도 `~/.config/chat2db-community/encryption.key`를 유지하고 별도로 백업하세요. 키를 교체하거나 분실하면 이전에 저장한 데이터 소스 비밀번호와 AI 모델 API 키를 읽을 수 없습니다. 일반 웹/헤드리스 시작을 포함하여 `chat2db.mode`가 `DESKTOP`이 아닌 상태에서 유효한 키를 제공하지 않으면 시작에 실패합니다.

저장소에는 Compose 정의도 포함되어 있습니다.

```bash
./script/security/init-community-encryption-key.sh
docker compose --file docker/docker-compose.yml up --detach
```

위의 `docker run` 예시는 `$HOME/.chat2db-community-docker`를 데이터 디렉터리로 사용하고, Docker Compose는 `chat2db-community-data`라는 이름이 지정된 볼륨을 사용합니다. 두 방식의 데이터는 자동으로 공유되지 않습니다.

## Community 암호화 키

Chat2DB Community는 저장된 데이터 소스 비밀번호와 사용자가 구성한 AI 모델 API 키를 AES-256-GCM으로 암호화합니다. 두 데이터는 서로 다른 인증 AAD 값과 동일한 설치 키를 사용하므로 한 용도의 암호문을 다른 용도로 복호화할 수 없습니다. 키는 디코딩 시 정확히 32바이트가 되는 유효한 Base64여야 합니다. 제공되는 초기화 도구는 표준 패딩 형식, 즉 `=`으로 끝나는 44자의 Base64를 생성합니다. 이는 사람이 읽을 수 있는 비밀번호가 아니라 암호화 키 자료입니다.

저장소 체크아웃에서 기본 키 파일을 한 번 생성하세요.

```bash
./script/security/init-community-encryption-key.sh
```

기본 경로는 `~/.config/chat2db-community/encryption.key`입니다. 사용자 지정 경로를 사용하려면 스크립트에 경로를 전달하고 Chat2DB를 시작할 때 동일한 경로를 구성하세요.

```bash
./script/security/init-community-encryption-key.sh /secure/path/chat2db-community.key

java -Dloader.path=chat2db-community-server/chat2db-community-start/target/lib \
    -Dchat2db.runtime.mode=community \
    -Dchat2db.mode=WEB \
    -Dchat2db.gui=false \
    -Dchat2db.network.status=OFFLINE \
    -Dchat2db.community.encryption-key-file=/secure/path/chat2db-community.key \
    -Dserver.address=127.0.0.1 \
    -Dserver.port=10825 \
    -jar chat2db-community-server/chat2db-community-start/target/chat2db-community.jar
```

스크립트에는 `openssl`이 필요합니다. 키 파일 경로의 우선순위는 위치 인수, `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`, 기본 경로 순입니다. 스크립트는 유효한 일반 파일을 재사용하고 심볼릭 링크와 일반 파일이 아닌 파일을 거부하며, 유효하지 않은 파일을 덮어쓰지 않습니다. 키는 Chat2DB 프로세스 소유자만 읽을 수 있도록 유지하고, 업그레이드와 컨테이너 재빌드 후에도 보존하며, 데이터 소스 저장소와 별도로 백업하세요.

키 구성은 다음 순서로 결정됩니다.

1. Base64 키가 포함된 JVM 속성 `chat2db.community.encryption-key`.
2. Base64 키가 포함된 환경 변수 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY`.
3. 키 파일 경로가 포함된 JVM 속성 `chat2db.community.encryption-key-file`.
4. 키 파일 경로가 포함된 환경 변수 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`.
5. 기본 파일 `~/.config/chat2db-community/encryption.key`.

처음 구성된 값이 최종 기준입니다. 빈 값, 잘못된 Base64, 디코딩 시 32바이트가 아닌 키 또는 유효하지 않은 키 파일이 있으면 다음 소스로 넘어가지 않고 시작에 실패합니다. 키 값을 프로세스 인수나 환경 변수에 직접 넣지 않도록 파일 기반 구성을 권장합니다.

키 파일 자동 생성 여부는 `chat2db.gui`가 아니라 `chat2db.mode`에 따라 결정됩니다. Community Desktop 모드(`chat2db.runtime.mode=community` 및 `chat2db.mode=DESKTOP`)에서는 인라인 키가 구성되지 않았고 선택한 키 파일이 없을 때 해당 파일을 생성합니다. 일반 웹/헤드리스 시작을 포함한 모든 비 Desktop 모드에서는 누락된 키를 생성하지 않으며, 유효한 키가 제공되거나 초기화될 때까지 시작에 실패합니다. 확인된 키는 프로세스 수명 동안 캐시되므로 키 구성을 변경한 후에는 애플리케이션을 다시 시작해야 합니다. 키를 교체하거나 분실하면 이전에 저장한 데이터 소스 비밀번호와 AI 모델 API 키를 읽을 수 없습니다.

## 개발 및 디버깅

### 런타임 환경

- Java 런타임: <a href="https://adoptium.net/temurin/releases/?version=17" target="_blank">Eclipse Temurin 17</a>
- Node.js 런타임: Node.js 18.17.0 이상
- Maven 3.8 이상

### 저장소를 로컬에 복제

```bash
git clone git@github.com:OtterMind/Chat2DB.git
```

### 프런트엔드 디버깅

저장소에 포함된 잠금 파일과 함께 Yarn을 사용하세요.

```bash
cd Chat2DB/chat2db-community-client
yarn install --frozen-lockfile
yarn run start:community:hot
```

### 백엔드 디버깅

```bash
cd Chat2DB
mvn -B clean package -Dmaven.test.skip=true -Dchat2db.finalName=chat2db-community \
    -f chat2db-community-server/pom.xml \
    -pl chat2db-community-start -am
./script/security/init-community-encryption-key.sh
java -Dloader.path=chat2db-community-server/chat2db-community-start/target/lib \
    -Dchat2db.gui=false \
    -Dchat2db.runtime.mode=community \
    -Dchat2db.mode=WEB \
    -Dchat2db.network.status=OFFLINE \
    -Dchat2db.community.encryption-key-file="$HOME/.config/chat2db-community/encryption.key" \
    -Dserver.address=127.0.0.1 \
    -Dserver.port=10825 \
    -Dspring.profiles.active=dev \
    -jar chat2db-community-server/chat2db-community-start/target/chat2db-community.jar
```

### 로컬 Docker 이미지 빌드

```bash
./docker/docker-build.sh 5.3.0 chat2db/chat2db:5.3.0
```

## 기여하기

커뮤니티의 버그 보고, 기능 요청, 문서 개선, 테스트 피드백 및 Pull Request를 환영합니다.

Issue를 열거나 Pull Request를 제출하기 전에 [기여 가이드](./CONTRIBUTING.md)를 읽어 주세요. 버그 보고, 개선 제안 및 유지 관리자가 기여 내용을 더 쉽게 검토할 수 있도록 하는 방법을 안내합니다.

- 버그 및 기능 요청은 [GitHub Issues](https://github.com/OtterMind/Chat2DB/issues)를 이용해 주세요.
- 질문, 설정 도움말 및 자유로운 논의는 [GitHub Discussions](https://github.com/OtterMind/Chat2DB/discussions)를 이용해 주세요.
- Pull Request가 Issue와 관련되어 있다면 PR 설명에 해당 Issue 링크를 포함해 주세요.

## 문의하기

- 이메일: Chat2DB@ch2db.com
- Discord: [Discord 서버 참여](https://discord.gg/uNjb3n5JVN)
- X: [@Chat2DB_AI](https://x.com/Chat2DB_AI)
- YouTube: [Chat2DB 채널](https://www.youtube.com/@chat2db.tutorial)
- GitHub: [Chat2DB GitHub](https://github.com/OtterMind/Chat2DB)


## 감사의 말


Chat2DB에 기여해 주신 모든 분께 감사드립니다.


<a href="https://github.com/OtterMind/Chat2DB/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=OtterMind/Chat2DB" alt="Chat2DB 기여자 목록" />
</a>

## 라이선스

Chat2DB Community 버전 5.3.0 이상에는
[이 저장소의 라이선스 조건](./LICENSE)이 적용됩니다. 이는 Apache License 2.0을
기반으로 추가 조건이 포함된 소스 공개 라이선스입니다. 버전 0.3.7과 그 이전의
과거 태그를 포함하여 5.3.0보다 먼저 게시된 Chat2DB 릴리스에는 Apache License 2.0이 계속 적용됩니다.
