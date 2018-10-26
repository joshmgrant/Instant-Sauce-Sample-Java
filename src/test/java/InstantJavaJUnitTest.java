import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class InstantJavaJUnitTest implements SauceOnDemandSessionIdProvider {

    /**
     * Note: this test makes use of Java 1.8 and requires this version of the JRE/JDK.
     */

    private WebDriver driver;
    private String sessionId;

    /** Here we are reading environment variables from your local machine and storing these
     * values in the variables below. Doing this is a best practice.
     *
     * Not sure how to use env variables, follow this -
     * https://wiki.saucelabs.com/display/DOCS/Best+Practice%3A+Use+Environment+Variables+for+Authentication+Credentials
     */
    private String sauceUserName = System.getenv("SAUCE_USERNAME");
    private String sauceAccessKey = System.getenv("SAUCE_ACCESS_KEY");
    /**
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(sauceUserName, sauceAccessKey);
    /**
     * JUnit Rule which will mark the Sauce Job as passed/failed when the test succeeds or fails.
     */
    @Rule
    public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);
    /**
     * Junit Rule which will return the method name being executed
     */
    @Rule
    public TestName name = new TestName() {
        public String getMethodName() {
            return String.format("%s", super.getMethodName());
        }
    };

    /**
     * @return the value of the Sauce Job id.
     */
    public String getSessionId() {
        return sessionId;
    }

    @Test
    public void shouldOpenSafari() throws MalformedURLException {

        /*
         * In this section, we will configure our test to run on some specific
         * browser/os combination in Sauce Labs
         */
        DesiredCapabilities capabilities = new DesiredCapabilities();
        //set your user name and access key to run tests in Sauce
        capabilities.setCapability("username", sauceUserName);
        //set your sauce labs access key
        capabilities.setCapability("accessKey", sauceAccessKey);
        //set browser to Safari
        capabilities.setCapability("browserName", "Safari");
        //set operating system to macOS version 10.13
        capabilities.setCapability("platform", "macOS 10.13");
        //set the browser version to 11.1
        capabilities.setCapability("version", "11.1");
        //set your test case name so that it shows up in Sauce Labs
        capabilities.setCapability("name", name.getMethodName());

        //create a new Remote driver that will allow your test to send
        //commands to the Sauce Labs grid so that Sauce can execute your tests
        driver = new RemoteWebDriver(new URL("http://ondemand.saucelabs.com:80/wd/hub"), capabilities);

        this.sessionId = ((RemoteWebDriver)driver).getSessionId().toString();

        //navigate to the url of the Sauce Labs Sample app
        driver.navigate().to("https://www.saucedemo.com");
        //Create an instance of a Selenium explicit wait so that we can dynamically wait for an element
        WebDriverWait wait = new WebDriverWait(driver, 5);
        //use the user name field locator as a WebElemet and wait for it to be visible
        WebElement userNameField = driver.findElement(By.cssSelector("[type='text']"));
        wait.until(ExpectedConditions.visibilityOf(userNameField));
        //type the user name string into the user name field
        userNameField.sendKeys("standard_user");
        //type the password into the password field
        driver.findElement(By.cssSelector("[type='password']")).sendKeys("secret_sauce");
        //hit Login button
        driver.findElement(By.cssSelector("[type='submit']")).click();

        //Synchronize on the next page and make sure it loads

        By inventoryPageLocator = By.id("inventory_container");
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryPageLocator));
        //Assert that the inventory page displayed appropriately
        Assert.assertTrue(driver.findElement(inventoryPageLocator).isDisplayed());
    }

    @After
    public void cleanUpAfterTestMethod() {
        driver.quit();
    }
}
