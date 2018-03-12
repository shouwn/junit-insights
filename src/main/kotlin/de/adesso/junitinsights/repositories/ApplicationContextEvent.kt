package de.adesso.junitinsights.repositories

import java.util.*
import javax.persistence.*

@Entity
data class ApplicationContextEvent (@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long = 0,
                                    var timestamp: Date,
                                    var applicationContextEvent: ApplicationContextEventType
) {
    override fun toString(): String {
        return "ApplicationContextEvent(id=$id, timestamp=$timestamp, applicationContextEvent=$applicationContextEvent)"
    }
}