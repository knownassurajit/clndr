# The Life Calendar

A visual representation of your life in weeks. This React application helps you visualize the passage of time through different perspectives: a 90-year life grid, a yearly calendar, and a year progress circle.

## About

The Life Calendar consists of three main visualizations:

1.  **Life Calendar**: A grid representing a 90-year life in weeks. It calculates the weeks you've lived based on your birthdate and fills them in, offering a perspective on "memento mori" (remember strictly that you will die) to encourage making the most of your time. Note: Only the weeks you have lived are displayed.
2.  **Year Calendar**: A view of the current year, day by day. It visualizes past days and the current day to give you a sense of where you are in the year.
3.  **Year Progress**: A circular progress bar showing how much of the current year has passed, along with days remaining.

## Features

-   **Interactive Life Calendar**: Enter your birthdate to see your lived weeks filled in.
-   **Yearly Overview**: See the entire current year at a glance.
-   **Year Progress Tracker**: Visual percentage and day count of the current year's progress.
-   **Responsive Design**: Works on various screen sizes.
-   **Local Storage**: Remembers your birthdate for the Life Calendar view.

## Tech Stack

-   **Framework**: [React 19](https://react.dev/)
-   **Build Tool**: [Vite](https://vitejs.dev/)
-   **Language**: JavaScript (ESModules)
-   **Styling**: CSS (Variables, Grid, Flexbox)
-   **Libraries**:
    -   `html2canvas`: For potential export functionality.
    -   `file-saver`: For saving exported images.

## Installation & Getting Started

1.  **Clone the repository**
    ```bash
    git clone <repository-url>
    cd clndr
    ```

2.  **Install dependencies**
    ```bash
    npm install
    ```

3.  **Start the development server**
    ```bash
    npm run dev
    ```

4.  **Build for production**
    ```bash
    npm run build
    ```

## Usage

1.  Open the application in your browser.
2.  Navigate between tabs: "LIFE CALENDAR", "YEAR CALENDAR", and "YEAR PROGRESS".
3.  **Life Calendar**: Enter your birthdate in the input field. The grid will update to show filled boxes for the weeks you have lived.
4.  **Year Calendar**: View the current year's days.
5.  **Year Progress**: View the current completion status of the year.

## Credits

Created by Surajit Das.
