package io.github.krisbitney.yuli

import androidx.compose.runtime.Composable
import io.github.krisbitney.yuli.state.YuliRoot
import io.github.krisbitney.yuli.ui.RootContent
import io.github.krisbitney.yuli.ui.theme.YuliTheme

@Composable
fun App(component: YuliRoot) {
    YuliTheme {
        RootContent(component)
    }
}

// TODO: error message localization
// TODO: notification UI
// TODO: setting: clear former follows
// TODO: setting: dark mode
// TODO: setting: data update interval
// TODO: setting: use default portrait instead of own pic, or allow upload pic

// TODO: Swiftagram errors are in form "[message] /n (Swiftagram.Authenticator.Error error 6.)"

// An `enum` listing some authentication-specific errors.
//enum Error: Swift.Error {
//    /// Generic error.
//    case generic(String)
//    /// Invalid cookies.
//    case invalidCookies([HTTPCookie])
//    /// Invalid password.
//    case invalidPassword
//    /// Invalid response.
//    case invalidResponse(URLResponse)
//    /// Invalid URL.
//    case invalidURL
//    /// Invalid username.
//    case invalidUsername
//    /// Two factor authentication challenge.
//    case twoFactorChallenge(TwoFactor)
//}