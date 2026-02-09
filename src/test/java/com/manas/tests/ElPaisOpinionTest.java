package com.manas.tests;

import com.manas.drivers.DriverFactory;
import com.manas.pages.ArticlePage;
import com.manas.pages.ElpaisHomePage;
import com.manas.pages.OpinionPage;
import com.manas.services.TranslationService;
import com.manas.utils.ImageDownloader;
import com.manas.utils.TextAnalyzer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElPaisOpinionTest {

    @BeforeMethod
    public void setup() throws Exception {
            DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        DriverFactory.quitDriver();
    }

    @Test
    public void scrapeTranslateAnalyze() throws Exception {
        var driver = DriverFactory.getDriver();

        // 1) Navigate to El País and go to Opinion section (Spanish Requirement)
        ElpaisHomePage home = new ElpaisHomePage(driver);
        home.open();
        home.ensureSpanish();
        home.goToOpinion();

        // 2) Collect first 5 article URLs from the Opinion listing page
        OpinionPage opinion = new OpinionPage(driver);
        List<String> urls = opinion.getFirstFiveArticleUrls();

        ArticlePage article = new ArticlePage(driver);

        // RapidAPI key is kept outside code (env var) to avoid to leak credentials in GitHub
        String apikey = System.getenv().getOrDefault("RAPID_API_KEY", "");
        TranslationService translator = new TranslationService(apikey);

        List<String> translatedHeaders = new ArrayList<>();

        int i = 1;
        for (String url : urls) {
            article.open(url);

            String titleEs = article.getTitle();
            String contentEs = article.getContentText();

            System.out.println("\n=== Article " + i + " (ES) ===");
            System.out.println("Title: " + titleEs);
            System.out.println("Content (first 500 chars): " + (contentEs.length() > 500 ? contentEs.substring(0,500) : contentEs));

            // 3) Download cover Image
            String imgUrl = article.getCoverImageUrlIfAny();
            ImageDownloader.downloadTo(imgUrl, Path.of("downloads/images"), "article_" + i + ".jpg");

            // 4) Translate titles ES -> EN (continue even if one translation fails)
            String titleEn;
            try {
                titleEn = translator.translateEsToEn(titleEs);
            } catch (Exception e) {
                System.out.println("TRANSLATION FAILED for title: " + titleEs);
                e.printStackTrace();
                titleEn = "";
            }

            System.out.println("Translated Title (EN): " + titleEn);
            if (!titleEn.isBlank()) translatedHeaders.add(titleEn);

            i++;
        }

        // 5) Analyze translated titles: words repeated more than twice across all titles combined
        Map<String, Integer> repeated = TextAnalyzer.repeatedWordsMoreThan2(translatedHeaders);

        System.out.println("\n=== Repeated Words (>2) Across Translated Headers ===");
        if (repeated.isEmpty()) {
            System.out.println("No repeated words found more than twice.");
        } else {
            repeated.forEach((w,c) -> System.out.println(w + " -> " + c));
        }
    }
}
