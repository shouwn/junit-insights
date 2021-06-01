package de.adesso.junitinsights.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents a test method invocation.
 * Used by TestClass
 * @see TestClass
 */
@Serializable
data class TestMethod(
    var name: String,
    var firstTimestamp: Long,
    @Transient var timestampBefore: Long = 0L,
    @Transient var timestampAfter: Long = 0L,
    var before: Long,
    var exec: Long,
    var after: Long,
    var successful: Boolean
)