package io.github.krisbitney.yuli.repository

import io.github.krisbitney.yuli.database.SocialDatabase
import io.github.krisbitney.yuli.models.Event
import io.github.krisbitney.yuli.models.Profile
import io.github.krisbitney.yuli.models.User
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class Repository(val db: SocialDatabase) {

    fun getUser(): User = db.userQueries.select().executeAsOne().toUser()

    fun getFans(): List<Profile> = db.profileQueries
        .selectFans()
        .executeAsList()
        .map { it.toProfile() }

    fun getMutuals(): List<Profile> = db.profileQueries
        .selectMutuals()
        .executeAsList()
        .map { it.toProfile() }

    fun getNonfollowers(): List<Profile> = db.profileQueries
        .selectNonfollowers()
        .executeAsList()
        .map { it.toProfile() }

    fun getEvents(): List<Event> = db.eventQueries
        .selectEvents(0)
        .executeAsList()
        .map { it.toEvent() }

    // TODO: get events for a specific day or time period
    fun getTodayEvents(): List<Event> = db.eventQueries
        .selectEvents(beginningOfTodayUnixTimestamp())
        .executeAsList()
        .map { it.toEvent() }

    private fun beginningOfTodayUnixTimestamp(): Long {
        val now = Clock.System.now()
        val localToday = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = LocalDateTime(localToday, LocalTime(0, 0))
        return startOfDay.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    }
}