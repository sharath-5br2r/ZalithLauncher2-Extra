# ARM32 Java 21 runtime build

The ARM32 Java 21 bundle is built from the Android OpenJDK build scripts at commit [`f3936fba6fcf14f45202c5dd8d2d8179dde051b2`](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/commit/f3936fba6fcf14f45202c5dd8d2d8179dde051b2). That source applies the ARM32 `InlineIntrinsics=false` change from [`8cb72d3e93a7a93d6711e9f818804b9fc9cf97c3`](https://github.com/PojavLauncherTeam/android-openjdk-build-multiarch/commit/8cb72d3e93a7a93d6711e9f818804b9fc9cf97c3), whose patch comment documents that enabling the ARM32 setting breaks `java.lang.Math`.

`.github/workflows/build-patched-jre21-arm32.yml` builds only the ARM32 Java 21 JRE, repacks its ABI-specific files to the launcher's `bin-arm.tar.xz` layout, checks the Java version, `OS_ARCH`, ELF32/ARM ABI, and source patch input, then updates the runtime version marker. The workflow uploads the archive for inspection and commits the verified bundle to the session branch when run there. It does not modify the other Java runtime ABIs or publish a release.

This source/ABI validation is not a Minecraft compatibility test. A real ARM32 Android device is still required before claiming Minecraft 1.20.5+ works with this runtime.
