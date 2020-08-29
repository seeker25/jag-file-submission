package ca.bc.gov.open.jagefilingapi.qa.frontend.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AuthenticationPage {

    private final WebDriver driver;
    private WebDriverWait wait;

    Logger log = LogManager.getLogger(AuthenticationPage.class);

    //Page Objects:
    @FindBy(id = "user")
    WebElement userName;

    @FindBy(id = "password")
    WebElement password;

    @FindBy(xpath = "//input[@type='submit']")
    WebElement continueBtn;

    @FindBy(xpath = "//*[@id='zocial-bceid']")
    WebElement bceidBtn;

    //Initializing the driver:
    public AuthenticationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    //Actions:
    public void signInWithIdir(String userNm, String pwd) {
        wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.titleIs("Government of British Columbia"));
        log.info("Waiting for the page to load...");
        userName.sendKeys(userNm);
        password.sendKeys(pwd);
        continueBtn.click();
    }

    public void clickBceid() {
        wait = new WebDriverWait(driver, 60);
        wait.until(ExpectedConditions.titleIs("Government of British Columbia"));
        bceidBtn.click();
    }
}
