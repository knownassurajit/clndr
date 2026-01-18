import React, { useState, useEffect } from 'react'
import LifeCalendar from './components/LifeCalendar'
import YearCalendar from './components/YearCalendar'
import YearProgress from './components/YearProgress'
import logo from './logo_full.svg'
import './index.css'

function App() {
  const [view, setView] = useState('life') // 'life' | 'year' | 'goal'
  const [birthdate, setBirthdate] = useState(() => {
    return localStorage.getItem('birthdate') || '1990-01-01'
  })

  useEffect(() => {
    localStorage.setItem('birthdate', birthdate)
  }, [birthdate])

  return (
    <div className="layout">
      <header>
        <img src={logo} alt="THE LIFE CALENDAR" style={{ maxHeight: '80px', margin: '0 auto', display: 'block' }} />
      </header>

      <nav className="nav-tabs">
        <button
          className={view === 'life' ? 'active' : ''}
          onClick={() => setView('life')}
        >
          LIFE CALENDAR
        </button>
        <button
          className={view === 'year' ? 'active' : ''}
          onClick={() => setView('year')}
        >
          YEAR CALENDAR
        </button>
        <button
          className={view === 'progress' ? 'active' : ''}
          onClick={() => setView('progress')}
        >
          YEAR PROGRESS
        </button>
      </nav>

      <main id="calendar-export-target" style={{ padding: '2rem', background: '#000', minHeight: '600px' }}>
        {view === 'life' && (
          <LifeCalendar birthdate={birthdate} setBirthdate={setBirthdate} />
        )}
        {view === 'year' && (
          <YearCalendar />
        )}
        {view === 'progress' && (
          <YearProgress />
        )}
      </main>

      <footer>
        <p style={{ opacity: 0.5, fontSize: '0.8rem', marginTop: '3rem' }}>
          Created by Surajit Das
        </p>
      </footer>
    </div>
  )
}

export default App
