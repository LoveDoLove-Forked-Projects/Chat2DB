#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
project_root="$(cd -- "${script_dir}/.." && pwd)"
client_dir="${project_root}/chat2db-community-client"
server_dir="${project_root}/chat2db-community-server"
resources_dir="${server_dir}/chat2db-community-start/src/main/resources"
target_dir="${server_dir}/chat2db-community-start/target"
front_dir="${resources_dir}/static/front"
index_file="${resources_dir}/thymeleaf/index.html"
version="${1:-4.0.0-SNAPSHOT}"
image="${2:-chat2db/chat2db:${version}}"
backup_dir="$(mktemp -d)"
front_was_present=false
index_was_present=false
staging_started=false

cleanup() {
  if [ "${staging_started}" = true ]; then
    rm -rf "${front_dir}"
    if [ "${front_was_present}" = true ]; then
      mkdir -p "$(dirname -- "${front_dir}")"
      cp -R "${backup_dir}/front" "${front_dir}"
    fi

    if [ "${index_was_present}" = true ]; then
      mkdir -p "$(dirname -- "${index_file}")"
      cp "${backup_dir}/index.html" "${index_file}"
    else
      rm -f "${index_file}"
    fi
  fi

  rm -rf "${backup_dir}"
}
trap cleanup EXIT

if [ -d "${front_dir}" ]; then
  cp -R "${front_dir}" "${backup_dir}/front"
  front_was_present=true
fi
if [ -f "${index_file}" ]; then
  cp "${index_file}" "${backup_dir}/index.html"
  index_was_present=true
fi

command -v yarn >/dev/null
command -v mvn >/dev/null
command -v docker >/dev/null

(
  cd "${client_dir}"
  yarn install --frozen-lockfile
  UMI_PublicPath=/static/front/ yarn run build:web:community --app_version="${version}"
)

staging_started=true
rm -rf "${front_dir}"
mkdir -p "${front_dir}" "$(dirname -- "${index_file}")"
cp -R "${client_dir}/dist/." "${front_dir}/"
cp "${client_dir}/dist/index.html" "${index_file}"

mvn -B clean package \
  -Dmaven.test.skip=true \
  -Dchat2db.finalName=chat2db-community \
  -f "${server_dir}/pom.xml" \
  -pl chat2db-community-start \
  -am

test -f "${target_dir}/chat2db-community.jar"
test -d "${target_dir}/lib"

docker build \
  --tag "${image}" \
  --file "${script_dir}/Dockerfile" \
  "${target_dir}"

echo "Built ${image}"
