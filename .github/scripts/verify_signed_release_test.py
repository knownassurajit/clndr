#!/usr/bin/env python3
"""Fixture tests for verify_signed_release.py.

The production failure was a missing
app/build/outputs/bundle/release/output-metadata.json after a successful
bundleRelease. These tests keep that file absent.
"""

from __future__ import annotations

import json
import os
import stat
import sys
import tempfile
import textwrap
import unittest
import zipfile

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, SCRIPT_DIR)

import verify_signed_release as verify  # noqa: E402

APP_ID = "com.knownassurajit.clndr_widget.app"


def write_exe(path: str, body: str) -> None:
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(body)
    os.chmod(path, os.stat(path).st_mode | stat.S_IEXEC)


class VerifySignedReleaseTest(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = tempfile.TemporaryDirectory()
        self.root = self.tmp.name
        self.bin = os.path.join(self.root, "bin")
        os.makedirs(self.bin)
        self._old_path = os.environ.get("PATH", "")
        self._old_env = {
            key: os.environ.get(key)
            for key in ("EXPECTED_APPLICATION_ID", "EXPECTED_TARGET_SDK", "ANDROID_HOME", "ANDROID_SDK_ROOT")
        }
        os.environ["PATH"] = self.bin + os.pathsep + self._old_path
        os.environ["EXPECTED_APPLICATION_ID"] = APP_ID
        os.environ["EXPECTED_TARGET_SDK"] = "36"
        os.environ.pop("ANDROID_HOME", None)
        os.environ.pop("ANDROID_SDK_ROOT", None)
        write_exe(
            os.path.join(self.bin, "aapt2"),
            textwrap.dedent(
                f"""\
                #!/bin/sh
                echo "package: name='{APP_ID}' versionCode='41' versionName='0.0.0.5' compileSdkVersion='36'"
                echo "sdkVersion:'26'"
                echo "targetSdkVersion:'36'"
                """
            ),
        )
        write_exe(
            os.path.join(self.bin, "jarsigner"),
            textwrap.dedent(
                """\
                #!/bin/sh
                echo "jar verified."
                exit 0
                """
            ),
        )

    def tearDown(self) -> None:
        os.environ["PATH"] = self._old_path
        for key, value in self._old_env.items():
            if value is None:
                os.environ.pop(key, None)
            else:
                os.environ[key] = value
        self.tmp.cleanup()

    def plant_outputs(self, *, bundle_metadata: bool = False) -> None:
        bundle_dir = os.path.join(self.root, verify.BUNDLE_DIR)
        apk_dir = os.path.join(self.root, verify.APK_DIR)
        mapping_dir = os.path.join(self.root, os.path.dirname(verify.MAPPING))
        os.makedirs(bundle_dir)
        os.makedirs(apk_dir)
        os.makedirs(mapping_dir)
        aab_path = os.path.join(bundle_dir, "app-release.aab")
        with zipfile.ZipFile(aab_path, "w") as bundle:
            bundle.writestr("BundleConfig.pb", b"pb")
            bundle.writestr("base/manifest/AndroidManifest.xml", b"manifest")
        apk_path = os.path.join(apk_dir, "app-release.apk")
        with open(apk_path, "wb") as fh:
            fh.write(b"apk")
        metadata = {
            "applicationId": APP_ID,
            "elements": [
                {
                    "versionCode": 41,
                    "versionName": "0.0.0.5",
                    "outputFile": "app-release.apk",
                }
            ],
        }
        with open(os.path.join(apk_dir, "output-metadata.json"), "w", encoding="utf-8") as fh:
            json.dump(metadata, fh)
        with open(os.path.join(mapping_dir, "mapping.txt"), "w", encoding="utf-8") as fh:
            fh.write("mapping")
        if bundle_metadata:
            with open(os.path.join(bundle_dir, "output-metadata.json"), "w", encoding="utf-8") as fh:
                fh.write("{}\n")

    def test_finds_aab_when_bundle_metadata_is_absent(self) -> None:
        self.plant_outputs(bundle_metadata=False)
        self.assertFalse(
            os.path.exists(os.path.join(self.root, verify.BUNDLE_DIR, "output-metadata.json"))
        )
        info = verify.verify_release(self.root)
        self.assertEqual(info["path"], "app/build/outputs/bundle/release/app-release.aab")
        self.assertEqual(info["apk"], "app/build/outputs/apk/release/app-release.apk")
        self.assertEqual(info["applicationId"], APP_ID)
        self.assertEqual(info["versionName"], "0.0.0.5")
        self.assertEqual(info["versionCode"], "41")
        self.assertEqual(info["targetSdk"], "36")
        self.assertTrue(
            os.path.isfile(os.path.join(self.root, "build/ci-release/clndr-0.0.0.5.aab"))
        )

    def test_missing_aab_fails_even_without_looking_for_bundle_metadata(self) -> None:
        self.plant_outputs()
        os.remove(os.path.join(self.root, verify.BUNDLE_DIR, "app-release.aab"))
        with self.assertRaises(SystemExit):
            verify.verify_release(self.root)

    def test_rejects_wrong_target_sdk(self) -> None:
        write_exe(
            os.path.join(self.bin, "aapt2"),
            textwrap.dedent(
                f"""\
                #!/bin/sh
                echo "package: name='{APP_ID}' versionCode='41' versionName='0.0.0.5'"
                echo "targetSdkVersion:'35'"
                """
            ),
        )
        self.plant_outputs()
        with self.assertRaises(SystemExit):
            verify.verify_release(self.root)

    def test_rejects_unsigned_aab(self) -> None:
        write_exe(
            os.path.join(self.bin, "jarsigner"),
            textwrap.dedent(
                """\
                #!/bin/sh
                echo "jar is unsigned."
                exit 0
                """
            ),
        )
        self.plant_outputs()
        with self.assertRaises(SystemExit):
            verify.verify_release(self.root)

    def test_parse_badging(self) -> None:
        parsed = verify.parse_badging(
            "package: name='com.example' versionCode='7' versionName='1.2.3'\n"
            "targetSdkVersion:'36'\n"
        )
        self.assertEqual(parsed["applicationId"], "com.example")
        self.assertEqual(parsed["versionCode"], "7")
        self.assertEqual(parsed["targetSdk"], "36")


if __name__ == "__main__":
    unittest.main()
