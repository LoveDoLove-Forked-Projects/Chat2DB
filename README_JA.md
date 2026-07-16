> 📣 **Chat2DB チームからのお知らせ！**
> AI ネイティブアプリケーション向けの新しいデプロイレイヤー **[Nubase](https://github.com/ottermind/nubase)** をオープンソースで公開しました！
> Chat2DB がデータ管理を簡単にするように、Nubase は AI コーディングツールやエージェントのデプロイをよりスムーズにします。
>
> オープンソースの取り組みを応援するため、GitHub で **[Nubase に ⭐️](https://github.com/ottermind/nubase)** をお願いします！
<div align="center">
  <h1>Chat2DB</h1>
  <p><strong>開発者、DBA、アナリスト、データチーム向けの AI 搭載データベースクライアント兼 SQL ワークスペースです。</strong></p>
</div>
<div align="center">
    <a href="https://trendshift.io/repositories/11808" target="_blank"><img src="https://trendshift.io/api/badge/repositories/11808" alt="OtterMind%2FChat2DB | Trendshift" style="width: 250px; height: 55px;" width="250" height="55"/></a>
</div>

<div align="center">

[![Modelscope][Modelscope-image]][Modelscope-url]
[![Discord][discord-image]][discord-url]
[![X][x-image]][x-url]

[Modelscope-image]: https://img.shields.io/badge/modelscope-blue?style=flat-square\&logo=modelscope
[Modelscope-url]: https://modelscope.cn/
[discord-image]: https://img.shields.io/badge/-Join%20us%20on%20Discord-%237289DA.svg?style=flat&logo=discord&logoColor=white
[discord-url]: https://discord.com/invite/uNjb3n5JVN
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

**1. インテリジェントSQL生成**:
Chat2DB Proは、AI駆動によるインテリジェントなSQL開発をサポートし、SQLクエリをより速く作成する手助けをします。

**2. データベース管理**:
MySQL、PostgreSQL、H2、Oracle、SQLServer、SQLite、MariaDB、ClickHouse、DM、Presto、DB2、OceanBase、Hive、KingBase、MongoDB、Redis、Snowflakeなど、10種類以上のデータベースをサポートしています。

**3. インテリジェントレポート生成**:
Chat2DB Proは、AI駆動によるインテリジェントなデータ報告をサポートし、ダッシュボードの作成を迅速に行う手助けをします。

**4. データ構造の同期**:
Chat2DB Proは、データベーステーブル構造の同期をサポートし、データベーステーブルの構造を迅速に同期する手助けをします。

## 機能比較

Community、Pro、Enterprise は、同じデータベースクライアントの基本機能を共有します。商用版は別のデータベースエンジンを提供するのではなく、その基本機能に公式 AI、クラウドストレージ、マルチデバイス利用、コラボレーション、ガバナンスを追加します。

| 機能 | Community | Pro（オンライン個人版） | Enterprise |
| --- | --- | --- | --- |
| データソース接続と認証情報管理 | ✅ | ✅ | ✅ |
| データベースプラグインとメタデータ参照 | ✅ | ✅ | ✅ |
| SQL の編集、補完、フォーマット、実行 | ✅ | ✅ | ✅ |
| DDL/DML、オブジェクト管理、データ編集 | ✅ | ✅ | ✅ |
| データのインポートとエクスポート | ✅ | ✅ | ✅ |
| 保存済み SQL、履歴、タスク | ✅ | ✅ | ✅ |
| ダッシュボードとチャート | ✅ | ✅ | ✅ |
| CLI と MCP | ✅ | ✅ | ✅ |
| カスタム AI モデル | ✅ | ✅ | ✅ |
| 公式 AI クレジットとクラウド会話履歴 | ❌ | ✅ | ✅ |
| アカウントと ID 管理 | ❌ | ✅ | ✅ |
| クラウドとマルチデバイス | ❌ | ✅ | ✅ |
| チームデータソースと共有資産 | ❌ | ❌ | ✅ |
| メンバーとロール | ❌ | ❌ | ✅ |
| 権限、承認、監査 | ❌ | ❌ | ✅ |
| 組織データコンテキストと企業ナレッジベース | ❌ | ❌ | ✅ |

## ダウンロードとインストール
Chat2DBは、Windows、MacOS、Linuxをサポートするクロスプラットフォームアプリケーションです。以下のリンクからChat2DBをダウンロードできます：
- [Proバージョンのダウンロード](https://chat2db.ai/download)
- [ローカルバージョンのダウンロード](https://chat2db.ai/download)
- [オープンソースバージョンのダウンロード](https://github.com/OtterMind/Chat2DB/releases)

## Community Edition のセキュリティ境界

> [!WARNING]
> Chat2DB Community は、単一ユーザー向けのローカルファーストなアプリケーションです。
> ユーザーアカウント、テナント分離、複数ユーザー間の認可境界は提供しません。
> HTTP サービスは `127.0.0.1` または `::1` にバインドし、他のユーザーや
> 信頼できないネットワークへ直接公開しないでください。
>
> カスタム JDBC ドライバーは実行可能な Java コードです。信頼できる提供元の
> ドライバーだけをインストールしてください。インポートした設定ファイル、
> アーカイブ、SQL ファイル、データベースの内容、AI の応答は引き続き信頼できない
> データとして扱います。完全な信頼境界と脆弱性の報告手順については、
> [セキュリティポリシー](SECURITY.md)を参照してください。

## コミュニティエディションのDockerインストール

### システム要件

Chat2DBをインストールする前に、システムが以下の要件を満たしていることを確認してください：
- Docker 19.03.0以上
- Docker Compose 1.25.0以上
- CPU >= 2コア
- RAM >= 4 GiB

```bash
# リポジトリ内で初回に一度実行します。再実行時は同じ有効なキーを再利用します。
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

`http://localhost:10825` を開いてください。更新する場合は、新しいイメージを取得し、古いコンテナを削除してから上記のコマンドを再実行します。

Chat2DB Community 5.3.0 は独立した `/root/.chat2db-community` ディレクトリを使用し、旧イメージの `/root/.chat2db` にあるデータを自動移行しません。

コンテナを再作成しても `~/.config/chat2db-community/encryption.key` を保持し、別途バックアップしてください。このファイルを置き換えたり失ったりすると、保存済みのデータソースパスワードと AI モデル API キーを復号できません。`chat2db.mode` が `DESKTOP` 以外の起動方式（通常の Web/headless 起動を含む）は、有効なキーがない場合に起動に失敗します。

リポジトリには Compose 定義も含まれています：

```bash
docker compose --file docker/docker-compose.yml up --detach
```

## コミュニティエディションの暗号化キー

Chat2DB Community は、保存されたデータソースパスワードとユーザー設定の AI モデル API キーを AES-256-GCM で暗号化します。両者は同じインストールキーを使用しますが、認証用 AAD は別々であるため、一方の暗号文をもう一方として復号できません。キーは有効な Base64 で、デコード後に正確に32バイトでなければなりません。付属の初期化スクリプトは、末尾が `=` の44文字からなる標準のパディング付き形式を生成します。人が決める通常のパスワードではなく、暗号化用のキーマテリアルです。

リポジトリ内で次のコマンドを実行し、デフォルトのキーファイルを作成します：

```bash
./script/security/init-community-encryption-key.sh
```

デフォルトパスは `~/.config/chat2db-community/encryption.key` です。カスタムパスを使用する場合は、初期化スクリプトと Chat2DB の起動引数に同じパスを指定します：

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

初期化スクリプトには `openssl` が必要です。スクリプトが使用するキーファイルパスの優先順位は、位置引数、`CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`、デフォルトパスの順です。既存の有効な通常ファイルは再利用し、シンボリックリンクと通常ファイル以外のパスを拒否し、無効なファイルを上書きしません。キーファイルは Chat2DB プロセスの所有者だけが読み取れるようにし、アップグレードやコンテナ再作成後も保持して、データソースストレージとは別にバックアップしてください。

キー設定は次の優先順位で解決されます：

1. Base64 キーを指定する JVM プロパティ `chat2db.community.encryption-key`。
2. Base64 キーを指定する環境変数 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY`。
3. キーファイルのパスを指定する JVM プロパティ `chat2db.community.encryption-key-file`。
4. キーファイルのパスを指定する環境変数 `CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE`。
5. デフォルトファイル `~/.config/chat2db-community/encryption.key`。

最初に設定された値が優先されます。空の値、不正な Base64、デコード後に32バイトにならないキー、または不正なキーファイルは、次の設定へフォールバックせず起動エラーになります。キー値をプロセス引数や環境変数へ直接置かないため、キーファイル方式を推奨します。

キーファイルの自動生成は `chat2db.mode` だけで決まり、`chat2db.gui` には依存しません。Community Desktop モード（`chat2db.runtime.mode=community` と `chat2db.mode=DESKTOP`）では、インラインキーが設定されておらず、選択されたキーファイルが存在しない場合にファイルを自動生成します。通常の Web/headless 起動を含む Desktop 以外のモードは不足しているキーを自動生成しないため、起動前に有効なキーを初期化または明示的に設定する必要があります。解決されたキーはプロセスの存続期間中キャッシュされるため、キー設定を変更した後はアプリケーションを再起動してください。キーを置き換えたり失ったりすると、保存済みのデータソースパスワードと AI モデル API キーを復号できません。

## コードデバッグ

## 実行環境

- Java runtime: <a href="https://adoptopenjdk.net/" target="_blank">Open JDK 17</a>
- Node.js runtime: Node.js 18 以降
- Maven 3.8 以降

**リポジトリをローカルにクローン**

```bash
$ git clone git@github.com:OtterMind/Chat2DB.git
```

**フロントエンドデバッグ**

```bash
リポジトリに含まれる Yarn lockfile を使用してください。
$ cd Chat2DB/chat2db-community-client
$ yarn install --frozen-lockfile
$ yarn run start:community:hot
```

**バックエンドデバッグ**

```bash
$ cd Chat2DB
$ mvn -B clean package -Dmaven.test.skip=true -Dchat2db.finalName=chat2db-community \
    -f chat2db-community-server/pom.xml \
    -pl chat2db-community-start -am
$ ./script/security/init-community-encryption-key.sh
$ java -Dloader.path=chat2db-community-server/chat2db-community-start/target/lib \
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

**ローカル Docker イメージのビルド**

```bash
$ ./docker/docker-build.sh 5.3.0 chat2db/chat2db:5.3.0
```

## コントリビューション

コミュニティからのバグ報告、機能リクエスト、ドキュメント改善、テストフィードバック、Pull Request を歓迎します。

Issue の作成や Pull Request の送信前に、[コントリビューションガイド](./CONTRIBUTING.md)をお読みください。バグの報告、改善提案、メンテナーがレビューしやすい形で貢献する方法を説明しています。

- バグや機能リクエストには [GitHub Issues](https://github.com/OtterMind/Chat2DB/issues) を使用してください。
- 質問、セットアップ支援、自由な議論には [GitHub Discussions](https://github.com/OtterMind/Chat2DB/discussions) を使用してください。
- Pull Request が Issue に関連する場合は、PR の説明に Issue へのリンクを含めてください。


## お問い合わせ

- メール: Chat2DB@ch2db.com
- Discord: [Discordサーバーに参加](https://discord.gg/JDkwB6JS8A)
- X: [@Chat2DB_AI](https://x.com/Chat2DB_AI)
- YouTube: [Chat2DB チャンネル](https://www.youtube.com/@chat2db.tutorial)
- GitHub: [Chat2DB GitHub](https://github.com/OtterMind/Chat2DB)

## 謝辞

Chat2DBに貢献してくださったすべての方々に感謝します~~



<a href="https://github.com/OtterMind/Chat2DB/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=OtterMind/Chat2DB" />
</a>

## Star History

<a href="https://star-history.com/#OtterMind/Chat2DB&Date">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=OtterMind/Chat2DB&type=Date&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=OtterMind/Chat2DB&type=Date" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=OtterMind/Chat2DB&type=Date" />
  </picture>
</a>

## License
Chat2DB Community 5.3.0 以降には、このリポジトリの
[LICENSE](./LICENSE) が適用されます。このライセンスは Apache License 2.0
を基礎として追加条件を設けた Source Available ライセンスです。Chat2DB
5.3.0 より前に公開されたすべてのリリース（0.3.7 およびそれ以前の履歴
バージョンを含む）には、引き続き Apache License 2.0 が適用されます。
