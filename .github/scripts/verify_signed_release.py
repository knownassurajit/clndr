#!/usr/bin/env python3
"""Verify the signed release AAB produced by stable-release.

AGP 8.9 ``bundleRelease`` writes the signed bundle to
``app/build/outputs/bundle/release/<module>-release.aab``. It does not write
``output-metadata.json`` beside that file. Bundle listing metadata is an IDE
redirect under ``app/build/intermediates/bundle_ide_model`` and omits
versionName and versionCode. This script therefore finds the AAB directly and
reads package, version, and targetSdk from the release APK built by the same
``assembleRelease`` invocation.
"""

from __future__ import annotations

import glob
import json
import os
import re
import shutil
import subprocess
import sys
import zipfile

BUNDLE_DIR = "app/build/outputs/bundle/release"
APK_DIR = "app/build/outputs/apk/release"
APK_METADATA = os.path.join(APK_DIR, "output-metadata.json")
MAPPING = "app/build/outputs/mapping/release/mapping.txt"
OUT_DIR = "build/ci-release"


def fail(message: str) -> None:
    print(f"::error::{message}")
    sys.exit(1)


def load_json(path: str) -> dict:
    with open(path, encoding="utf-8") as fh:
        return json.load(fh)


def resolve_listed_output(metadata_path: str, output_file: str) -> str:
    if os.path.isabs(output_file):
        return os.path.normpath(output_file)
    return os.path.normpath(os.path.join(os.path.dirname(metadata_path), output_file))


def require_under(path: str, parent: str, suffix: str) -> str:
    parent_abs = os.path.abspath(parent)
    path_abs = os.path.abspath(path)
    try:
        inside = os.path.commonpath([parent_abs, path_abs]) == parent_abs
    except ValueError:
        inside = False
    if not inside or not path_abs.endswith(suffix):
        fail(f"Unexpected artifact path {path!r}; expected a {suffix} under {parent}.")
    if not os.path.isfile(path_abs):
        fail(f"Artifact was not found at {path}.")
    return path_abs


def find_aapt2() -> str:
    found = shutil.which("aapt2")
    if found:
        return found
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if not sdk:
        fail("aapt2 was not found. Install Android build-tools or set ANDROID_HOME.")
    tools = os.path.join(sdk, "build-tools")
    if os.path.isdir(tools):
        for version in sorted(os.listdir(tools), reverse=True):
            candidate = os.path.join(tools, version, "aapt2")
            if os.path.isfile(candidate) and os.access(candidate, os.X_OK):
                return candidate
    fail(f"aapt2 was not found under {tools}.")


def parse_badging(text: str) -> dict:
    package = re.search(
        r"package: name='([^']+)' versionCode='(\d+)' versionName='([^']*)'",
        text,
    )
    target = re.search(r"targetSdkVersion:'(\d+)'", text)
    if not package or not target:
        fail("aapt2 dump badging did not report package name, version, and targetSdkVersion.")
    return {
        "applicationId": package.group(1),
        "versionCode": package.group(2),
        "versionName": package.group(3),
        "targetSdk": target.group(1),
    }


