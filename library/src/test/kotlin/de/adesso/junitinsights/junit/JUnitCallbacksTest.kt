package de.adesso.junitinsights.junit

import de.adesso.junitinsights.model.EventLog
import de.adesso.junitinsights.tools.InsightProperties
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*

class JUnitCallbacksTest {

    @Test
    fun beforeAllSetsConfiguration() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        val callbacks = JUnitCallbacks()

        // Act
        callbacks.beforeAll(mockedExtensionContext)

        // Assert
        assertTrue(InsightProperties.configurationSet)
    }

    @Test
    fun beforeAllCreatesEvent() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        val callbacks = JUnitCallbacks()
        InsightProperties.enabled = true

        // Act
        callbacks.beforeAll(mockedExtensionContext)

        // Assert
        assertTrue(EventLog.containsEventWithName("before all"))
    }

    @Test
    fun beforeEachCreatesEvent() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        val callbacks = JUnitCallbacks()
        InsightProperties.enabled = true

        // Act
        callbacks.beforeEach(mockedExtensionContext)

        // Assert
        assertTrue(EventLog.containsEventWithName("before each"))
    }

    @Test
    fun afterEachCreatesEvent() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        val callbacks = JUnitCallbacks()
        InsightProperties.enabled = true

        // Act
        callbacks.afterEach(mockedExtensionContext)

        // Assert
        assertTrue(EventLog.containsEventWithName("after each"))
    }

    @Test
    fun afterAllCreatesEvent() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        val callbacks = JUnitCallbacks()
        InsightProperties.enabled = true

        // Act
        callbacks.afterAll(mockedExtensionContext)

        // Assert
        assertTrue(EventLog.containsEventWithName("after all"))
    }

    @Test
    @Disabled
    fun excludedTestClassesDoNotLog() {
        // Arrange
        val mockedExtensionContext: ExtensionContext = mockedExtensionContext()
        //TODO: mock call to context.element in JUnitCallbacks.shouldNotBeBenched to return element with NoJUnitInsights annotation (might require reflection magic)
        val callbacks = JUnitCallbacks()
        InsightProperties.enabled = true

        // Act
        callbacks.beforeAll(mockedExtensionContext)

        // Assert
        assertEquals(0, EventLog.eventCount())
    }

    private fun mockedExtensionContext(): ExtensionContext = mockk {
        every { testClass } returns Optional.of(javaClass)
        every { element } returns Optional.empty()
        every { testMethod } returns Optional.empty()
        every { getConfigurationParameter("de.adesso.junitinsights.enabled") } returns Optional.of("true")
        every { getConfigurationParameter("de.adesso.junitinsights.reportpath") } returns Optional.empty()
    }
}
