package com.worldclasscitizen.jobTerminal.sites.wanted;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WantedCrawler {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver driver = new ChromeDriver(options);

        // 518 -> 개발 직무 전체, 660 -> 자바 개발자만 필터링, years=0 -> 신입만 필터링
        String url = "https://www.wanted.co.kr/wdlist/518/660?years=0";
        driver.get(url);

        try {
            Thread.sleep(2000); // 페이지 로딩 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("페이지 로딩 중 Thread 가 Interrupt 되었습니다. " + e.getMessage());
        }

        List<WebElement> jobCards = driver.findElements(By.cssSelector("div[data-cy='job-card']")); // 실제 클래스명 확인 필요
        List<String> jobLinks = new ArrayList<>();
        for (WebElement card : jobCards) {
            WebElement anchor = card.findElement(By.tagName("a"));
            String link = "https://www.wanted.co.kr" + anchor.getDomAttribute("href");
            jobLinks.add(link);
        }
    }
}
