package com.manas.drivers;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> TL = new ThreadLocal<>();

    public static WebDriver getDriver(){
        return TL.get();
    }

    public static void createDriver() throws Exception {
//        String runOn = System.getProperty("runOn", "bs");
        String headless = System.getProperty("headless", "false");

        //Local Execution
            ChromeOptions options = new ChromeOptions();
            if(headless.equalsIgnoreCase("true")){
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1400,900");
            TL.set(new ChromeDriver(options));
    }

    public static void quitDriver(){
        if (TL.get() != null){
            TL.get().quit();
            TL.remove();
        }
    }
}
