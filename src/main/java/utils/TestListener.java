package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========================================");
        System.out.println("Starting Test Suite: " + context.getName());
        System.out.println("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n Running: " + result.getMethod().getMethodName());

        // Add test description to Allure
        String description = result.getMethod().getDescription();
        if (description != null && !description.isEmpty()) {
            Allure.description(description);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED: " + result.getMethod().getMethodName());

        // Add success info to Allure
        Allure.step("Test completed successfully");
        addExecutionTime(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("FAILED: " + result.getMethod().getMethodName());
        System.out.println("Reason: " + result.getThrowable().getMessage());

        // Add failure details to Allure
        Allure.step("Test failed: " + result.getThrowable().getMessage(), Status.FAILED);

        // Attach stack trace
        String stackTrace = getStackTrace(result.getThrowable());
        Allure.addAttachment("Stack Trace", "text/plain",
                new ByteArrayInputStream(stackTrace.getBytes(StandardCharsets.UTF_8)), "txt");

        // Attach response if available
        attachResponseIfAvailable(result);

        addExecutionTime(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("SKIPPED: " + result.getMethod().getMethodName());

        // Add skip info to Allure
        if (result.getThrowable() != null) {
            Allure.step("Test skipped: " + result.getThrowable().getMessage(), Status.SKIPPED);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========================================");
        System.out.println("Test Results:");
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
        System.out.println("========================================\n");

        // Add summary to Allure
        String summary = String.format(
                "Total: %d | Passed: %d | Failed: %d | Skipped: %d",
                context.getAllTestMethods().length,
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size()
        );

        Allure.addAttachment("Test Suite Summary", "text/plain",
                new ByteArrayInputStream(summary.getBytes(StandardCharsets.UTF_8)), "txt");
    }

    // Helper: Add execution time to Allure
    private void addExecutionTime(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        Allure.parameter("Execution Time", duration + " ms");
        System.out.println("Duration: " + duration + "ms");
    }

    // Helper: Get stack trace as string
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    // Helper: Attach API response if available
    private void attachResponseIfAvailable(ITestResult result) {
        try {
            Object[] parameters = result.getParameters();
            if (parameters != null && parameters.length > 0) {
                for (Object param : parameters) {
                    if (param instanceof Response) {
                        Response response = (Response) param;

                        // Attach response body
                        String responseBody = response.getBody().asPrettyString();
                        Allure.addAttachment("API Response Body", "application/json",
                                new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)), "json");

                        // Add response details
                        Allure.parameter("Status Code", response.getStatusCode());
                        Allure.parameter("Response Time", response.getTime() + " ms");

                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore if response not available
        }
    }
}