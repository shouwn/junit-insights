package de.adesso.junitinsights.tools

import de.adesso.junitinsights.model.Event
import de.adesso.junitinsights.model.Report
import de.adesso.junitinsights.model.TestClass
import de.adesso.junitinsights.model.TestMethod
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

object ReportCreator : IReportCreator {

    /**
     * Takes a list of events and turns them into a full Report object
     * @param events The full event list for multiple test classes
     * @return The report object which contains all the interesting information about the test execution
     */
    override fun createReport(events: List<Event>): Report {
        val eventsGroupedByClass = groupEventsByClass(events)
        val testClasses = eventsGroupedByClass.map { classEvents -> processClassEvents(classEvents) }
        val springContextCreated = testClasses.fold(0) { acc, e -> acc + e.springContextCount }
        val currentDate = ZonedDateTime.now()
        return Report(getReportPageTitle(currentDate), currentDate, springContextCreated, testClasses)
    }

    /**
     * Takes a list of events for a class and turns them into into a TestClass object.
     * This also includes the processing of TestMethod objects.
     */
    private fun processClassEvents(events: List<Event>): TestClass {
        var beforeAll = 0L
        var spring = 0L
        var afterAll = 0L
        var contextCount = 0

        val eventsGroupedByMethods = groupEventsByMethod(events)
        val methods = eventsGroupedByMethods.map { methodEvents -> processMethodEvents(methodEvents) }

        for (i in 1 until events.size) {
            when {
                events[i].name == "after all" -> afterAll += events[i].timeStamp.time - events[i - 1].timeStamp.time
                events[i].name == "before each" -> {
                    if (events[i - 1].name == "context refreshed" || events[i - 1].name == "before all")
                        beforeAll += events[i].timeStamp.time - events[i - 1].timeStamp.time
                }
                events[i].name == "context refreshed" -> {
                    spring += events[i].timeStamp.time - events[i - 1].timeStamp.time
                    contextCount++
                }
            }
        }

        val before = methods.asSequence().map { method -> method.before }.sum()
        val exec = methods.asSequence().map { method -> method.exec }.sum()
        val after = methods.asSequence().map { method -> method.after }.sum()

        val between = events.last().timeStamp.time - events.first().timeStamp.time - beforeAll - spring - afterAll - before - exec - after

        return TestClass(events.last().className, events[0].timeStamp.time, methods, beforeAll, before, exec, after, afterAll, between, spring, contextCount)
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
        var firstTimestamp = events[0].timeStamp.time

        if (events[0].name != "before each")
            firstTimestamp = events[1].timeStamp.time

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
                firstTimestamp,
                beforeEach,
                afterEach,
                beforeTestExecution - beforeEach,
                afterTestExecution - beforeTestExecution,
                afterEach - afterTestExecution,
                events.last().successful
        )
    }

    private fun groupEventsByClass(events: List<Event>): List<List<Event>> {
        val eventGroups = groupEventsAfterKeyword(events, "after all")

        if (eventGroups.isEmpty())
            return eventGroups

        val trimmedEventGroups = ArrayList<ArrayList<Event>>()

        trimmedEventGroups.add(ArrayList(eventGroups[0]))
        for (i in (1 until eventGroups.size)) {
            if (getClassNameFromGroup(trimmedEventGroups.last()) == getClassNameFromGroup(eventGroups[i]))
                trimmedEventGroups.last().addAll(eventGroups[i])
            else
                trimmedEventGroups.add(ArrayList(eventGroups[i]))
        }

        return trimmedEventGroups
    }

    private fun getClassNameFromGroup(eventGroup: List<Event>): String {
        for (event in eventGroup) {
            if (event.className != "")
                return event.className.split("$")[0]
        }
        return ""
    }

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

    private fun getReportPageTitle(currentDate: ZonedDateTime): String {
        val currentLocalDateTime = currentDate.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        val titleDatePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        return "JUnit Insights Report ${currentLocalDateTime.format(titleDatePattern)}"
    }
}
