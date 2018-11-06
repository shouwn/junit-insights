package de.adesso.junitinsights.tools

import de.adesso.junitinsights.model.Event
import de.adesso.junitinsights.model.Report
import de.adesso.junitinsights.model.TestClass
import de.adesso.junitinsights.model.TestMethod
import java.util.*
import kotlin.collections.ArrayList

object ReportCreator {

    /**
     * Takes a list of events and turns them into a full Report object
     * @param reportName The name of the report which is included in the Report object
     * @param events The full event list for multiple test classes
     */
    fun createReport(reportName: String, events: List<Event>): Report {
        val eventsGroupedByClass = groupEventsByClass(events)
        val testClasses = eventsGroupedByClass.map { classEvents -> processClassEvents(classEvents) }
        val springContextCreated = countCreatedSpringContexts(events)
        return Report(reportName, Date(), springContextCreated, testClasses)
    }

    /**
     * Takes a list of events for a class and turns them into into a TestClass object.
     * This also includes the processing of TestMethod objects.
     */
    private fun processClassEvents(events: List<Event>): TestClass {
        val beforeAll = events[0].timeStamp.time
        val spring = if (events[1].name == "context refreshed") events[1].timeStamp.time else events[0].timeStamp.time
        val afterAll = events.last().timeStamp.time
        val eventsGroupedByMethods = groupEventsByMethod(events)
        val methods = eventsGroupedByMethods.map { methodEvents -> processMethodEvents(methodEvents) }

        if (methods.isEmpty())
            return TestClass(events.last().className, events[0].timeStamp.time, methods, 0, 0, 0, 0, 0, 0, 0)

        var between = 0L
        for (i in 1..methods.lastIndex)
            between += methods[i].timestampBefore - methods[i - 1].timestampAfter

        return TestClass(
                events.last().className,
                events[0].timeStamp.time,
                methods,
                methods[0].timestampBefore - spring,
                methods.asSequence().map { method -> method.before }.sum(),
                methods.asSequence().map { method -> method.exec }.sum(),
                methods.asSequence().map { method -> method.after }.sum(),
                afterAll - methods.last().timestampAfter,
                between,
                spring - beforeAll
        )
    }

    /**
     * Takes a list of events for a single test method call and turns them into a TestMethod object.
     * The time-spans for each test are calculated in this method.
     */
    private fun processMethodEvents(events: List<Event>): TestMethod {
        var beforeEach = 0L
        var beforeTestExecution = 0L
        var afterEach = 0L
        var afterTestExecution = 0L
        for (event in events) {
            when (event.name) {
                "before each" -> beforeEach = event.timeStamp.time
                "before test execution" -> beforeTestExecution = event.timeStamp.time
                "after test execution" -> afterTestExecution = event.timeStamp.time
                "after each" -> afterEach = event.timeStamp.time
            }
        }
        return TestMethod(
                events.last().methodName,
                events[0].timeStamp.time,
                beforeEach,
                afterEach,
                beforeTestExecution - beforeEach,
                afterTestExecution - beforeTestExecution,
                afterEach - afterTestExecution,
                events.last().successful
        )
    }

    private fun groupEventsByClass(events: List<Event>) = groupEventsAfterKeyword(events, "after all")
    private fun groupEventsByMethod(events: List<Event>) = groupEventsAfterKeyword(events, "after each")

    private fun groupEventsAfterKeyword(events: List<Event>, keyword: String): List<List<Event>> {
        val result = ArrayList<List<Event>>()
        var currentEventGroup = ArrayList<Event>()
        for (event in events) {
            currentEventGroup.add(event)
            if (event.name == keyword) {
                result.add(currentEventGroup)
                currentEventGroup = ArrayList()
            }
        }
        return result
    }

    private fun countCreatedSpringContexts(events: List<Event>): Int {
        return events
                .filter { it.name == "context created" || it.name == "context refreshed" }
                .size
    }
}