def dump_badging(aapt2: str, apk_path: str) -> dict:
    result = subprocess.run(
        [aapt2, "dump", "badging", apk_path],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        sys.stdout.write(result.stdout)
        sys.stderr.write(result.stderr)
        fail(f"aapt2 dump badging failed for {apk_path}.")
    return parse_badging(result.stdout + "\n" + result.stderr)


def assert_signed_aab(aab_path: str) -> None:
    try:
        with zipfile.ZipFile(aab_path) as bundle:
            names = set(bundle.namelist())
    except zipfile.BadZipFile:
        fail(f"Release bundle is not a zip: {aab_path}.")
    if "BundleConfig.pb" not in names or not any(
        name.endswith("AndroidManifest.xml") for name in names
    ):
        fail(f"{aab_path} is not an Android App Bundle (missing BundleConfig.pb or manifest).")

    verify = subprocess.run(
        ["jarsigner", "-verify", aab_path],
        capture_output=True,
        text=True,
    )
    verify_text = f"{verify.stdout}\n{verify.stderr}".lower()
    # -strict treats a self-signed Play upload key as a failure.
    # An unsigned jar can also exit 0, so require the "jar verified" line.
    if verify.returncode != 0 or "jar verified" not in verify_text or "jar is unsigned" in verify_text:
        sys.stdout.write(verify.stdout)
        sys.stderr.write(verify.stderr)
        fail(
            "Release AAB is not signed. Check KEYSTORE_B64, CLNDR_STORE_PASSWORD, "
            "CLNDR_KEY_ALIAS, and CLNDR_KEY_PASSWORD."
        )


def locate_aab(root: str) -> str:
    bundle_dir = os.path.join(root, BUNDLE_DIR)
    aabs = sorted(glob.glob(os.path.join(bundle_dir, "*.aab")))
    if len(aabs) != 1:
        present = sorted(os.listdir(bundle_dir)) if os.path.isdir(bundle_dir) else []
        fail(
            f"Expected exactly one signed AAB in {BUNDLE_DIR}, found {len(aabs)}. "
            f"Directory contents: {present}. "
            "AGP 8.9 does not write output-metadata.json next to the AAB; "
            "bundleRelease still writes the .aab in that directory."
        )
    return require_under(aabs[0], bundle_dir, ".aab")


def locate_apk(root: str) -> tuple[str, dict]:
    metadata_path = os.path.join(root, APK_METADATA)
    apk_dir = os.path.join(root, APK_DIR)
    if os.path.isfile(metadata_path):
        meta = load_json(metadata_path)
        elements = meta.get("elements") or []
        if len(elements) != 1:
            fail(f"Expected exactly one release APK, found {len(elements)} in {APK_METADATA}.")
        output_file = elements[0].get("outputFile") or ""
        apk_path = resolve_listed_output(metadata_path, output_file)
        apk_path = require_under(apk_path, apk_dir, ".apk")
        return apk_path, meta

    apks = sorted(glob.glob(os.path.join(apk_dir, "*.apk")))
    if len(apks) != 1:
        fail(f"Expected exactly one release APK in {APK_DIR}, found {len(apks)}.")
    return require_under(apks[0], apk_dir, ".apk"), {}


def verify_release(root: str) -> dict:
    expected_app = os.environ["EXPECTED_APPLICATION_ID"]
    expected_target = os.environ.get("EXPECTED_TARGET_SDK", "36")

    aab_path = locate_aab(root)
    assert_signed_aab(aab_path)
    apk_path, apk_meta = locate_apk(root)

    meta_app = apk_meta.get("applicationId")
    if meta_app and meta_app != expected_app:
        fail(
            f"Release APK applicationId is {meta_app!r}; Play packageName must be {expected_app!r}."
        )

    badging = dump_badging(find_aapt2(), apk_path)
    if badging["applicationId"] != expected_app:
        fail(
            f"Release APK package is {badging['applicationId']!r}; "
            f"Play packageName must be {expected_app!r}."
        )
    if badging["targetSdk"] != expected_target:
        fail(
            f"Release APK targetSdkVersion is {badging['targetSdk']}; expected {expected_target}."
        )

    element = (apk_meta.get("elements") or [{}])[0]
    meta_name = str(element.get("versionName") or "")
    meta_code = element.get("versionCode")
    meta_code_text = "" if meta_code is None else str(meta_code)
    if meta_name and meta_name != badging["versionName"]:
        fail(
            f"APK metadata versionName {meta_name!r} does not match badging {badging['versionName']!r}."
        )
    if meta_code_text and meta_code_text != badging["versionCode"]:
        fail(
            f"APK metadata versionCode {meta_code_text} does not match badging {badging['versionCode']}."
        )

    version_name = badging["versionName"]
    version_code = badging["versionCode"]
    if not version_name or not version_code.isdigit():
        fail("Release APK is missing versionName or versionCode.")
    code_int = int(version_code)
    if code_int <= 0 or code_int > 2_100_000_000:
        fail(f"versionCode {code_int} is outside Play's 1..2100000000 range.")

    mapping = os.path.join(root, MAPPING)
    if not os.path.isfile(mapping):
        print("::warning::Proguard mapping.txt was not produced; Play will not receive a deobfuscation file.")
        mapping = ""

    out_dir = os.path.join(root, OUT_DIR)
    os.makedirs(out_dir, exist_ok=True)
    shutil.copyfile(aab_path, os.path.join(out_dir, f"clndr-{version_name}.aab"))
    shutil.copyfile(apk_path, os.path.join(out_dir, f"clndr-{version_name}.apk"))
    if mapping:
        shutil.copyfile(mapping, os.path.join(out_dir, "mapping.txt"))

    # Paths published to later steps stay workspace-relative, matching the previous step.
    relative_aab = os.path.relpath(aab_path, root)
    relative_apk = os.path.relpath(apk_path, root)
    relative_mapping = os.path.relpath(mapping, root) if mapping else ""
    return {
        "path": relative_aab,
        "apk": relative_apk,
        "mapping": relative_mapping,
        "versionName": version_name,
        "versionCode": version_code,
        "applicationId": expected_app,
        "targetSdk": badging["targetSdk"],
    }


def main() -> None:
    info = verify_release(os.getcwd())
    short_sha = os.environ["GITHUB_SHA"][:7]
    output_path = os.environ["GITHUB_OUTPUT"]
    with open(output_path, "a", encoding="utf-8") as fh:
        fh.write(f"path={info['path']}\n")
        fh.write(f"apk={info['apk']}\n")
        fh.write(f"mapping={info['mapping']}\n")
        fh.write(f"versionName={info['versionName']}\n")
        fh.write(f"versionCode={info['versionCode']}\n")
        fh.write(f"applicationId={info['applicationId']}\n")
        fh.write(f"targetSdk={info['targetSdk']}\n")
        fh.write(f"shortSha={short_sha}\n")
    print(
        "Signed AAB {path} applicationId={applicationId} versionName={versionName} "
        "versionCode={versionCode} targetSdk={targetSdk}".format(**info)
    )


if __name__ == "__main__":
    main()
