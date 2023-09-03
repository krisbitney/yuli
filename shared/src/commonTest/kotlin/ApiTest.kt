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
        picUrl = "https://scontent-sof1-2.cdninstagram.com/v/t51.2885-19/364289591_1500385474063959_7537635027041104620_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent-sof1-2.cdninstagram.com&_nc_cat=103&_nc_ohc=xfpe2SyyeSUAX8xhp4K&edm=AJlpnE4BAAAA&ccb=7-5&oh=00_AfCx03CL6zdFAmyXv5V3XfXRfFmFv7vMAdYkzpPP-OxUWg&oe=64F95A82&_nc_sid=125e1d",
        followerCount = 0,
        followingCount = 0
    )

    private val yuliaProfile = Profile(
        username = "automagically__now",
        name = "YЮлия Кейс",
        picUrl = "https://scontent-sof1-1.cdninstagram.com/v/t51.2885-19/316732727_858613001940097_2723378978418699811_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent-sof1-1.cdninstagram.com&_nc_cat=101&_nc_ohc=VigRNWd3cvYAX93Sx3-&edm=APQMUHMBAAAA&ccb=7-5&oh=00_AfBES4JxcpEQlBsIQQ8oZxXeXZ8OFW0PjKA0o-oA8p5ZgQ&oe=64F9AAE5&_nc_sid=6ff7c8",
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
        val result = api.restoreSession()
        assertNull(result.exceptionOrNull())
        assertTrue(result.getOrThrow())
    }

    // TODO: followerCount and followingCount apparently are not available
    fun testFetchUser() = runTest {
        val result = api.fetchUser()
        assertNull(result.exceptionOrNull())
        assertEquals(krisUser, result.getOrThrow())
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