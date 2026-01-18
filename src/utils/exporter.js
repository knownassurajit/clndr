import html2canvas from 'html2canvas'
import { saveAs } from 'file-saver'

export const downloadWallpaper = async (elementId, filename = 'life-calendar-wallpaper.png') => {
    const element = document.getElementById(elementId)
    if (!element) return

    try {
        const canvas = await html2canvas(element, {
            backgroundColor: '#000000', // Ensure dark background
            scale: 2, // High resolution
            useCORS: true
        })

        canvas.toBlob((blob) => {
            saveAs(blob, filename)
        })
    } catch (error) {
        console.error('Export failed:', error)
    }
}
