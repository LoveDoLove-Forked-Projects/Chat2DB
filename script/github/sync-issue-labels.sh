#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
MANIFEST="${ROOT_DIR}/.github/issue-taxonomy.json"
REPOSITORY="${GH_REPO:-OtterMind/Chat2DB}"
APPLY=false

usage() {
  cat <<'EOF'
Usage: sync-issue-labels.sh [--repo OWNER/REPO] [--apply]

Without --apply, the script only prints the labels it would create or update.
It never deletes labels.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --repo)
      if [[ $# -lt 2 || -z "${2:-}" ]]; then
        echo "--repo requires OWNER/REPO" >&2
        exit 2
      fi
      REPOSITORY="$2"
      shift 2
      ;;
    --apply)
      APPLY=true
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [[ "${REPOSITORY}" != */* ]]; then
  echo "Repository must use OWNER/REPO format: ${REPOSITORY}" >&2
  exit 2
fi

for command in gh jq; do
  if ! command -v "${command}" >/dev/null 2>&1; then
    echo "Required command not found: ${command}" >&2
    exit 1
  fi
done

jq -e '
  .schemaVersion == 1
  and (.labels | type == "array")
  and ([.labels[].name] | length == (unique | length))
  and all(.labels[];
    (.name | type == "string" and length > 0)
    and (.color | test("^[0-9A-Fa-f]{6}$"))
    and (.description | type == "string")
  )
' "${MANIFEST}" >/dev/null

current_labels="$(gh label list --repo "${REPOSITORY}" --limit 1000 --json name,color,description)"

created=0
updated=0
unchanged=0

while IFS= read -r label; do
  name="$(jq -r '.name' <<<"${label}")"
  color="$(jq -r '.color' <<<"${label}")"
  description="$(jq -r '.description' <<<"${label}")"
  existing="$(jq -c --arg name "${name}" 'map(select(.name == $name)) | first // empty' <<<"${current_labels}")"

  action=create
  if [[ -n "${existing}" ]]; then
    existing_color="$(jq -r '.color' <<<"${existing}" | tr '[:lower:]' '[:upper:]')"
    existing_description="$(jq -r '.description // ""' <<<"${existing}")"
    desired_color="$(tr '[:lower:]' '[:upper:]' <<<"${color}")"

    if [[ "${existing_color}" == "${desired_color}" && "${existing_description}" == "${description}" ]]; then
      action=unchanged
    else
      action=update
    fi
  fi

  if [[ "${action}" == unchanged ]]; then
    unchanged=$((unchanged + 1))
    continue
  fi

  if ${APPLY}; then
    gh label create "${name}" --repo "${REPOSITORY}" --color "${color}" --description "${description}" --force
  else
    printf '%-6s %s\n' "${action}" "${name}"
  fi

  if [[ "${action}" == create ]]; then
    created=$((created + 1))
  else
    updated=$((updated + 1))
  fi
done < <(jq -c '.labels[]' "${MANIFEST}")

printf 'summary repo=%s apply=%s create=%d update=%d unchanged=%d\n' "${REPOSITORY}" "${APPLY}" "${created}" "${updated}" "${unchanged}"
