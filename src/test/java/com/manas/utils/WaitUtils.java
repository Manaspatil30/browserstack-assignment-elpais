package com.manas.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    // Standard explicit wait instead of Thread.sleep for stability.
    public static WebElement visible(WebDriver driver, By by, int seconds){
        return new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    // Wait until clickable, then click in one go - to avoid ElementNotIntractable issues.
    public static void clickableClick(WebDriver driver, By by, int seconds){
        new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.elementToBeClickable(by)).click();
    }


    // Useful for optional elements like cookie banners.
    public static boolean present(WebDriver driver, By by, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (TimeoutException e){
            return false;
        }
    }
}
