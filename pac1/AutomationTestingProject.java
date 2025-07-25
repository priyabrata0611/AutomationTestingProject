package pac1;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import javax.imageio.ImageIO;

public class AutomationTestingProject {

    WebDriver driver;
    WebDriverWait wait;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        ExtentSparkReporter spark = new ExtentSparkReporter("InspirationReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    public void highlightElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", element);
    }

    public void takeScreenshot(String fileName) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(src.toPath(), Paths.get(fileName));
            test.addScreenCaptureFromPath(fileName);
        } catch (IOException e) {
            test.warning("Could not attach screenshot: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void clickInspirationTabOnly() {
        test = extent.createTest("Click on 'Inspiration' Tab");
        driver.get("https://www.asianpaints.com/");

        WebElement inspirationTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-target='#Inspiration']")));
        highlightElement(inspirationTab);
        inspirationTab.click();
        test.pass("Clicked on 'Inspiration' tab successfully.");

        try {
            Thread.sleep(2000); // small wait
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void testColourCombinationsPage() {
        test = extent.createTest("Verify Colour Combinations Page");
        driver.get("https://www.asianpaints.com/inspiration/ideas/colour-inspiration.html");

        String[] altTexts = {
                "The exterior of a house along with the surrounding landscape. Wall color combinations for home exterior.",
                "A living room with yellow accent wall and TV unit. Wall color combinations for living room.",
                "A bedroom decorated with wooden wall panels and headboard. Wall color combinations for bedroom."
        };

        for (String alt : altTexts) {
            WebElement img = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//img[@alt='" + alt + "']")));
            highlightElement(img);
            Assert.assertTrue(img.isDisplayed(), "Image not visible: " + alt);
            test.pass("Verified image with alt text: " + alt);
        }
    }

    @Test(priority = 3)
    public void testCelebrityHomesPage() {
        test = extent.createTest("Verify Celebrity Homes Page");

        driver.get("https://www.asianpaints.com/where-the-heart-is/season-7.html");

        WebElement exploreBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'explore similar styles')]")));
        highlightElement(exploreBtn);
        Assert.assertTrue(exploreBtn.isDisplayed());
        test.pass("Verified 'Explore Similar Styles' button");

        WebElement videoThumb = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//img[contains(@class,'videocards__thumbnail')]")));
        highlightElement(videoThumb);
        Assert.assertTrue(videoThumb.isDisplayed());
        test.pass("Verified video thumbnail on Celebrity Homes page");
    }

    @Test(priority = 4)
    public void testColourOfTheYearPage() {
        test = extent.createTest("Verify Colour of the Year 2025 Page");

        driver.get("https://www.asianpaints.com/colour-next.html");

        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Colour of the year 2025 – Cardinal (8206)')]")));
        highlightElement(heading);
        Assert.assertTrue(heading.isDisplayed());
        test.pass("Verified heading for Colour of the Year 2025");
    }

    @Test(priority = 5)
    public void testInspirationSubModulesFailing() {
        test = extent.createTest("(Failing) Validate Submodules Under Inspiration Tab");

        driver.get("https://www.asianpaints.com/");

        WebElement inspirationTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@data-target='#Inspiration']")));
        highlightElement(inspirationTab);
        inspirationTab.click();
        test.pass("Clicked on 'Inspiration' tab");

        try {
            WebElement submodule = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[text()='Colour Combinations']")));
            highlightElement(submodule);
            test.pass("Submodule 'Colour Combinations' is visible");
        } catch (Exception e) {
            takeScreenshot("submodule_failure.png");
            test.fail("Submodule 'Colour Combinations' not found or visible");
            Assert.fail("Submodule not found");
        }
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
        extent.flush();
    }
}
