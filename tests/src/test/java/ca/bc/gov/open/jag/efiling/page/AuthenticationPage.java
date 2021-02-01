package ca.bc.gov.open.jag.efiling.page;

import ca.bc.gov.open.jag.efiling.services.GenerateUrlService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

public class AuthenticationPage extends Base{

//    private final WebDriver driver;
    private WebDriverWait wait;

    Logger log = LogManager.getLogger(AuthenticationPage.class);

    //Page Objects:

    // on TEST
   // @FindBy(id = "user")
    @FindBy(id = "userName")
    WebElement userName;

    @FindBy(id = "password")
    WebElement password;

    @FindBy(xpath = "//input[@type='submit']")
    WebElement continueBtn;

    @FindBy(id = "zocial-bceid")
    WebElement bceidBtn;

    @FindBy(id = "kc-login")
    WebElement signIn;


  /*  //Initializing the driver:
    public AuthenticationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }*/

    private final GenerateUrlService generateUrlService;

    public AuthenticationPage(GenerateUrlService generateUrlService) {

        //  this.eFileSubmissionPage = eFileSubmissionPage;
        this.generateUrlService = generateUrlService;
    }

    //Actions:

    public void goTo() throws IOException {
        String actualGeneratedRedirectUrl = generateUrlService.getGeneratedUrl();

        System.out.println(actualGeneratedRedirectUrl);
        driver.get(actualGeneratedRedirectUrl);

    }
    public void signInWithBceid(String userNm, String pwd) throws InterruptedException {
        /*wait = new WebDriverWait(driver, 90);
        wait.until(ExpectedConditions.titleIs("Sign in to Efiling Hub"));*/
        log.info("Waiting for the page to load...");
        userName.sendKeys(userNm);
        password.sendKeys(pwd);
        Thread.sleep(2000L);
        signIn.click();
    }

    public void clickBceid() {
        wait = new WebDriverWait(driver, 90);
        wait.until(ExpectedConditions.titleIs("Log in to Family Law Act Application"));
        signIn.click();
    }
}
