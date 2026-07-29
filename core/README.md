# 📦 Clndr Core Module (`clndr/core`)

The `core` module contains domain models, Room database schemas, repository implementations, and shared utilities for the Clndr application.

---

## 🏗️ Directory & Layer Structure

```text
core/
├── database/              # Room Database entity definitions & DAO interfaces
│   ├── schemas/           # Room JSON migration schema exports
│   ├── entity/            # Database table entities (CalendarEvent, LifeGridEntry)
│   └── dao/               # Room Data Access Objects
├── domain/                # Business logic, state models, and use cases
└── data/                  # Repository implementations & data sources
```

---

## ⚙️ Testing

```bash
# Run unit tests for core module
./gradlew :core:database:test
```
