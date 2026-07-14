#!/usr/bin/env bash

set -euo pipefail

usage() {
    echo "Usage: $0 [KEY_FILE]" >&2
}

if [ "$#" -gt 1 ]; then
    usage
    exit 2
fi

if ! command -v openssl >/dev/null 2>&1; then
    echo "openssl is required to initialize the Community encryption key" >&2
    exit 1
fi

default_key_file="${HOME}/.config/chat2db-community/encryption.key"
key_file="${1:-${CHAT2DB_COMMUNITY_ENCRYPTION_KEY_FILE:-${default_key_file}}}"
key_dir="$(dirname "${key_file}")"

if [ ! -d "${key_dir}" ]; then
    mkdir -p "${key_dir}"
    chmod 700 "${key_dir}"
fi

validate_key_file() {
    local file="$1"
    local encoded_key
    local decoded_bytes

    encoded_key="$(tr -d '\r\n' < "${file}")"
    if [[ ! "${encoded_key}" =~ ^[A-Za-z0-9+/]{43}=$ ]]; then
        return 1
    fi

    decoded_bytes="$(printf '%s' "${encoded_key}" | openssl base64 -d -A | wc -c | tr -d '[:space:]')"
    [ "${decoded_bytes}" = "32" ]
}

if [ -e "${key_file}" ]; then
    if [ -L "${key_file}" ] || [ ! -f "${key_file}" ]; then
        echo "Community encryption key path must be a regular file: ${key_file}" >&2
        exit 1
    fi
    if ! validate_key_file "${key_file}"; then
        echo "Existing Community encryption key is invalid and was not overwritten: ${key_file}" >&2
        exit 1
    fi
    state="reused"
else
    umask 077
    temporary_file="$(mktemp "${key_file}.tmp.XXXXXX")"
    cleanup() {
        rm -f "${temporary_file}"
    }
    trap cleanup EXIT

    openssl rand -base64 -out "${temporary_file}" 32
    chmod 600 "${temporary_file}"
    if ! validate_key_file "${temporary_file}"; then
        echo "Generated Community encryption key failed validation" >&2
        exit 1
    fi

    if ln "${temporary_file}" "${key_file}" 2>/dev/null; then
        state="created"
    elif [ -f "${key_file}" ] && validate_key_file "${key_file}"; then
        state="reused"
    else
        echo "Unable to create Community encryption key without overwriting an existing file: ${key_file}" >&2
        exit 1
    fi
fi

chmod 600 "${key_file}"
echo "Community encryption key ${state}: ${key_file}"
echo "decoded-bytes=32"
