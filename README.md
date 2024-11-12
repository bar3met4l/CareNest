
# CareNest

CareNest is a mobile application designed to help parents and caregivers manage and track essential activities and details for their children. With features like scheduling, baby details management, and custom notifications, CareNest aims to simplify and organize child care routines.

## Features

- **Baby Management**:
  - Add, view, and update essential details for each baby, including:
    - Name
    - Gender
    - Date of Birth (DOB)
    - Health Note
    - Emergency Contact
    - Picture
- **Scheduling**:
  - Schedule activities such as nap time, meal time, play time, etc.
  - View all scheduled activities for a baby in a structured list.
  - Set notifications to remind caregivers of scheduled activities.
- **Notifications**:
  - Custom notifications with reminders for scheduled activities, including a custom sound and message.

## Technologies Used

- **Kotlin**: Used for building the app logic and functionality.
- **Android Room Database**: For efficient local data storage and management.
- **Material Design Components**: For a modern and user-friendly interface.
- **LiveData & Lifecycle Components**: Ensuring UI updates in real-time when data changes.
- **Coroutines**: For asynchronous database operations.
- **AlarmManager and NotificationManager**: For scheduling and triggering notifications.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/CareNest.git
   ```
2. Open the project in **Android Studio**.
3. Build and run the project on your Android device or emulator.

## Usage

1. **Adding a Baby**:
   - Tap the Floating Action Button (FAB) on the main screen.
   - Enter the baby's details and save.
2. **Scheduling Activities**:
   - Tap on a baby's card to enter the schedule screen.
   - Tap the FAB to add a new schedule for the selected baby.
   - Set a time and activity, and save. Notifications will be triggered at the scheduled time.
3. **Viewing and Managing Data**:
   - View all saved baby details in a list.
   - Long-tap to edit or delete entries as needed.

## Future Scope

- Multi-user support with user authentication.
- Cloud backup and data synchronization.
- Health monitoring features such as tracking weight and vaccination schedules.
- Multi-language support for global accessibility.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any new features, bug fixes, or improvements.

## License

This project is licensed under the [MIT License](LICENSE).


