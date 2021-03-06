package org.testng;

import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes;
import org.testng.internal.IResultListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: anna
 * Date: 5/22/13
 */
public class IDEATestNGRemoteListener implements ISuiteListener, IResultListener{

  private String myCurrentClassName;

  public void onConfigurationSuccess(ITestResult itr) {
    //won't be called
  }

  public void onConfigurationFailure(ITestResult itr) {
    //won't be called
  }

  public void onConfigurationSkip(ITestResult itr) {
    //won't be called
  }

  public void onStart(ISuite suite) {
    System.out.println("##teamcity[enteredTheMatrix]");
    System.out.println("##teamcity[testSuiteStarted name =\'" + suite.getName() + "\']");
  }

  public void onFinish(ISuite suite) {
    System.out.println("##teamcity[testSuiteFinished name=\'" + suite.getName() + "\']");
  }

  public void onTestStart(ITestResult result) {
    final String className = result.getTestClass().getName();
    if (myCurrentClassName == null || !myCurrentClassName.equals(className)) {
      if (myCurrentClassName != null) {
        System.out.println("##teamcity[testSuiteFinished name=\'" + myCurrentClassName + "\']");
      }
      System.out.println("##teamcity[testSuiteStarted name =\'" + className + "\']");
      myCurrentClassName = className;
    }
    String methodName = getMethodName(result);
    System.out.println("##teamcity[testStarted name=\'" +
                       methodName + "\' locationHint=\'java:test://" + className + "." + methodName + "\']");
  }

  private static String getMethodName(ITestResult result) {
    String methodName = result.getMethod().getMethodName();
    final Object[] parameters = result.getParameters();
    if (parameters.length > 0) {
      methodName += "[" + parameters[0].toString() + "]";
    }
    return methodName;
  }

  public void onTestSuccess(ITestResult result) {
    System.out.println("##teamcity[testFinished name=\'" + getMethodName(result) + "\']");
  }

  public String getTrace(Throwable tr) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    tr.printStackTrace(writer);
    StringBuffer buffer = stringWriter.getBuffer();
    return buffer.toString();
  }

  public void onTestFailure(ITestResult result) {
    final Throwable ex = result.getThrowable();
    final String trace = getTrace(ex);
    final Map<String, String> attrs = new HashMap<String, String>();
    attrs.put("name", getMethodName(result));
    final String failureMessage = ex.getMessage();
    attrs.put("message", failureMessage != null ? failureMessage : "");
    attrs.put("details", trace);
    attrs.put("error", "true");
    System.out.println(ServiceMessage.asString(ServiceMessageTypes.TEST_FAILED, attrs));
    System.out.println("##teamcity[testFinished name=\'" + getMethodName(result) + "\']");
  }

  public void onTestSkipped(ITestResult result) {
    System.out.println("##teamcity[testFinished name=\'" + getMethodName(result) + "\']");
  }

  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

  }

  public void onStart(ITestContext context) {
    //System.out.println("##teamcity[testSuiteStarted name =\'" + context.getName() + "\']");
  }

  public void onFinish(ITestContext context) {
    if (myCurrentClassName != null) {
      System.out.println("##teamcity[testSuiteFinished name=\'" + myCurrentClassName + "\']");
    }
    //System.out.println("##teamcity[testSuiteFinished name=\'" + context.getName() + "\']");
  }
}
