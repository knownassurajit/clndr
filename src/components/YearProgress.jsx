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
        <div className="calendar-container year-progress md-card" style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '60vh'
        }}>
            <div className="card-header" style={{ width: '100%', justifyContent: 'center' }}>
                <h2 style={{ textTransform: 'uppercase', letterSpacing: '0.1em', textAlign: 'center' }}>
                    {currentYear} Progress
                </h2>
            </div>

            <div className="dashboard" style={{ position: 'relative', width: radius * 2, height: radius * 2 }}>
                <svg
                    height={radius * 2}
                    width={radius * 2}
                    style={{ transform: 'rotate(-90deg)' }}
                >
                    <circle
                        stroke="var(--md-sys-color-surface-container-highest)"
                        strokeWidth={stroke}
                        fill="transparent"
                        r={normalizedRadius}
                        cx={radius}
                        cy={radius}
                    />
                    <circle
                        stroke="var(--md-sys-color-tertiary)"
                        strokeDasharray={circumference + ' ' + circumference}
                        style={{ strokeDashoffset, transition: 'stroke-dashoffset 0.8s cubic-bezier(0.2, 0, 0, 1)' }}
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
                    <span style={{ fontSize: '3.5rem', fontWeight: '500', color: 'var(--md-sys-color-on-surface)' }}>{percentage}%</span>
                </div>
            </div>

            <div className="stats" style={{
                marginTop: '3.5rem',
                display: 'grid',
                gridTemplateColumns: 'minmax(120px, 1fr) minmax(120px, 1fr)',
                gap: '2.5rem',
                textAlign: 'center'
            }}>
                <div className="stat-item">
                    <div className="stat-value">{daysPassed}</div>
                    <div className="stat-label">DAYS PAST</div>
                </div>
                <div className="stat-item">
                    <div className="stat-value">{daysRemaining}</div>
                    <div className="stat-label">DAYS LEFT</div>
                </div>
            </div>
        </div>
    )
}

export default YearProgress
