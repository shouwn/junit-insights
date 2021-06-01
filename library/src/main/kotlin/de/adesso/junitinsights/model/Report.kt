package de.adesso.junitinsights.model

import de.adesso.junitinsights.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

/**
 * Top-level data structure for a test report.
 * Contains general information and branches off into specific test classes.
 */
@Serializable
data class Report(
    var reportTitle: String,
    @Serializable(with = KZonedDateTimeSerializer::class) var created: ZonedDateTime,
    var springContextsCreated: Int,
    var testClasses: List<TestClass>
)