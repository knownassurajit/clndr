import React from 'react'

const YearProgress = () => {
    const today = new Date()
    const currentYear = today.getFullYear()
    const startOfYear = new Date(currentYear, 0, 1)
    const endOfYear = new Date(currentYear, 11, 31)

    // Calculate days passed and total days
    const oneDay = 1000 * 60 * 60 * 24
    const totalDays = Math.ceil((endOfYear - startOfYear) / oneDay) + 1
    const daysPassed = Math.floor((today - startOfYear) / oneDay) + 1
    const daysRemaining = totalDays - daysPassed
    const percentage = Math.round((daysPassed / totalDays) * 100)

    // SVG Circle properties
    const radius = 120
    const stroke = 15
    const normalizedRadius = radius - stroke * 2
    const circumference = normalizedRadius * 2 * Math.PI
    const strokeDashoffset = circumference - (percentage / 100) * circumference

    return (
        <div className="calendar-container year-progress" style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '60vh'
        }}>
            <h2 style={{ marginBottom: '2rem', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
                {currentYear} Progress
            </h2>

            <div className="dashboard" style={{ position: 'relative', width: radius * 2, height: radius * 2 }}>
                <svg
                    height={radius * 2}
                    width={radius * 2}
                    style={{ transform: 'rotate(-90deg)' }}
                >
                    <circle
                        stroke="#333"
                        strokeWidth={stroke}
                        fill="transparent"
                        r={normalizedRadius}
                        cx={radius}
                        cy={radius}
                    />
                    <circle
                        stroke="#E69138"
                        strokeDasharray={circumference + ' ' + circumference}
                        style={{ strokeDashoffset, transition: 'stroke-dashoffset 0.5s ease-in-out' }}
                        strokeWidth={stroke}
                        strokeLinecap="round" // Optional: makes ends round
                        fill="transparent"
                        r={normalizedRadius}
                        cx={radius}
                        cy={radius}
                    />
                </svg>
                <div style={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    textAlign: 'center'
                }}>
                    <span style={{ fontSize: '3rem', fontWeight: 'bold' }}>{percentage}%</span>
                </div>
            </div>

            <div className="stats" style={{
                marginTop: '3rem',
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '2rem',
                textAlign: 'center'
            }}>
                <div className="stat-item">
                    <div style={{ fontSize: '2rem', fontWeight: '600' }}>{daysPassed}</div>
                    <div style={{ opacity: 0.6, fontSize: '0.9rem' }}>DAYS PAST</div>
                </div>
                <div className="stat-item">
                    <div style={{ fontSize: '2rem', fontWeight: '600' }}>{daysRemaining}</div>
                    <div style={{ opacity: 0.6, fontSize: '0.9rem' }}>DAYS LEFT</div>
                </div>
            </div>
        </div>
    )
}

export default YearProgress
