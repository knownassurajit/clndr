# CI/CD (`clndr/.github`)

```text
.github/
├── dependabot.yml              Weekly Gradle + Actions updates
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
| `test` | push to develop/master, all PRs | Detekt, lint, unit tests, datetime JVM tests |
| `dependency-review` | PRs | Fail on high-severity dependency advisories |
| `debug-release` | push to develop | GitHub pre-release debug APK |
| `pr-summary` | PRs into master | Sticky check summary + changelog |
| `stable-release` | push to master | Signed APK/AAB, GitHub release, optional Play internal track |

Actions are SHA-pinned. Workflow default permissions are `read-all`; write is granted per job.
