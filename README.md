# Medicare

**A reliable, accessible medicine reminder and health records app for Android.**

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26-informational)
![Status](https://img.shields.io/badge/status-MVP-yellow)

Medicare helps people take their medicines on time, especially elderly patients and people on short treatment courses (1 to 4 weeks). It works fully offline, keeps all data on the phone, and is designed to be usable by people with low vision, hearing difficulty or limited mobility.



## Why Medicare

Many patients forget doses, run out of tablets without noticing, or stop taking medicines at the wrong time. Existing reminder apps often assume a lifelong schedule. Medicare is built around **treatment courses**: you say how long you will take a medicine (1, 2, 3 weeks, a month, custom or ongoing), and the app stops reminding you when the course is finished.

## Features

### Reminders and alarms
- Exact alarms that work offline and survive phone restarts
- Full-screen alarm over the lock screen with large **Taken / Snooze 10 min / Skip** buttons
- The same actions are available directly from the notification
- Reminders stop automatically when a course ends

### Medicines
- Add, edit and delete medicines with dosage, time slots (morning, noon, evening, night) and custom times
- Course length: 1, 2, 3 weeks, 1 month, custom days or ongoing
- Categories (diabetes, blood pressure, cholesterol, heart, infection, cold and fever, pain, stomach, vitamins, other) with the medicine list grouped by category
- Stock tracking with a low-stock threshold, automatic stock reduction when a dose is taken, and a low-stock notification
- A friendly "course complete" message on the home screen when a course finishes

### Today screen
- Next dose card with one-tap **Taken / Skip**
- Automatic **Missed** detection (2 hours after the scheduled time) and an **I took it** correction button
- Low-stock banner and a daily progress summary

### History and sharing
- Adherence percentage, current streak, weekly overview and a day-by-day log (7 days, 30 days or all)
- Filter by medicine, including medicines that were later deleted
- **Share a report on WhatsApp** (or any app) with a doctor or caregiver, in English, Sinhala or Tamil

### Medical records
- Store doctor receipts and lab reports with a title, doctor or lab name, date, notes and a photo (camera or gallery)
- Filter by type, search by text, pinch-to-zoom on photos

### Profile and safety
- Profile with name, age, phone, address and photo
- Current location (on demand), saved on the phone only
- Medicine allergy list with reactions
- One-tap emergency contacts (ambulance, police, family doctor, caregiver)

### Accessibility and personalization
- TalkBack labels, large touch targets and non-colour-only status indicators
- Three text sizes (100%, 115%, 130%) on top of the system font size
- Light, dark and follow-system themes
- Languages: English, සිංහල (Sinhala) and தமிழ் (Tamil), switchable inside the app

## Privacy

Medicare has **no account, no backend and no analytics**. Medicines, dose history, profile, photos and records are stored only on the device. Location is read only when you tap the button, and it is never included in shared reports. Uninstalling the app removes all data.

## Tech stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| State and architecture | ViewModel, StateFlow, feature-based packages |
| Local storage | Jetpack DataStore (JSON), app-private file storage for images |
| Background work | AlarmManager (exact alarms), BroadcastReceivers, notifications with full-screen intent |
| Location | Fused Location Provider (Google Play services), Geocoder |
| Images | Android Photo Picker, camera via FileProvider, on-device compression |
| Localization | Android resources (en, si, ta) with in-app language switching |

## Architecture

The project is organised **by feature**. Newer features (profile, records) also separate the layers:

- `view/` contains stateless Compose screens that only receive a UI state and a set of action callbacks
- `logic/` contains the ViewModel and the `Route` composable that wires UI actions to real behaviour (permissions, launchers, navigation)
- `data/` contains models and repositories

This keeps UI easy to change and lets new components be added without touching unrelated features.

```
app/src/main/java/com/example/medicare_assistant/
├── core/          Shared helpers (DataStore, date and time formatting)
├── dose/          Dose log, dose rules, missed-dose finalizer
├── history/       Adherence, streaks, report builder and sharing
├── home/          Today screen
├── medicine/      Medicines, add/edit form, repository
├── navigation/    Main tabs and overlay pages
├── onboarding/    Onboarding pages
├── profile/       Profile, edit profile, allergies (data / view / logic)
├── records/       Medical records (data / view / logic)
├── reminder/      Alarm scheduling, receivers, notifications, alarm screen
├── settings/      Theme, language and text size
└── ui/            Theme and shared components
```

## Getting started

### Requirements
- Android Studio (latest stable)
- A device or emulator running Android 8.0 (API 26) or higher
- A **real device** is recommended for testing alarms and location

### Run the app
```bash
git clone https://github.com/Lemith020/<repository-name>.git
cd <repository-name>
```
1. Open the project in Android Studio and wait for Gradle sync to finish
2. Select a device and press **Run**

No API keys, accounts or backend setup are needed.

### Try the reminders
1. Allow notifications and exact alarms when the app asks
2. Add a medicine and set its time two minutes ahead
3. Lock the phone and wait for the alarm

On some phones (Samsung, Xiaomi, Oppo, Vivo) set the app's battery usage to **Unrestricted** so background alarms are not delayed.

## Permissions

| Permission | Why it is needed |
|---|---|
| `POST_NOTIFICATIONS` | Show medicine reminders and low-stock alerts |
| `SCHEDULE_EXACT_ALARM` | Ring at the exact scheduled time |
| `RECEIVE_BOOT_COMPLETED` | Restore alarms after a restart |
| `USE_FULL_SCREEN_INTENT` | Show the alarm screen over the lock screen |
| `VIBRATE` | Vibration with alarms |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Save your current location in your profile (only when you tap the button) |
| `CALL_PHONE` | One-tap emergency calls (falls back to the dialer if denied) |

## Roadmap

- [x] Reminders, alarms, courses and stock tracking
- [x] History, adherence and WhatsApp report sharing
- [x] Medical records with photos
- [x] Profile, allergy list and emergency contacts
- [x] Accessibility settings, dark mode and three languages
- [ ] **AI lab report analysis**: read a report photo (OCR) and explain the results in simple language, with a clear safety disclaimer
- [ ] Add and manage custom emergency contacts
- [ ] Allergy warning when adding a medicine
- [ ] Patient details and allergies in the shared report
- [ ] Flashlight and vibration alerts, spoken reminders (text to speech)
- [ ] Backup and restore
- [ ] Optional caregiver sharing

## Known limitations

- Data lives only on the phone, so there is no cloud backup yet
- Alarm reliability can still depend on manufacturer battery optimisation
- Records support photos only (no PDF import)
- Sinhala and Tamil translations are drafts and may need review by native speakers

## Disclaimer















