# Fork notice

This repository and the release artifacts published by `autokick/BCR` are modified versions of Basic Call Recorder (BCR) by Andrew Gunnerson (`chenxiaolong`).

- Upstream project: https://github.com/chenxiaolong/BCR
- Fork repository: https://github.com/autokick/BCR
- License: GPL-3.0-only
- Fork modifications began: 2026-03-08

The upstream copyright and GPL-3.0-only licensing notices are preserved. Local changes are documented in `PATCH_NOTES.md`, the patch files under `patches/`, and the Git history.

Release APKs from this fork are signed with the fork maintainer's own Android signing key. The upstream project's APK certificate fingerprint and upstream SSH release signatures do not authenticate this fork's release artifacts. Each fork release publishes a `SIGNING.txt` asset containing the SHA-256 fingerprint of the certificate used to sign that release APK.

The fork's module ZIP is not separately signed with the upstream project's SSH key. Verify the APK certificate fingerprint against the release's `SIGNING.txt` and, when needed, compare the release artifacts with the corresponding source archive and checksums published alongside them.
