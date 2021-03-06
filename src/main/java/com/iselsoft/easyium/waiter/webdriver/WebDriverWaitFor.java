package com.iselsoft.easyium.waiter.webdriver;

import com.iselsoft.easyium.DynamicElement;
import com.iselsoft.easyium.Element;
import com.iselsoft.easyium.WebDriver;
import com.iselsoft.easyium.WebDriverType;
import com.iselsoft.easyium.exceptions.WebDriverTimeoutException;

public class WebDriverWaitFor {
    protected final WebDriver webDriver;
    protected final long interval;
    protected final long timeout;
    protected boolean desiredOccurrence;

    public WebDriverWaitFor(WebDriver webDriver, long interval, long timeout) {
        this.webDriver = webDriver;
        this.interval = interval;
        this.timeout = timeout;
        this.desiredOccurrence = true;
    }

    protected void waitFor(WebDriverCondition condition, long interval, long timeout) {
        long startTime = System.currentTimeMillis();

        if (condition.occurred() == desiredOccurrence) {
            return;
        }

        while ((System.currentTimeMillis() - startTime) <= timeout) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            if (condition.occurred() == desiredOccurrence) {
                return;
            }
        }

        throw new WebDriverTimeoutException(String.format("Timed out waiting for <%s> to be <%s>.", condition, desiredOccurrence));
    }

    /**
     * Wait for not.
     *
     * @return this WebDriverWaitFor instance
     */
    public WebDriverWaitFor not() {
        desiredOccurrence = !desiredOccurrence;
        return this;
    }

    /**
     * Wait for the alert present.
     */
    public void alertPresent() {
        waitFor(new WebDriverAlertPresentCondition(webDriver), interval, timeout);
    }

    /**
     * Wait for the text present.
     *
     * @param text the text to wait
     */
    public void textPresent(String text) {
        waitFor(new WebDriverTextPresentCondition(webDriver, text), interval, timeout);
    }

    /**
     * Wait for the url equals expected url.
     *
     * @param url the expected url
     */
    public void urlEquals(String url) {
        waitFor(new WebDriverURLEqualsCondition(webDriver, url), interval, timeout);
    }

    /**
     * Wait for the page to be refreshed / redirected.
     * <p>
     * Example: Wait for the page to be refreshed.
     * <pre>
     *   // usually we use body as indicator, the indicator should be a DynamicElement
     *   Element indicator = driver.findElement("tag=body");
     *   // reload after 2 seconds
     *   new StaticElement(driver, "id=reload_after_2_seconds").click();
     *   driver.waitFor().reloaded(indicator);
     * </pre>
     * 
     * @param indicator the indicator element, it should be a {@link DynamicElement}
     */
    public void reloaded(Element indicator) {
        waitFor(new WebDriverReloadedCondition(webDriver, indicator), interval, timeout);
    }

    /**
     * Wait for the activity present.
     * <p><b>Supported by ANDROID</b></p>
     *
     * @param activity the activity to wait
     */
    public void activityPresent(String activity) {
        webDriver.checkSupport(WebDriverType.ANDROID);

        waitFor(new WebDriverActivityPresentCondition(webDriver, activity), interval, timeout);
    }
}
