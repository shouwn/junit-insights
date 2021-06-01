package de.adesso.junitinsights.tools

import de.adesso.junitinsights.model.Event
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.*

class ReportCreatorTest {

    @Test
    fun noEventsProduceEmptyReport() {
        val events: ArrayList<Event> = ArrayList()
        val report = ReportCreator.createReport(events)
        assertTrue(report.testClasses.isEmpty())
        assertTrue(report.springContextsCreated == 0)
        assertTrue(report.created == ZonedDateTime.now() || report.created.isBefore(ZonedDateTime.now()))
    }

    @Test
    fun noMethodsInReport() {
        val events: ArrayList<Event> = ArrayList()
        events.add(Event("before all", Date(0), "test-class"))
        events.add(Event("after all", Date(1), "test-class"))
        val report = ReportCreator.createReport(events)
        assertEquals(0, report.testClasses.first().methods.size)
    }

    @Test
    fun singleClassInReport() {
        val events: ArrayList<Event> = ArrayList()
        events.add(Event("before all", Date(0), "test-class"))
        events.add(Event("context refreshed", Date(1)))
        events.add(Event("before each", Date(3), "test-class", "test-method"))
        events.add(Event("before test execution", Date(6), "test-class", "test-method"))
        events.add(Event("after test execution", Date(10), "test-class", "test-method"))
        events.add(Event("after each", Date(15), "test-class", "test-method"))
        events.add(Event("after all", Date(21), "test-class"))
        val report = ReportCreator.createReport(events)

        assertTrue(report.created == ZonedDateTime.now() || report.created.isBefore(ZonedDateTime.now()))
        assertEquals(1, report.testClasses.size)
        assertEquals(1, report.testClasses.first().methods.size)

        assertEquals(1, report.testClasses.first().spring)
        assertEquals(2, report.testClasses.first().beforeAll)
        assertEquals(3, report.testClasses.first().methods.first().before)
        assertEquals(4, report.testClasses.first().methods.first().exec)
        assertEquals(5, report.testClasses.first().methods.first().after)
        assertEquals(6, report.testClasses.first().afterAll)
    }

    @Test
    fun twoMethodsInReport() {
        val events: ArrayList<Event> = ArrayList()
        events.add(Event("before all", Date(0), "test-class"))
        events.add(Event("before each", Date(1), "test-class", "test-method"))
        events.add(Event("before test execution", Date(3), "test-class", "test-method"))
        events.add(Event("after test execution", Date(6), "test-class", "test-method"))
        events.add(Event("after each", Date(10), "test-class", "test-method"))
        events.add(Event("before each", Date(15), "test-class", "another-method", false))
        events.add(Event("before test execution", Date(21), "test-class", "another-method", false))
        events.add(Event("after test execution", Date(28), "test-class", "another-method", false))
        events.add(Event("after each", Date(36), "test-class", "another-method", false))
        events.add(Event("after all", Date(45), "test-class"))
        val report = ReportCreator.createReport(events)

        assertEquals(1, report.testClasses.size)
        assertEquals(2, report.testClasses.first().methods.size)
        assertEquals(0, report.testClasses.first().firstTimestamp)
        assertEquals(1, report.testClasses.first().methods.first().firstTimestamp)
        assertEquals("test-class", report.testClasses.first().name)
        assertEquals("test-method", report.testClasses.first().methods.first().name)
        assertTrue(report.testClasses.first().methods.first().successful)
        assertFalse(report.testClasses.first().methods.last().successful)

        assertEquals(2 + 6, report.testClasses.first().before)
        assertEquals(3 + 7, report.testClasses.first().exec)
        assertEquals(4 + 8, report.testClasses.first().after)
        assertEquals(5, report.testClasses.first().between)

    }

    @Test
    fun reportNameFitsPattern() {
        val events: ArrayList<Event> = ArrayList()
        val report = ReportCreator.createReport(events)
        // "JUnit Insights Report dd.MM.yyyy HH:mm:ss"
        // \d represents a single digit
        val expectedPattern = """JUnit Insights Report \d\d.\d\d.\d\d\d\d \d\d:\d\d:\d\d""".toRegex()
        assert(expectedPattern.matches(report.reportTitle))
    }

    @Test
    fun countsSpringContexts() {
        val events: ArrayList<Event> = ArrayList()
        events.add(Event("before all", Date()))
        events.add(Event("context refreshed", Date()))
        events.add(Event("context refreshed", Date()))
        events.add(Event("after all", Date()))
        val report = ReportCreator.createReport(events)
        assertEquals(2, report.springContextsCreated)
    }
}
