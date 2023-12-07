# Yuli

Yuli is a mobile app for Android and iOS that helps users identify who unfollowed them on Instagram. My wife was having trouble finding a secure app that would tell her who unfollowed her without compromising her Instagram account. So I built one.

- All data is stored locally on the user's device. 
- Locally stored login data is encrypted.
- User data is not transmitted to any third party--it stays between the user and Instagram.
- No ads, no tracking, no analytics.

The app is built with Kotlin Multiplatform Mobile and Jetbrains Compose Multiplatform.

Nearly the entire app is written in Kotlin, with the one exception of an API implementation written in Swift: https://github.com/krisbitney/yuli-ios-api. Native iOS APIs are generally accessed through Kotlin Multiplatform Mobile.

# Architecture
The app uses an MVI architecture with unidirectional data flow. Events flow from the UI downward, and state flows upward from the data layer. The UI is updated based on the current state.

```plaintext
API --------|
            |
            V
            Repository -----> State -----> UI
            ^
            |
DB ---------|
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

# Design
Yuli's design is based on an image generated with ChatGPT DALLE 3.

<img width="200" src="https://github.com/krisbitney/yuli/assets/39842820/32180c10-7391-4632-bbce-c8f151e5f1d8">

# Screenshots
<img src="https://github.com/krisbitney/yuli/assets/39842820/1605792b-3327-41db-9a7b-f9ee7259b062" width="200"/>  
<img src="https://github.com/krisbitney/yuli/assets/39842820/8e0658a9-4f26-45ac-9e9f-affb52dff77b" width="200"/>  
<img src="https://github.com/krisbitney/yuli/assets/39842820/33a991f1-da9e-4087-87fc-fac1ddcfabab" width="200"/>  
<img src="https://github.com/krisbitney/yuli/assets/39842820/c35d36e8-bb0c-4727-97ec-625255316770" width="200"/>  
<img src="https://github.com/krisbitney/yuli/assets/39842820/f58f1ae8-a3ba-488f-8e97-882da6194e05" width="200"/>  
<img src="https://github.com/krisbitney/yuli/assets/39842820/928c36b4-182b-4284-b07a-d6d5411e9499" width="200"/>

