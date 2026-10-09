#!/usr/bin/env python3
"""Verify the repackaged Java 21 ARM32 runtime and its source patch input."""

from __future__ import annotations

import re
import struct
import sys
import tarfile
from pathlib import Path


def normalized_name(name: str) -> str:
    while name.startswith("./"):
        name = name[2:]
    return name


def fail(message: str) -> None:
    raise SystemExit(f"ARM32 Java 21 verification failed: {message}")


def main() -> None:
    if len(sys.argv) != 3:
        fail("usage: verify_jre21_arm32.py <bin-arm.tar.xz> <patch directory>")

    archive_path = Path(sys.argv[1])
    patch_dir = Path(sys.argv[2])
    if not archive_path.is_file():
        fail(f"runtime archive does not exist: {archive_path}")

    patch_files = sorted(patch_dir.glob("*.diff"))
    patch_text = "\n".join(path.read_text(errors="replace") for path in patch_files)
    expected_patch = "+define_pd_global(bool, InlineIntrinsics,             false); // Making it true breaks java.lang.Math"
    if expected_patch not in patch_text:
        fail("the build source does not contain the ARM32 InlineIntrinsics=false Math fix")

    try:
        with tarfile.open(archive_path, mode="r:xz") as archive:
            members = {normalized_name(member.name): member for member in archive.getmembers()}

            release_member = members.get("release")
            if release_member is None:
                fail("release metadata is missing")
            release_stream = archive.extractfile(release_member)
            if release_stream is None:
                fail("release metadata cannot be read")
            release = release_stream.read().decode("utf-8", errors="replace")

            java_version = re.search(r'^JAVA_VERSION="([^"]+)"$', release, re.MULTILINE)
            os_arch = re.search(r'^OS_ARCH="([^"]+)"$', release, re.MULTILINE)
            if not java_version or not java_version.group(1).startswith("21."):
                fail("release metadata does not identify Java 21")
            if not os_arch or os_arch.group(1) != "arm":
                fail("release metadata does not identify the ARM32 ABI")

            jvm_member = next(
                (members.get(path) for path in ("lib/server/libjvm.so", "lib/client/libjvm.so") if members.get(path)),
                None,
            )
            java_member = members.get("bin/java")
            if jvm_member is None or java_member is None:
                fail("the HotSpot VM or java launcher is missing")

            for label, member in (("libjvm.so", jvm_member), ("bin/java", java_member)):
                stream = archive.extractfile(member)
                if stream is None:
                    fail(f"{label} cannot be read")
                header = stream.read(52)
                if len(header) < 52 or header[:4] != b"\x7fELF":
                    fail(f"{label} is not a valid ELF file")
                if header[4] != 1 or header[5] != 1:
                    fail(f"{label} is not little-endian ELF32")
                machine = struct.unpack_from("<H", header, 18)[0]
                if machine != 40:  # EM_ARM
                    fail(f"{label} has ELF machine {machine}, expected ARM")
                e_flags = struct.unpack_from("<I", header, 36)[0]
                if e_flags & 0xFF000000 != 0x05000000:  # EF_ARM_EABI_VER5
                    fail(f"{label} does not declare the ARM EABI5 ABI")

    except (OSError, tarfile.TarError) as error:
        fail(f"cannot inspect runtime archive: {error}")

    print(
        f"Verified {archive_path}: Java {java_version.group(1)}, ARM32 ELF HotSpot, "
        "and source patch disabling broken inline intrinsics."
    )


if __name__ == "__main__":
    main()
