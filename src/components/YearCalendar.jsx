import React, { useMemo } from 'react'

const YearCalendar = () => {
    const currentYear = new Date().getFullYear()

    const months = useMemo(() => {
        return Array.from({ length: 12 }).map((_, monthIndex) => {
            // Get number of days in this month
            // new Date(year, month + 1, 0).getDate() returns last day of month
            const daysInMonth = new Date(currentYear, monthIndex + 1, 0).getDate()
            const days = Array.from({ length: daysInMonth }).map((_, dayIndex) => {
                const date = new Date(currentYear, monthIndex, dayIndex + 1)
                // Reset time for accurate comparison if needed, but simple < works for past including today if we want strictly past, 
                // usually current day is "active" or "unfilled" depending on philosophy. 
                // Let's make today and past filled? Or just past? 
                // Life calendar fills passed time. Let's fill past days (strictly before today).
                // Actually, let's include today as filled or make it special?
                // Let's just fill strictly past days for now.
                return {
                    date,
                    isPast: date.setHours(0, 0, 0, 0) < new Date().setHours(0, 0, 0, 0),
                    isToday: date.toDateString() === new Date().toDateString()
                }
            })

            const dateForName = new Date(currentYear, monthIndex, 1)
            const monthName = dateForName.toLocaleString('default', { month: 'long' })

            return {
                name: monthName,
                days
            }
        })
    }, [currentYear])

    return (
        <div className="calendar-container year-calendar">
            <div className="controls" style={{ marginBottom: '2rem', textAlign: 'center' }}>
                <h2>{currentYear}</h2>
            </div>

            <div className="months-grid" style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '2rem',
                maxWidth: '100%'
            }}>
                {months.map((month) => (
                    <div key={month.name} className="month-block">
                        <h3 style={{
                            fontSize: '0.9rem',
                            marginBottom: '0.5rem',
                            textTransform: 'uppercase',
                            opacity: 0.8
                        }}>{month.name}</h3>

                        <div className="days-grid" style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(7, 1fr)',
                            gap: '4px'
                        }}>
                            {month.days.map((day) => (
                                <div
                                    key={day.date.toISOString()}
                                    title={day.date.toDateString()}
                                    style={{
                                        aspectRatio: '1',
                                        backgroundColor: day.isToday ? '#E69138' : (day.isPast ? '#fff' : '#333'),
                                        opacity: day.isToday ? 1 : (day.isPast ? 0.8 : 0.3),
                                        borderRadius: '1px'
                                    }}
                                />
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default YearCalendar
