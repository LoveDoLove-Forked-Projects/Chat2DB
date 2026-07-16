<div align="center">
  <h1>Chat2DB</h1>
  <p><strong>An AI-powered database client and SQL workspace for developers, DBAs, analysts, and data teams.</strong></p>
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

> 📣 **Exciting News from the Chat2DB Team!**
> We just open-sourced **[Nubase](https://github.com/ottermind/nubase)** — our brand new deployment layer for Artificial Intelligence-native applications!
> If Chat2DB helps you manage data effortlessly, Nubase will help you deploy your AI coding tools and agents with zero friction.
>
> Show some love and **[give Nubase a ⭐️ on GitHub](https://github.com/ottermind/nubase)** to support our open-source journey!

## Feature Comparison

Community, Pro, and Enterprise share the same core database-client capabilities. Commercial editions add official AI services, cloud storage, multi-device access, collaboration, and governance around that core rather than replacing it with a different database engine.

| Feature | Community | Pro (Online Personal) | Enterprise |
| --- | --- | --- | --- |
| Datasource connections and credential management | ✅ | ✅ | ✅ |
| Database plugins and metadata browsing | ✅ | ✅ | ✅ |
| SQL editing, completion, formatting, and execution | ✅ | ✅ | ✅ |
| DDL/DML, object management, and data editing | ✅ | ✅ | ✅ |
| Data import and export | ✅ | ✅ | ✅ |
| Saved SQL, history, and tasks | ✅ | ✅ | ✅ |
| Dashboards and charts | ✅ | ✅ | ✅ |
| CLI and MCP | ✅ | ✅ | ✅ |
| Custom AI models | ✅ | ✅ | ✅ |
| Official AI credits and cloud chat history | ❌ | ✅ | ✅ |
| Account and identity system | ❌ | ✅ | ✅ |
| Cloud storage and multi-device | ❌ | ✅ | ✅ |
| Team datasources and shared assets | ❌ | ❌ | ✅ |
| Members and roles | ❌ | ❌ | ✅ |
| Permissions, approvals, and audit | ❌ | ❌ | ✅ |
| Organization data context and enterprise knowledge base | ❌ | ❌ | ✅ |

## Download and Installation

Chat2DB is a cross-platform application that supports Windows, macOS, and Linux. You can download Chat2DB from the following links:

- [Download Pro Version](https://chat2db.ai/download)
- [Download Local Version](https://chat2db.ai/download)
- [Download Community (Source Available)](https://github.com/OtterMind/Chat2DB/releases)

## Community Security Boundary

> [!WARNING]
> Chat2DB Community is a single-user, local-first application. It does not
> provide user accounts, tenant isolation, or authorization boundaries between
> multiple users. Keep the HTTP service bound to `127.0.0.1` or `::1` and do
> not expose it directly to other users or untrusted networks.
>
> Custom JDBC drivers are executable Java code. Install them only from sources
> you trust. Imported configuration files, archives, SQL files, database
> contents, and AI responses remain untrusted data. See [Security Policy](SECURITY.md)
> for the complete trust boundary and vulnerability reporting process.

## Community Edition Docker Installation

### System Requirements

Before installing Chat2DB, ensure your system meets the following requirements:

- Docker 19.03.0 or later
- Docker Compose 2.0.0 or later (Compose V2)
- CPU >= 2 Cores
- RAM >= 4 GiB

```bash
# Run once from a repository checkout. Re-running reuses the same valid key.
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

Open `http://localhost:10825`. To update the container, pull the new image, remove the old container, and run the command above again.

Chat2DB Community 5.3.0 uses the independent `/root/.chat2db-community` directory. It does not automatically migrate data from earlier images that used `/root/.chat2db`.

Keep `~/.config/chat2db-community/encryption.key` across container rebuilds and back it up separately. Replacing or losing it makes previously stored datasource passwords and AI model API keys unreadable. Startup with `chat2db.mode` other than `DESKTOP`, including normal Web/headless startup, fails when no valid key is provided.

The repository also includes a Compose definition:

```bash
./script/security/init-community-encryption-key.sh
docker compose --file docker/docker-compose.yml up --detach
```

The `docker run` example stores application data in `$HOME/.chat2db-community-docker`. The Compose definition uses the `chat2db-community-data` named volume. These storage locations do not automatically share data.

## Community Encryption Key

Chat2DB Community encrypts stored datasource passwords and user-configured AI model API keys with AES-256-GCM. Both use the same installation key with separate authenticated AAD values, so ciphertext from one purpose cannot be decrypted as the other. The key must be valid Base64 that decodes to exactly 32 bytes. The bundled initializer generates the standard padded form: 44 Base64 characters ending in `=`. It is cryptographic key material, not a human-readable password.

Create the default key file once from a repository checkout:

```bash
./script/security/init-community-encryption-key.sh
```

The default path is `~/.config/chat2db-community/encryption.key`. To use a custom path, pass it to the script and configure the same path when starting Chat2DB:

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

The script requires `openssl`. Its key-file path priority is the positional argument, `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`, then the default path. It reuses a valid regular file, rejects symbolic links and non-regular files, and refuses to overwrite an invalid file. Keep the key readable only by the Chat2DB process owner, persist it across upgrades and container rebuilds, and back it up separately from the datasource storage.

Key configuration is resolved in this order:

1. JVM property `chat2db.community.encryption-key` containing the Base64 key.
2. Environment variable `CHAT2DB_COMMUNITY_ENCRYPTION_KEY` containing the Base64 key.
3. JVM property `chat2db.community.encryption-key-file` containing a key-file path.
4. Environment variable `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE` containing a key-file path.
5. Default file `~/.config/chat2db-community/encryption.key`.

The first configured value is authoritative. A blank value, malformed Base64, a key that does not decode to 32 bytes, or an invalid key file fails startup instead of falling through to the next source. File-based configuration is recommended because it avoids placing the key value directly in process arguments or environment variables.

Automatic key-file creation depends on `chat2db.mode`, not `chat2db.gui`. Community Desktop mode (`chat2db.runtime.mode=community` with `chat2db.mode=DESKTOP`) creates the selected key file when no inline key is configured and the file is missing. Any non-Desktop mode, including normal Web/headless startup, never creates a missing key and fails until a valid key is provided or initialized. The resolved key is cached for the process lifetime, so changing key configuration requires an application restart. Replacing or losing the key makes previously stored datasource passwords and AI model API keys unreadable.

## Development

### Runtime Environment

- Java runtime: <a href="https://adoptium.net/temurin/releases/?version=17" target="_blank">Eclipse Temurin 17</a>
- Node.js runtime: Node.js 18.17.0 or later
- Maven 3.8 or later

### Clone the Repository

```bash
git clone git@github.com:OtterMind/Chat2DB.git
```

### Frontend Debugging

Use Yarn with the checked-in lockfile.

```bash
cd Chat2DB/chat2db-community-client
yarn install --frozen-lockfile
yarn run start:community:hot
```

### Backend Debugging

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

### Build a Local Docker Image

```bash
./docker/docker-build.sh 5.3.0 chat2db/chat2db:5.3.0
```

## Contributing

We welcome bug reports, feature requests, documentation improvements, testing feedback, and pull requests from the community.

Before opening an issue or submitting a pull request, please read our [Contributing Guide](./CONTRIBUTING.md). It explains how to report bugs, suggest improvements, and make contributions easier for maintainers to review.

- For bugs and feature requests, please use [GitHub Issues](https://github.com/OtterMind/Chat2DB/issues).
- For questions, setup help, and open-ended discussions, please use [GitHub Discussions](https://github.com/OtterMind/Chat2DB/discussions).
- If your pull request is related to an issue, please link it in the PR description.

## Contact Us

- Email: Chat2DB@ch2db.com
- Discord: [Join our Discord server](https://discord.gg/uNjb3n5JVN)
- X: [@Chat2DB_AI](https://x.com/Chat2DB_AI)
- YouTube: [Chat2DB Channel](https://www.youtube.com/@chat2db.tutorial)
- GitHub: [Chat2DB GitHub](https://github.com/OtterMind/Chat2DB)


## Acknowledgments


Thanks to everyone who has contributed to Chat2DB.


<a href="https://github.com/OtterMind/Chat2DB/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=OtterMind/Chat2DB" alt="Chat2DB contributors" />
</a>

## Star History

<a href="https://www.star-history.com/?repos=OtterMind%2FChat2DB&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=OtterMind/Chat2DB&type=date&theme=dark&legend=top-left&sealed_token=HqUTPR9nqjAv9Iikq8Jw6_J5TVBqOi0TiLtkPzm23BAJclEPLy93G-5ts_ZiNPJjnOAYTJoedoV7GqiMMkvlxjN0hTCKVn1_Bbe3RABuGYqg3jRibN8db-xChpiLkLrfnEa_C_O4bVoHnwjB1lNnlYNEHEvxFw_6WRuoSKwAj4t5KXYS2dz315a9Yo3c" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=OtterMind/Chat2DB&type=date&legend=top-left&sealed_token=HqUTPR9nqjAv9Iikq8Jw6_J5TVBqOi0TiLtkPzm23BAJclEPLy93G-5ts_ZiNPJjnOAYTJoedoV7GqiMMkvlxjN0hTCKVn1_Bbe3RABuGYqg3jRibN8db-xChpiLkLrfnEa_C_O4bVoHnwjB1lNnlYNEHEvxFw_6WRuoSKwAj4t5KXYS2dz315a9Yo3c" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=OtterMind/Chat2DB&type=date&legend=top-left&sealed_token=HqUTPR9nqjAv9Iikq8Jw6_J5TVBqOi0TiLtkPzm23BAJclEPLy93G-5ts_ZiNPJjnOAYTJoedoV7GqiMMkvlxjN0hTCKVn1_Bbe3RABuGYqg3jRibN8db-xChpiLkLrfnEa_C_O4bVoHnwjB1lNnlYNEHEvxFw_6WRuoSKwAj4t5KXYS2dz315a9Yo3c" />
 </picture>
</a>

## License

Chat2DB Community version 5.3.0 and later is available under the
[license terms in this repository](./LICENSE). This is a source-available
license based on the Apache License 2.0 with additional conditions. Chat2DB
releases published before version 5.3.0, including version 0.3.7 and the
earlier historical tags, remain under the Apache License 2.0.
