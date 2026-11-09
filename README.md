# MediMate

Smart health management Android app built with Kotlin. Helps patients, elderly people, and caregivers manage medicines, track health data, and get AI-assisted report summaries.

## Features

- Medicine list with dosage and schedule
- Reminders and alarms
- Low stock alerts
- BP and blood sugar logging with charts
- Emergency SOS with location sharing
- Caregiver monitoring
- Medical report upload with OCR
- AI-based report summary (with disclaimer)
- Voice commands
- Doctor connect

## Target Users

- Chronic patients (diabetes, hypertension)
- Elderly people
- Caregivers and family members
- Doctors and medical staff


## Tech Stack

- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM
- Local DB: Room
- DI: Hilt
- Background work: WorkManager, AlarmManager
- Backend: Firebase (Auth, Firestore, Storage, FCM)
- ML: ML Kit Text Recognition, OpenCV
- Networking: Retrofit, OkHttp
- Charts: Vico
