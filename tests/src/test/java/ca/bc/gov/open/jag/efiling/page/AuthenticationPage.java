package ca.bc.gov.open.jag.efiling.page;

import ca.bc.gov.open.jag.efiling.services.GenerateUrlService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;

public class AuthenticationPage extends BasePage {

    private Logger logger = LogManager.getLogger(AuthenticationPage.class);

    @Value("${USERNAME_BCEID:bobross}")
    private String username;

    @Value("${PASSWORD_BCEID:changeme}")
    private String password;

    //Page Objects:
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "kc-login")
    private WebElement signIn;

    private final GenerateUrlService generateUrlService;

    public AuthenticationPage(GenerateUrlService generateUrlService) {
        this.generateUrlService = generateUrlService;
    }

    public void signInWithBceid() {

        wait.until(ExpectedConditions.titleIs("Sign in to Efiling Hub"));
        logger.info("Waiting for the page to load...");
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        signIn.click();

    }

}
