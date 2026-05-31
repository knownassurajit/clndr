pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "clndr"

include(
    ":app",
    ":core:datetime",
    ":core:database",
    ":core:designsystem",
    ":core:domain",
    ":feature:lifegrid",
    ":feature:milestones",
    ":feature:widgets",
)
