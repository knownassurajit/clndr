# The Life Calendar - Project Documentation

## 1. Project Overview
**The Life Calendar** is a React-based web application designed to help users visualize the passage of time. It draws inspiration from the concept of "Memento Mori," but focuses on the *days lived* to provide a dynamic and personalized perspective.

The application offers three distinct perspectives:
1.  **Life Progress**: A dynamic grid showing every single day you have lived.
2.  **Year Calendar**: A daily grid view of the current year.
3.  **Year Progress**: A circular progress visualization of the current year.

## 2. Technology Stack

### Core Framework & Build Tools
-   **React 19**: The core library for building the user interface. Uses functional components and Hooks (`useState`, `useEffect`, `useMemo`).
-   **Vite 7.2.4**: A modern frontend build tool for fast development and production builds.

### Styling & Design System
-   **Design Language**: **Material Design (Dark Theme)**.
-   **Typography**: **Roboto** (via Google Fonts).
-   **CSS3**:
    -   **CSS Variables**: Used for Material Design color tokens (e.g., `--md-sys-color-primary`, `--md-sys-color-surface`).
    -   **Flexbox**: Used for the dynamic fluid grid of days.
    -   **CSS Grid**: Used for the structured year calendar.
    -   **Shadows**: Elevation levels (`--md-sys-elevation-1` to `3`) for depth.

## 3. Project Structure

```
clndr/
├── src/
│   ├── components/       # UI Components
│   │   ├── LifeCalendar.jsx   # Dynamic "days lived" grid
│   │   ├── YearCalendar.jsx   # Current year daily grid
│   │   └── YearProgress.jsx   # Circular year progress
│   ├── utils/            # Helper functions
│   │   └── exporter.js        # Logic for saving views (unused)
│   ├── App.jsx           # Main application layout & state
│   ├── main.jsx          # Entry point
│   ├── App.css           # Component styles (Material implementation)
│   ├── index.css         # Global variables & reset
│   └── logo_full.svg     # Project assets
├── public/               # Static assets
│   └── logo_icon.svg     # Favicon
├── index.html            # HTML entry point (Fonts added)
└── vite.config.js        # Vite configuration
```

## 4. Component Architecture & Data Flow

### 4.1. Application Entry (`main.jsx`)
Entry point that mounts `App` within `React.StrictMode` and an `ErrorBoundary`.

### 4.2. Main Layout (`App.jsx`)
-   **State**: Manages `view` (current tab) and `birthdate`.
-   **Design**: Implements a centralized container layout with Material Design tabs.

### 4.3. Life Calendar (`LifeCalendar.jsx`)
**Refactored Logic**:
-   **Dynamic Days**: Removes the fixed "90-year" limit.
-   **Calculation**: Computes exact `daysLived` between birthdate and today.
-   **Rendering**: Renders a flex container with thousands of small `6px` squares.
    -   *Note*: For a 30-year-old, this renders ~11,000 DOM elements.
-   **Aesthetics**: Uses a dark card layout with floating input labels.

![Life Calendar Material Design](/Users/knownassurajit/.gemini/antigravity/brain/d94bfbae-9d33-421e-be8c-82a8831df5a6/life_calendar_material_1769019059241.png)
*Figure 1: The new Life Calendar with Material Design. Displays the total count of days lived.*

### 4.4. Year Calendar (`YearCalendar.jsx`)
**Purpose**: Displays the days of the current year.
-   **Design Update**: Styled to match the Material Design card aesthetic.
-   **Grid**: Maintains the structured monthly grid layout.

![Year Calendar Material Design](/Users/knownassurajit/.gemini/antigravity/brain/d94bfbae-9d33-421e-be8c-82a8831df5a6/year_calendar_material_1769019253390.png)
*Figure 2: Year Calendar view consistent with the dark theme.*

### 4.5. Year Progress (`YearProgress.jsx`)
**Purpose**: Circular progress dashboard.
-   **Design Update**: Typography updated to Roboto; container styled as a card.

## 5. UI/UX Improvements

### Material Design Implementation
-   **Dark Mode**: Deep `#121212` background with `#1E1E1E` surface cards.
-   **Elevation**: Cards use subtle shadows to float above the background.
-   **Interaction**: Inputs have focus states; buttons have hover effects.
-   **Color Palette**:
    -   **Primary**: `#E69138` (Amber/Gold)
    -   **Surface**: `#1E1E1E` (Dark Grey)

## 6. Setup & Installation

1.  **Install Dependencies**: `npm install`
2.  **Development**: `npm run dev`
3.  **Build**: `npm run build`
