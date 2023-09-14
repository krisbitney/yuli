import io.github.krisbitney.yuli.api.SocialApiFactory
import io.github.krisbitney.yuli.api.requestDelay
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration

class ApiTest {
    private val api = SocialApiFactory.get("./src/commonTest/kotlin/generated/")
    private val username = "kris_makes_apps"
    private val password = "her3jct4tzd5kjq_GPC"

    private val krisUser = User(
        username = "kris_makes_apps",
        name = "Kris B",
        picUrl = "",
    )

    private val yuliaProfile = Profile(
        username = "automagically__now",
        name = "YЮлия Кейс",
        follower = true,
        following = true,
    )

    @Test
    fun testApi() = runTest(timeout = Duration.parse("60s")) {
        println("ApiTest: testing Login...")
        testLogin()
        delay(requestDelay)

        println("ApiTest: testing FetchUser...")
        testFetchUser()
        delay(requestDelay)

        println("ApiTest: testing RestoreSession...")
        testRestoreSession()
        delay(requestDelay)

        println("ApiTest: testing FetchFollowers...")
        testFetchFollowers()
        delay(requestDelay)

        println("ApiTest: testing FetchFollowings...")
        testFetchFollowings()
    }

    fun testLogin() = runTest {
        val result = api.login(username, password)
        assertNull(result.exceptionOrNull())
    }

    fun testRestoreSession() = runTest {
        val result = api.restoreSession(username)
        assertNull(result.exceptionOrNull())
        assertTrue(result.getOrThrow())
    }

    // TODO: followerCount and followingCount apparently are not available
    fun testFetchUser() = runTest {
        val result = api.fetchUser()
        assertNull(result.exceptionOrNull())
        val received = result.getOrThrow()
        assertEquals(krisUser.username, received.username)
        assertEquals(krisUser.name, received.name)
    }

    fun testFetchFollowers() = runTest(timeout = Duration.parse("30s")) {
        val result = api.fetchFollowers(requestDelay)
        assertNull(result.exceptionOrNull())
        val profiles = result.getOrThrow()
        assertTrue(profiles.isNotEmpty())
        assertNotNull(profiles.find { it.username == yuliaProfile.username })
    }

    fun testFetchFollowings() = runTest(timeout = Duration.parse("30s")) {
        val result = api.fetchFollowings(requestDelay)
        assertNull(result.exceptionOrNull())
        val profiles = result.getOrThrow()
        assertTrue(profiles.isNotEmpty())
        assertNotNull(profiles.find { it.username == yuliaProfile.username })
    }
}