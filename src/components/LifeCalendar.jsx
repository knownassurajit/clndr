import React, { useMemo } from 'react'

const LifeCalendar = ({ birthdate, setBirthdate }) => {
    const TOTAL_YEARS = 90
    const WEEKS_PER_YEAR = 52
    const TOTAL_WEEKS = TOTAL_YEARS * WEEKS_PER_YEAR

    const weeksLived = useMemo(() => {
        if (!birthdate) return { weeks: 0, days: 0 }
        const birth = new Date(birthdate)
        const now = new Date()
        const normalizedDiff = Math.max(0, now - birth)
        const diffWeeks = Math.floor(normalizedDiff / (1000 * 60 * 60 * 24 * 7))
        const diffDays = Math.floor(normalizedDiff / (1000 * 60 * 60 * 24))
        return { weeks: diffWeeks, days: diffDays }
    }, [birthdate])

    return (
        <div className="calendar-container life-calendar">
            <div className="controls" style={{ marginBottom: '1rem', display: 'flex', gap: '1rem', alignItems: 'center', justifyContent: 'center' }}>
                <label>
                    Your Birthday:
                    <input
                        type="date"
                        value={birthdate}
                        onChange={(e) => setBirthdate(e.target.value)}
                        style={{
                            background: 'transparent',
                            border: '1px solid #333',
                            color: '#fff',
                            padding: '0.5rem',
                            marginLeft: '0.5rem',
                            fontFamily: 'inherit'
                        }}
                    />
                </label>
                <span>
                    ({weeksLived.days} days / {weeksLived.weeks} weeks lived)
                </span>
            </div>

            <div className="grid-container" style={{
                display: 'grid',
                gridTemplateColumns: `repeat(${WEEKS_PER_YEAR}, 1fr)`,
                gap: '2px',
                maxWidth: '100%',
                overflowX: 'auto'
            }}>
                {Array.from({ length: TOTAL_WEEKS }).map((_, index) => (
                    <div
                        key={index}
                        style={{
                            width: '6px',
                            height: '6px',
                            backgroundColor: index < weeksLived.weeks ? 'var(--filled-color, #fff)' : 'var(--empty-color, #333)',
                            borderRadius: '1px' // rounding slightly specifically for screen rendering, might remove for 'print' feel
                        }}
                        title={`Week ${index + 1}`}
                    />
                ))}
            </div>
        </div>
    )
}

export default LifeCalendar
