# Patch Notes

## Patch 1: Notification small icon

Why:
- Some Android 16 + KernelSU/WildKSU + SUSFS/overlayfs setups make SystemUI fail to load
  `com.chiller3.bcr` resources for foreground service small icons.
- `setSmallIcon(int)` is not sufficient here because the resulting `Icon` can still be resolved
  against the app package instead of the framework package.

What to keep:
- `Notifications.kt` must use
  `Icon.createWithResource("android", android.R.drawable.sym_def_app_icon)` for notification
  small icons.
- Do not use app drawable resources or int-based `setSmallIcon(...)` for notification small
  icons.

Reapply on future upstream versions:
- Check every notification builder path in `Notifications.kt`.
- Verify the small icon helper still creates a framework `Icon` with package `"android"`.

## Patch 2: Ancillary log file creation

Why:
- Failure to create `.log`, `ERROR_*.log`, or `crash.log` should not kill `RecorderThread` or the
  whole app.
- These files are diagnostic only. Recording should continue even if ancillary logging cannot be
  created.

What to keep:
- `RecorderThread.startLogcat()` must treat ancillary log creation and process startup failures as
  warnings and continue recording without log capture.
- `OutputDirUtils.createFileInDefaultDirBestEffort()` must return `null` if both the requested
  log path and its `ERROR_...` fallback path fail.
- `RecorderApplication`'s uncaught exception handler must never throw a second exception when
  `crash.log` creation or dumping fails.

Reapply on future upstream versions:
- Re-check `RecorderThread.kt`, `RecorderApplication.kt`, and `OutputDirUtils.kt`.
- Ensure `.log`, `ERROR_*.log`, and `crash.log` paths stay best-effort only.
- Keep main recording file creation, encoding, and final move behavior unchanged unless upstream
  refactors require touching the surrounding code.

## Patch 3: Fork module updates

Why:
- The Magisk/KernelSU module updater should fetch patched releases from `autokick/BCR`, not the
  original upstream BCR release ZIPs.
- Without this, pressing the manager's update button can replace the patched module with upstream's
  unpatched module.

What to keep:
- Release builds must write `updateJson=https://github.com/autokick/BCR/releases/latest/download/update.json`
  into `module.prop`.
- `.github/workflows/patched-release.yml` must upload an `update.json` release asset whose `zipUrl`
  points at the patched ZIP in the same fork release.

Reapply on future upstream versions:
- Re-check `app/build.gradle.kts`'s `moduleProp*` task if upstream refactors module metadata
  generation.
- Verify the generated release ZIP's `module.prop` points to `autokick/BCR`, not
  `chenxiaolong/BCR`.

## Patch 4: Fork provenance and release verification

Why:
- Distributed fork artifacts must clearly identify that they are modified versions of upstream BCR.
- The fork uses its own Android signing key, so upstream APK fingerprints and upstream SSH release
  signatures must not be presented as verification for fork artifacts.
- Binary releases should have an unambiguous corresponding-source artifact.

What to keep:
- `README.md` and `FORK_NOTICE.md` must identify `autokick/BCR` as a modified fork of
  `chenxiaolong/BCR`.
- Release module ZIPs must include `README.md`, `FORK_NOTICE.md`, `PATCH_NOTES.md`, and
  `LICENSE`.
- `module.prop` must identify both the upstream author and the fork-patch maintainer.
- Each release must publish `SIGNING.txt`, `SHA256SUMS`, and a corresponding-source tarball.
- `SIGNING.txt` must report the certificate fingerprint measured from the APK built in that
  workflow run instead of hard-coding a fingerprint.
- Do not claim that fork ZIPs are signed by the upstream project's SSH release key.

Reapply on future upstream versions:
- Verify that `app/build.gradle.kts` still generates `module.prop` and packages root-level
  documentation using the same task structure.
- If upstream changes its release or signing scheme, keep fork verification text explicitly
  separate from upstream verification instructions.

## GitHub Actions automation

Workflow:
- `.github/workflows/patched-release.yml` watches for new upstream tags on a schedule and also
  supports manual runs via `workflow_dispatch`.
- It checks out upstream `chenxiaolong/BCR` at the latest tag, applies `patches/*.patch`, adds fork
  provenance files, builds `app-release.apk` and the release ZIP, and publishes them as a fork release.
- It also publishes the exact corresponding-source archive, measured APK certificate fingerprint,
  and SHA-256 checksums for every release.

Required repository secrets:
- `RELEASE_KEYSTORE_BASE64`
- `RELEASE_KEYSTORE_PASSPHRASE`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSPHRASE`
