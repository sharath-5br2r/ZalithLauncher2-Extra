#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <Pojav build directory> <output bin-arm.tar.xz>" >&2
  exit 2
fi

build_dir=$(realpath "$1")
output=$(realpath -m "$2")
mapfile -t archives < <(find "$build_dir" -maxdepth 1 -type f -name 'jre21-arm-*-release.tar.xz' -print | sort)
if [[ ${#archives[@]} -ne 1 ]]; then
  printf 'Expected exactly one jre21-arm release archive in %s; found %d\n' "$build_dir" "${#archives[@]}" >&2
  printf '  %s\n' "${archives[@]}" >&2
  exit 1
fi

work=$(mktemp -d "${TMPDIR:-/tmp}/mirai-jre21-arm32.XXXXXX")
trap 'rm -rf "$work"' EXIT
mkdir -p "$work/extracted" "$work/packed/lib" "$(dirname "$output")"
tar -xJf "${archives[0]}" -C "$work/extracted"

if [[ ! -d "$work/extracted/bin" || ! -d "$work/extracted/lib" ]]; then
  echo "The raw JRE archive is missing its bin/ or lib/ directory." >&2
  exit 1
fi
mv "$work/extracted/bin" "$work/packed/bin"

for file in jexec jvm.cfg; do
  if [[ -e "$work/extracted/lib/$file" || -L "$work/extracted/lib/$file" ]]; then
    mv "$work/extracted/lib/$file" "$work/packed/lib/$file"
  fi
done

for vm in server client; do
  if [[ -d "$work/extracted/lib/$vm" ]]; then
    mv "$work/extracted/lib/$vm" "$work/packed/lib/$vm"
  fi
done

# Match the launcher's per-ABI archive layout: keep the VM directories intact, and place
# the remaining architecture-specific shared libraries directly under lib/.
while IFS= read -r -d '' library; do
  destination="$work/packed/lib/$(basename "$library")"
  if [[ -e "$destination" || -L "$destination" ]]; then
    echo "Duplicate flattened shared library name: $(basename "$library")" >&2
    exit 1
  fi
  mv "$library" "$destination"
done < <(find "$work/extracted" \( -type f -o -type l \) -name '*.so' -print0)

if [[ -f "$work/extracted/release" ]]; then
  mv "$work/extracted/release" "$work/packed/release"
else
  echo "The raw JRE archive is missing its release metadata." >&2
  exit 1
fi

if [[ ! -f "$work/packed/lib/server/libjvm.so" && ! -f "$work/packed/lib/client/libjvm.so" ]]; then
  echo "The repacked JRE has no ARM32 HotSpot libjvm.so." >&2
  exit 1
fi

rm -f "$output"
tar -cJf "$output" -C "$work/packed" .
printf 'Repacked %s -> %s (%s)\n' "${archives[0]}" "$output" "$(du -h "$output" | cut -f1)"
