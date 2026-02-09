package com.manas.pages;

import com.manas.utils.WaitUtils;
import org.openqa.selenium.*;

public class ArticlePage {
    private final WebDriver driver;

    // On The Articles Page, the real headline is usually <h1 class="a_t">...</h1>
    private final By title = By.cssSelector("h1.a_t");

    //Keeping content broad is intentional - the exact paragraph structure can vary
    private final By content = By.cssSelector("article");

    //Cover image is typically within the article
    private final By coverImg = By.cssSelector("article img");

    public ArticlePage(WebDriver driver){
        this.driver = driver;
    }

    public void open(String url){
        driver.get(url);
        WaitUtils.visible(driver, By.cssSelector("body"), 10);
    }

    public String getTitle(){
        return WaitUtils.visible(driver, title, 10).getText().trim();
    }

    public String getContentText() {
        //Some articles can have partial content or dynamic loading, so i return "" instead of failing the whole run
        try{
            return WaitUtils.visible(driver, content, 10).getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public String getCoverImageUrlIfAny() {
        try {
            WebElement img = driver.findElements(coverImg).stream().findFirst().orElse(null);
            if (img == null) return null;

            //Many sites have lazy-load images - scrolling helps ensure src/data-src is filled.
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", img);

            String src = img.getAttribute("src");
            if (src == null || src.isBlank()) src = img.getAttribute("data-src");
            return (src == null || src.isBlank()) ? null : src;
        } catch (Exception e) {
            return null;
        }
    }
}
