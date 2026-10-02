# CI/CD (`clndr/.github`)

```text
.github/
├── dependabot.yml              Weekly Gradle + Actions updates
├── scripts/verify_signed_release.py
├── workflows/ci-cd.yml         develop → master pipeline
└── README.md
```

## Branching

- `develop` — integration. Feature PRs land here.
- `master` — production. Only promoted from `develop`.
- `release/clndr/<version>` — rollback snapshot created by `stable-release`.

## Jobs (`workflows/ci-cd.yml`)

| Job | Trigger | Purpose |
|---|---|---|
| `test` | push to develop/master/main, all PRs | Detekt, lint, unit tests, datetime JVM tests |
| `dependency-review` | PRs | Fail on high-severity dependency advisories |
| `debug-release` | push to develop | GitHub pre-release debug APK |
| `pr-summary` | PRs into master or main | Sticky check summary + changelog |
| `stable-release` | push to `master` or `main` | Signed release AAB/APK, GitHub release, Play **internal** track when secrets exist |

Actions are SHA-pinned. Workflow default permissions are `read-all`; write is granted per job.

## Production release (`master` → Play)

Production is `master`. `main` is included so a branch rename still publishes. A push to that branch, after `test` passes, runs `stable-release`:

1. Build a **signed** release APK and AAB (`assembleRelease`, `bundleRelease`). The job fails if the signing secrets are missing or the AAB is unsigned.
2. Take the signed AAB from `app/build/outputs/bundle/release/*.aab`. AGP 8.9 does not write `output-metadata.json` in that directory (bundle listing metadata is an intermediates IDE file and has no version). Require package `com.knownassurajit.clndr_widget.app` and `targetSdk` 36 from the release APK built in the same step, and require `jarsigner -verify` to report the AAB as signed.
3. Upload that AAB to the Play **internal** track (`status: completed`) when `PLAY_CONSOLE_JSON` is set.
4. Publish a GitHub Release and move `release/clndr/<versionName>` to the built commit.

Play versionCode is the formula in `app/build.gradle.kts` plus `github.run_number`, so a later master merge is accepted even when `versionName` did not change. `versionName` itself is unchanged. A manual rollback (`workflow_dispatch` on `master` or `main`, input `rollback_version` like `v0.0.0.4`) checks out that tag and sets an absolute `yyyyMMddHH` versionCode.

No secret values live in the repo. If a secret is absent, the workflow does not invent one.

### Required secrets

| Secret | Required for | Value |
|---|---|---|
| `KEYSTORE_B64` | Signed AAB | Base64 of the upload keystore (`base64 -w0 upload.jks`). Whitespace is ignored. |
| `CLNDR_STORE_PASSWORD` | Signed AAB | Keystore password |
| `CLNDR_KEY_ALIAS` | Signed AAB | Key alias inside the keystore |
| `CLNDR_KEY_PASSWORD` | Signed AAB | Key password |
| `PLAY_CONSOLE_JSON` | Play upload | Raw JSON key for a Google service account. Not base64. |

`PLAY_CONSOLE_JSON` is optional for the GitHub Release. Without it the signed AAB is still built and attached to the GitHub Release, and the log says Play was skipped. With it, the internal-track upload runs. Do not gate that step on `if: env.PLAY_CONSOLE_JSON != ''` — a multiline JSON key makes that expression skip the upload even when the secret is set.

### One-time Play Console setup

1. Create a Cloud service account, download its JSON key, and store the file contents in `PLAY_CONSOLE_JSON`.
2. In Play Console → Users and permissions, invite that service account and grant it access to `com.knownassurajit.clndr_widget.app`, including releases to testing tracks.
3. The Play Developer API returns "package not found" until the app exists on the account. Create the app once in the Console (store listing name `clndr`) and upload the first AAB by hand if the API has never seen the package.
4. Internal testing does not replace a production rollout. Promote the release in the Console when you want a wider track.

If the API responds that changes cannot be sent for review automatically, set `changesNotSentForReview: true` on the upload step. If it responds that the parameter must not be set, leave it unset. The default in this workflow is unset.
