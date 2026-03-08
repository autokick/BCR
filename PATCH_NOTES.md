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
