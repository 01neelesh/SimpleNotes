
# SimpleNotes App 📝
A modern Android application built with Google Sign-In, SQLite (Room), Fragments, and RecyclerView to help users create, manage, and organize notes. This project demonstrates essential concepts like user authentication, database management, and dynamic user interface handling with fragments.

## Table of Contents
- [ Features ](#desc)
- [ Technologies ](#usage)
- [ Setup & Installation ](#setupIntallation)
- [App Walkthrough](#appWalkthrough)
    - [Login Screen   ](#login_screen)
    - [Notes Management. ](#notesMgmt)
- [ScreenShots](#screenShots)
- [Contributing ](#contributing)
- [License  ](#license)

<a name="desc"></a>
## Features 🌟
- **Google Sign-In:** Authenticate users using Google accounts to ensure secure and simple login.
- __Note Management:__ Users can: 
  * Create new notes.
  * Update existing notes.
  * Delete notes.
  * * View all notes in a scrollable list.

- **SQLite with Room:**  Persistent storage of notes using Room, an abstraction over SQLite.
- **RecyclerView Integration:** Dynamically display notes in a list using a RecyclerView for a smooth user experience.
- **Single Activity Architecture:** The entire app operates within a single activity using fragments for various screens.

<a name="setupIntallation"></a>
## Technologies 🛠️
- **Language:** Java
- **Authentication:** Google Sign-In API
- **Database:** SQLite with Room Persistence Library
- **UI Components:** RecyclerView, Fragments, Single Activity Pattern
- **Architecture:** MVVM (Model-View-ViewModel) for better code organization
- **Dependency Management:** Gradle

<a name="setupIntallation"></a>
## Getting Started 🚀

  To Run this project locally, follow the steps below.
  
 ### Prerequisites
 - Android Studio (latest version recommended)
 - A valid OAuth 2.0 Client ID for Google Sign-In (from Google Cloud Console)
- A physical device or Android emulator with Google Play Services enabled

<a name="setupIntallation"></a>
## Setup & Installation
1. ### Clone the Repository:

```bash
git clone https://github.com/01neelesh/SimpleNotes.git
cd SimpleNotesApp
```
2. ### Configure Google Sign-In:
- Download the `google-services.json` file from Firebase or Google Cloud Console and place it in the `app/` directory.
- Ensure you have added your OAuth 2.0 Client ID in the `strings.xml` file

```bash
<string name="default_web_client_id">YOUR_CLIENT_ID_HERE</string>
```

3. ### Build & Run
- Open the project in Android Studio.
- Sync the project with Gradle files.
- Build and run the app on an emulator or physical device.

<a name="appWalkthrough"></a>
## App Walkthrough 📲


<a name="login_screen"></a>
### Login Screen
The app begins with a **Google Sign-In** screen. If the user is not logged in, they are prompted to log in using their Google account. Upon successful login, the app stores the user's email locally using SharedPreferences, enabling seamless authentication across sessions.

<a name="notesMgmt"></a>
### Notes Management
After logging in, users are directed to the Notes Management screen where they can view all of their notes in a **RecyclerView**.

- **Add Note:** Users can create a new note by tapping the "Add" button. A dialog box appears where they can input the note's content.
- **Update Note:** Users can select any note and update its content.
- **Delete Note:** Each note has a delete option, allowing the user to remove notes permanently.
The app uses **SQLite** for local storage, implemented with **Room**, ensuring that all notes persist between sessions.

<a name="screenShots"></a>
## Screenshots 📸
![App Screenshot](https://i.pinimg.com/originals/bb/f1/91/bbf191f07c3d9815f28c0dd8d69c2514.jpg)

<a name="contributing"></a>
## Contributing 🤝

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. If you'd like to contribute to this project, feel free to create a pull request or submit an issue!

### Steps to contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make changes and commit (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

<a name="license"></a>
## License 📄

[MIT](https://choosealicense.com/licenses/mit/)

