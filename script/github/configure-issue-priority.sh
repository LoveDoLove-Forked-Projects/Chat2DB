#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
MANIFEST="${ROOT_DIR}/.github/issue-taxonomy.json"
OWNER="${GH_PROJECT_OWNER:-OtterMind}"
PROJECT=""
APPLY=false

usage() {
  cat <<'EOF'
Usage: configure-issue-priority.sh [--owner OWNER] [--project NUMBER] [--apply]

Without --apply, the script prints the required Priority field configuration.
With --apply, it creates a missing Priority field or validates an existing one.
It never deletes or replaces a Project field.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --owner)
      if [[ $# -lt 2 || -z "${2:-}" ]]; then
        echo "--owner requires a value" >&2
        exit 2
      fi
      OWNER="$2"
      shift 2
      ;;
    --project)
      if [[ $# -lt 2 || ! "${2:-}" =~ ^[0-9]+$ ]]; then
        echo "--project requires a numeric project number" >&2
        exit 2
      fi
      PROJECT="$2"
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

for command in gh jq; do
  if ! command -v "${command}" >/dev/null 2>&1; then
    echo "Required command not found: ${command}" >&2
    exit 1
  fi
done

options="$(jq -r '[.priorities[].name] | join(",")' "${MANIFEST}")"

if ! ${APPLY}; then
  printf 'owner=%s project=%s field=Priority type=SINGLE_SELECT options=%s\n' "${OWNER}" "${PROJECT:-<required-for-apply>}" "${options}"
  exit 0
fi

if [[ -z "${PROJECT}" ]]; then
  echo "--project is required with --apply" >&2
  exit 2
fi

fields="$(gh project field-list "${PROJECT}" --owner "${OWNER}" --format json)"
existing="$(jq -c '(.fields // .) | map(select(.name == "Priority")) | first // empty' <<<"${fields}")"

if [[ -z "${existing}" ]]; then
  gh project field-create "${PROJECT}" --owner "${OWNER}" --name Priority --data-type SINGLE_SELECT --single-select-options "${options}" --format json
  exit 0
fi

actual_options="$(jq -r '[(.options // [])[].name] | join(",")' <<<"${existing}")"
if [[ "${actual_options}" != "${options}" ]]; then
  echo "Existing Priority options do not match the taxonomy." >&2
  echo "expected: ${options}" >&2
  echo "actual:   ${actual_options}" >&2
  echo "Reconcile the existing field manually; this script will not replace it." >&2
  exit 1
fi

echo "Priority field already matches the taxonomy."
