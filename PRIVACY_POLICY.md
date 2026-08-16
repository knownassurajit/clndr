# Privacy Policy for CLNDR

**Developer:** Surajit Das  
**App Name:** CLNDR  
**Package ID:** `com.knownassurajit.clndr_widget.app`  
**Effective Date:** August 13, 2026  
**Last Updated:** August 16, 2026

## 1. Overview

CLNDR is a minimal, monochrome, widget-first Android calendar focused on life-grid, year progress, and milestones. It is designed to run locally on the user's device. This Privacy Policy explains how information is handled when you use the Application.

The short version: CLNDR does not collect, store, transmit, or share personal data with us or any third-party servers.

## 2. Information Collection and Storage

- **Local Data Storage:** Birth date, theme, calendar events, milestones, life-grid state, and year-progress configuration are stored **locally on your device** (DataStore and Room).
- **No Personal Data Collection:** We do not collect, transmit, store, or process personally identifiable information on our servers. We operate no backend for this app.
- **No Cloud Synchronization:** The Application does not sync data to external servers or cloud services.
- **Sunrise/Sunset Formula:** Automated day/night theme transitions use an offline mathematical formula (NOAA simplified calculation) that does **not** collect or access your location or location permissions.

## 3. Device Permissions

The Application requests only the permissions required for optional, user-initiated features:

- **`POST_NOTIFICATIONS`**: Local milestone reminder notifications.
- **`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`**: Fire those reminders at the time you choose.
- **`READ_CALENDAR` / `WRITE_CALENDAR`**: Optional mirroring of a milestone into the device calendar. Events stay on-device; nothing is sent to us.
- **`SET_ALARM`**: Optional hand-off to the system Clock app to create an alarm for a milestone. The Clock app is a separate system application.
- **`RECEIVE_BOOT_COMPLETED`**: Re-schedule local reminders after reboot.

Calendar, Clock, and notification features are off until you enable them on a milestone.

## 4. Third-Party Services & Analytics

- **No Third-Party SDKs:** The Application contains no third-party tracking, analytics, crash reporting, or advertising SDKs.
- **No Ads:** The Application is ad-free.

## 5. Security

Your data stays on your device. Security depends on your device-level protections (PIN, passphrase, biometrics). Uninstalling the Application removes locally stored database entries.

## 6. Children's Privacy

The Application does not address or collect data from anyone under the age of 13.

## 7. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Changes will be reflected in this document in the repository.

## 8. Contact Us

If you have questions about this Privacy Policy, contact:

- **Developer:** Surajit Das
- **Email:** surajit@duck.com
- **GitHub Repository:** [https://github.com/knownassurajit/clndr](https://github.com/knownassurajit/clndr)
