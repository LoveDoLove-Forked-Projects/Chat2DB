<div align="center">
  <h1>Chat2DB</h1>
  <p><strong>面向开发者、DBA、分析师和数据团队的 AI 驱动数据库客户端与 SQL 工作空间。</strong></p>
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

> 📣 **Chat2DB 团队的新消息！**
> 我们刚刚开源了面向 AI 原生应用的全新部署层 **[Nubase](https://github.com/ottermind/nubase)**！
> 如果 Chat2DB 能帮助你更轻松地管理数据，Nubase 将帮助你更顺畅地部署 AI 编程工具和 Agent。
>
> 欢迎在 GitHub 上为 **[Nubase 点亮 ⭐️](https://github.com/ottermind/nubase)**，支持我们的开源工作！

## 功能比较

Community、Pro 和 Enterprise 共享同一套数据库客户端基础能力。商业版本是在这一基础上增加官方 AI、云端存储、多设备访问、协作和治理能力，而不是提供另一套数据库内核。

| 功能点 | Community 社区版 | Pro 在线个人版 | Enterprise 企业版 |
| --- | --- | --- | --- |
| 数据源连接与凭证管理 | ✅ | ✅ | ✅ |
| 数据库插件与元数据浏览 | ✅ | ✅ | ✅ |
| SQL 编辑、补全、格式化与执行 | ✅ | ✅ | ✅ |
| DDL/DML、对象管理与数据编辑 | ✅ | ✅ | ✅ |
| 数据导入导出 | ✅ | ✅ | ✅ |
| SQL 收藏、历史记录与任务 | ✅ | ✅ | ✅ |
| Dashboard 与 Chart | ✅ | ✅ | ✅ |
| CLI 与 MCP | ✅ | ✅ | ✅ |
| 自定义 AI 模型 | ✅ | ✅ | ✅ |
| 官方 AI 额度与云端聊天历史 | ❌ | ✅ | ✅ |
| 账号与身份体系 | ❌ | ✅ | ✅ |
| 云端存储与多设备 | ❌ | ✅ | ✅ |
| 团队数据源与共享资产 | ❌ | ❌ | ✅ |
| 成员与角色 | ❌ | ❌ | ✅ |
| 权限、审批与审计 | ❌ | ❌ | ✅ |
| 组织数据上下文与企业知识库 | ❌ | ❌ | ✅ |

## 下载安装

Chat2DB 是一个跨平台应用，支持 Windows、macOS 和 Linux。您可以从以下链接下载 Chat2DB。

- [下载 Pro 版](https://chat2db.ai/download)
- [下载 Local 版](https://chat2db.ai/download)
- [下载 Community 版（Source Available）](https://github.com/OtterMind/Chat2DB/releases)

## 社区版安全边界

> [!WARNING]
> Chat2DB Community 是单用户、本机优先的应用，不提供用户账号、租户隔离或
> 多用户之间的权限边界。HTTP 服务必须绑定到 `127.0.0.1` 或 `::1`，不要直接
> 暴露给其他用户或不可信网络。
>
> 自定义 JDBC Driver 是可执行 Java 代码，只应安装来自可信来源的驱动。导入的
> 配置文件、压缩包、SQL 文件、数据库内容和 AI 响应仍属于不可信数据。完整信任
> 边界和漏洞报告流程请参阅 [安全策略](SECURITY.md)。

## 社区版 Docker 安装

### 系统要求

在安装 Chat2DB 之前，请确保您的系统满足以下要求：

- Docker 19.03.0 或更高版本
- Docker Compose 2.0.0 或更高版本（Compose V2）
- CPU >= 2 核
- RAM >= 4 GiB

```bash
# 在仓库目录中首次执行一次；重复执行会复用同一把合法密钥。
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

浏览器访问 `http://localhost:10825`。更新时先拉取新镜像、删除旧容器，再重新执行以上命令。

Chat2DB Community 5.3.0 使用独立的 `/root/.chat2db-community` 目录，不会自动迁移旧镜像 `/root/.chat2db` 中的数据。

容器重建时必须保留 `~/.config/chat2db-community/encryption.key`，并单独备份。替换或丢失该文件会导致已有数据源密码和 AI 模型 API Key 无法解密；`chat2db.mode` 不是 `DESKTOP` 的启动方式（包括常规 Web/headless 启动）缺少合法密钥时会直接启动失败。

仓库也提供了 Compose 配置：

```bash
./script/security/init-community-encryption-key.sh
docker compose --file docker/docker-compose.yml up --detach
```

`docker run` 示例将应用数据保存在 `$HOME/.chat2db-community-docker`，Compose 配置使用名为 `chat2db-community-data` 的命名卷。两处存储不会自动共享数据。

## 社区版加密密钥

Chat2DB Community 使用 AES-256-GCM 加密保存的数据源密码和用户配置的 AI 模型 API Key。两类数据使用同一把安装密钥，但使用不同的认证 AAD，因此一种用途的密文不能作为另一种用途解密。密钥必须是合法的 Base64，且解码后恰好为 32 字节。仓库提供的初始化脚本会生成标准带填充格式，即 44 个 Base64 字符并以 `=` 结尾。它是加密密钥材料，不是用户自行输入的普通口令。

在仓库目录中执行以下命令，创建默认密钥文件：

```bash
./script/security/init-community-encryption-key.sh
```

默认路径是 `~/.config/chat2db-community/encryption.key`。如需使用自定义路径，应在初始化脚本和 Chat2DB 启动参数中指定同一路径：

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

初始化脚本依赖 `openssl`。脚本选择密钥文件路径的优先级依次为位置参数、`CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE` 和默认路径。脚本会复用已有的合法普通文件，拒绝符号链接和非普通文件；如果已有文件不合法，脚本会报错且不会覆盖。密钥文件应当只允许 Chat2DB 进程所属用户读取，升级或重建容器时必须保留，并与数据源存储分开备份。

密钥配置按以下优先级解析：

1. JVM 参数 `chat2db.community.encryption-key`，值为 Base64 密钥。
2. 环境变量 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY`，值为 Base64 密钥。
3. JVM 参数 `chat2db.community.encryption-key-file`，值为密钥文件路径。
4. 环境变量 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`，值为密钥文件路径。
5. 默认文件 `~/.config/chat2db-community/encryption.key`。

第一个已配置的值具有最高优先级。空值、非法 Base64、解码后不是 32 字节的密钥或非法密钥文件都会直接导致启动失败，不会静默回退到下一项。推荐使用密钥文件，避免把密钥值直接暴露在进程参数或环境变量中。

密钥文件是否自动创建只取决于 `chat2db.mode`，与 `chat2db.gui` 无关。Community Desktop 模式（`chat2db.runtime.mode=community` 且 `chat2db.mode=DESKTOP`）会在未配置内联密钥且所选密钥文件不存在时自动创建该文件。任何非 Desktop 模式（包括常规 Web/headless 启动）都不会创建缺失的密钥，必须提前初始化或显式配置合法密钥，否则启动失败。解析后的密钥会在进程生命周期内缓存，因此修改密钥配置后必须重启应用。替换或丢失密钥后，已经保存的数据源密码和 AI 模型 API Key 将无法解密。

## 开发调试

### 运行环境

- Java 运行环境：<a href="https://adoptium.net/temurin/releases/?version=17" target="_blank">Eclipse Temurin 17</a>
- Node 运行环境：Node.js 18.17.0 或更高版本
- Maven 3.8 或以上版本

### 克隆仓库到本地

```bash
git clone git@github.com:OtterMind/Chat2DB.git
```

### 前端调试

请使用仓库中的 Yarn lockfile。

```bash
cd Chat2DB/chat2db-community-client
yarn install --frozen-lockfile
yarn run start:community:hot
```

### 后端调试

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

### 构建本地 Docker 镜像

```bash
./docker/docker-build.sh 5.3.0 chat2db/chat2db:5.3.0
```

## 参与贡献

我们欢迎社区提交 Bug、功能建议、文档改进、测试反馈和 Pull Request。

创建 Issue 或提交 Pull Request 前，请先阅读[贡献指南](./CONTRIBUTING.md)。其中说明了如何报告问题、提出建议，以及如何让维护者更高效地审查贡献。

- Bug 和功能建议请使用 [GitHub Issues](https://github.com/OtterMind/Chat2DB/issues)。
- 使用问题、配置帮助和开放讨论请使用 [GitHub Discussions](https://github.com/OtterMind/Chat2DB/discussions)。
- 如果 Pull Request 与某个 Issue 相关，请在 PR 描述中附上对应链接。

## 联系我们

<img src="https://github.com/chat2db/Chat2DB/assets/22975773/81d13eff-c615-49f5-aee3-4107089593e0" alt="Chat2DB 联系二维码" width="25%" />

- Email: Chat2DB@ch2db.com
- Discord: [Join our Discord server](https://discord.gg/uNjb3n5JVN)
- X: [@Chat2DB_AI](https://x.com/Chat2DB_AI)
- YouTube: [Chat2DB Channel](https://www.youtube.com/@chat2db.tutorial)
- GitHub: [Chat2DB GitHub](https://github.com/OtterMind/Chat2DB)


## 致谢

感谢所有为 Chat2DB 贡献力量的同学们。

<a href="https://github.com/OtterMind/Chat2DB/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=OtterMind/Chat2DB" alt="Chat2DB 贡献者" />
</a>

## Star History

<a href="https://www.star-history.com/?repos=OtterMind%2FChat2DB&type=date&legend=top-left">
 <img alt="Chat2DB Star History 曲线" src="https://api.star-history.com/chart?repos=OtterMind/Chat2DB&type=date&legend=top-left&sealed_token=HqUTPR9nqjAv9Iikq8Jw6_J5TVBqOi0TiLtkPzm23BAJclEPLy93G-5ts_ZiNPJjnOAYTJoedoV7GqiMMkvlxjN0hTCKVn1_Bbe3RABuGYqg3jRibN8db-xChpiLkLrfnEa_C_O4bVoHnwjB1lNnlYNEHEvxFw_6WRuoSKwAj4t5KXYS2dz315a9Yo3c" />
</a>

## 许可证

Chat2DB Community 5.3.0 及后续版本适用本仓库的
[LICENSE](./LICENSE)。该许可基于 Apache License 2.0 并附加了使用条件，
属于 Source Available 许可。Chat2DB 5.3.0 之前发布的所有版本，包括 0.3.7
以及更早的历史版本，继续适用 Apache License 2.0。
