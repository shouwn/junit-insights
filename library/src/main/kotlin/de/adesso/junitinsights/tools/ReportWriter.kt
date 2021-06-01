package de.adesso.junitinsights.tools

import de.adesso.junitinsights.model.Report
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ReportWriter : IReportWriter {

    /**
     * Takes a Report object, inserts the date as JSON in a template file and writes it to disk under a specific file name.
     * @param report The Report object containing all the information
     */
    override fun writeReport(report: Report) {
        // If JUnit Insights is disabled, the report should not be created
        if (!InsightProperties.enabled)
            return
        val json = generateJsonFromReport(report)
        val html = insertJsonInTemplate(json)
        val reportFile = writeHtmlToFile(html, InsightProperties.reportpath, getReportFileName(report))
        LoggerFactory.getLogger(this::class.java).debug("Report created at ${reportFile.absolutePath}")
    }

    private fun generateJsonFromReport(report: Report): String = Json.encodeToString(report)

    private fun insertJsonInTemplate(json: String): String {
        val template = InputStreamReader(this.javaClass.getResourceAsStream("/index.html")!!).readText()
        return template.replace("var OVERRIDE_REPORT = {}", "var OVERRIDE_REPORT = $json")
    }

    private fun writeHtmlToFile(html: String, path: String, filename: String): File {
        val htmlReportFile = File("$path$filename")
        if (htmlReportFile.parentFile != null)
            htmlReportFile.parentFile.mkdirs()
        PrintWriter(htmlReportFile).use { it.write(html) }
        return htmlReportFile
    }

    private fun getReportFileName(report: Report): String {
        val filenameDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        val currentLocalDateTime = report.created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        return "JUnit_Insights_${currentLocalDateTime.format(filenameDatePattern)}.html"
    }
}
