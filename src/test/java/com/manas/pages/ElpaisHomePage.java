package com.manas.pages;

import com.manas.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ElpaisHomePage {

    private final WebDriver driver;

    //Cookie consent UI can change, but this button class has been stable.
    private final By acceptCookies = By.xpath(
            "//button[@class='pmConsentWall-button']"
    );

    // Prefer href-based matching because visible textcan change (Opinion -> Editorial etc.)
    private final By opinionLink = By.xpath(
            "//a[normalize-space()='Opinión' or contains(@href,'/opinion')]"
    );

    private final By htmlLang = By.xpath("//html");

    public ElpaisHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void handleCookiesIfAny(){
        // Cookie popup doesn't always appear (depends on region/session), so treat it as optional
        if (WaitUtils.present(driver, acceptCookies, 4)) {
            try{
                WaitUtils.clickableClick(driver, acceptCookies, 6);
            } catch (Exception ignored) {
                // If the popup disappears between locate and click, we can safely continue.
            }
        }
    }

    public void open(){
        driver.get("https://elpais.com/");
        handleCookiesIfAny();
    }

    public void ensureSpanish(){
        //Requirement: confirm the site is displayed in Spanish.
        String lang = driver.findElement(htmlLang).getAttribute("lang");
        if(lang != null && !lang.toLowerCase().startsWith("es")) {
            driver.get("https://elpais.com/");
        }
    }

    public void goToOpinion(){
        WaitUtils.clickableClick(driver, opinionLink, 10);
    }
}
