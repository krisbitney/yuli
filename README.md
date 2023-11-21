# Yuli

Yuli is a mobile app for Android and iOS that helps users identify who unfollowed them on Instagram. My wife was having trouble finding a secure app that would tell her who unfollowed her without compromising her Instagram account. So I built one for her.

- All data is stored locally on the user's device. 
- Locally stored login data is encrypted.
- User data is not transmitted to any third party--it stays between the user and Instagram.
- No ads, no tracking, no analytics.

The app is built with Kotlin Multiplatform Mobile and Jetbrains Compose Multiplatform.

Nearly the entire app is written in Kotlin, with the one exception of a small API implementation written in Swift: https://github.com/krisbitney/yuli-ios-api. Native iOS APIs are generally accessed through Kotlin Multiplatform Mobile.

# Design
The app uses an MVI architecture with unidirectional data flow. Events flow from the UI downward, and state flows upward from the data layer. The UI is updated based on the current state.

```plaintext
API ----------------|
                    |
                    V
                    Repository -----> State -----> UI
                    ^
                    |
DB -----------------|
```

- **API and DB (Level -2)**
    - The API and DB represent external and internal data sources, respectively.
    - They operate independently but feed data into the same subsequent level.

- **Repository (Level -1)**
    - The Repository acts as an intermediary, processing data from both the API and DB.
    - It is responsible for transforming and consolidating data before passing it to the State.

- **State (Level 0)**
    - The State is the element that holds the current status or data of the application.
    - It is updated by the Repository based on the data received from API and DB.

- **UI (User Interface) (Level 1)**
    - The UI represents how the data and current state are displayed to the user.
    - It is the final layer where the processed and stored data becomes visible and interactive.
