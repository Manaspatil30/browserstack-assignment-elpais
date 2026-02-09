package com.manas.pages;

import com.manas.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class OpinionPage {
    private final WebDriver driver;

    // On the Opinion page, article headlines are in: article -> h2 -> a
    // This avoids grabbing navigation links or the page header
    private final By articleLinks = By.cssSelector("article h2.c_t a[href*='/opinion/']");

    public OpinionPage(WebDriver driver) {
        this.driver = driver;
    }

    public List<String> getFirstFiveArticleUrls(){
        WaitUtils.visible(driver, By.cssSelector("body"), 10);
        List<WebElement> links = driver.findElements(articleLinks);

        return links.stream()
                .map(e -> e.getAttribute("href"))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }
}
