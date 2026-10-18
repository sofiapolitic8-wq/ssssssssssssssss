#!/usr/bin/env bash
set -euo pipefail
cat >&2 <<'MSG'
Release publishing is intentionally disabled in this source rebuild.
Before enabling it, the owner must configure and review a DAVID.IO-owned repository,
a newly generated signing key/public key pair, the update manifest URL, and release permissions.
Do not reuse any inherited repository, token, private key, update endpoint, or signing identity.
Build and test the APK first; then implement publishing against the verified owner-controlled repository.
MSG
exit 2
