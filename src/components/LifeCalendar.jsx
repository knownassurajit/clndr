import React from 'react'

const LifeCalendar = ({ birthdate, setBirthdate }) => {

    // Calculate days lived directly in render to avoid any memoization staleness.
    // This is a fast calculation (math only), so performance impact is negligible.
    const calculateDaysLived = (dob) => {
        if (!dob) return 0
        const birth = new Date(dob)
        const now = new Date()

        // Reset hours to ensure clean day calculation
        birth.setHours(0, 0, 0, 0)
        now.setHours(0, 0, 0, 0)

        const diff = Math.max(0, now - birth)
        return Math.floor(diff / (1000 * 60 * 60 * 24))
    }

    const daysLived = calculateDaysLived(birthdate)

    return (
        <div className="calendar-container life-calendar md-card">
            <header className="card-header">
                <h2>Life Progress</h2>
                <div className="controls">
                    <div className="input-group">
                        <label htmlFor="birthdate">Date of Birth</label>
                        <input
                            id="birthdate"
                            type="date"
                            value={birthdate}
                            onChange={(e) => setBirthdate(e.target.value)}
                        />
                    </div>
                </div>
            </header>

            <div className="stats-summary">
                <span className="highlight-text">{daysLived.toLocaleString()}</span> days lived
            </div>

            <div className="days-grid-container">
                {/* 
                 Using CSS Grid with auto-fill is cleaner than manually calculating columns.
                 However, "days" are small squares.
                */}
                <div className="dynamic-days-grid">
                    {Array.from({ length: daysLived }).map((_, index) => (
                        <div
                            key={index}
                            className="day-cell lived"
                            title={`Day ${index + 1}`}
                        />
                    ))}
                </div>
            </div>

            <div className="motivation-text">
                Every square represents a day you have lived. Make them count.
            </div>
        </div>
    )
}

export default LifeCalendar
