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

        // 518 -> 개발 직무 전체
        // 660 -> 자바 개발자만 필터링
        // years=0 -> 신입만 필터링
        // employment_types=employment_type.regular -> 정규직만 필터링
        String url = "https://www.wanted.co.kr/wdlist/518/660?years=0&employment_types=job.employment_type.regular";
        driver.get(url);

        try {
            Thread.sleep(2000); // 페이지 로딩 대기
        } catch (InterruptedException e) {
            System.err.println("페이지 로딩 중 Thread 가 Interrupt 되었습니다. " + e.getMessage());
        }

        List<WebElement> jobCards = driver.findElements(By.cssSelector("div[data-cy='job-card']")); // 실제 클래스명 확인 필요
        List<String> jobLinks = new ArrayList<>();
        for (WebElement card : jobCards) {
            WebElement anchor = card.findElement(By.tagName("a"));
            String link = "https://www.wanted.co.kr" + anchor.getDomAttribute("href");
            jobLinks.add(link);
        }

        System.out.println("공고 개수: " + jobCards.size());

        // 각 공고 페이지 방문 및 정보 추출
        for (String jobUrl : jobLinks) {
            driver.get(jobUrl);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("취업 공고 상세 페이지 로딩 중 Thread 가 Interrupt 되었습니다. " + e.getMessage());
            }

            try {
                // 0. 채용 공고 제목
                String title = "";
                try {
                    WebElement metaElement = driver.findElement(By.cssSelector("a[data-attribute-id='company__click']"));
                    title = metaElement.getDomAttribute("data-position-name");
                } catch (Exception e) {
                    title = "제목 정보 없음";
                }

                // 1. 회사명
                String company = "";
                try {
                    WebElement metaElement = driver.findElement(By.cssSelector("a[data-attribute-id='company__click']"));
                    company = metaElement.getDomAttribute("data-company-name");
                } catch (Exception e) {
                    company = "회사명 정보 없음";
                }
                // 2. 회사 위치

                // 3. 근무 경력
                String experienceLevel = "";
                try {
                    List<WebElement> spans = driver.findElements(By.tagName("span"));
                    for (WebElement span : spans) {
                        String text = span.getText().trim();
                        if (text.equals("신입") ||
                                text.matches("경력 \\d+년.*") ||
                                text.matches("신입-경력 \\d+년.*") ||
                                text.matches("\\d+년차.*") ||
                                text.contains("신입 이상") ||
                                text.matches("\\d+년.*이상") ||
                                text.contains("경력") && !text.equals("경력")) {
                            experienceLevel = text;
                            break;
                        }
                    }
                    if (experienceLevel.isEmpty()) {
                        experienceLevel = "경력 정보 없음";
                    }
                } catch (Exception e) {
                    experienceLevel = "경력 정보 없음";
                }

                // 4. 채용 공고 내용

                // 5. 마감일
                String deadline = "";
                try {
                    deadline = driver.findElement(By.xpath("//h2[contains(text(), '마감일')]/following-sibling::span")).getText().trim();
                } catch (Exception e) {
                    try {
                        List<WebElement> spans = driver.findElements(By.tagName("span"));
                        for (WebElement span : spans) {
                            String text = span.getText().trim();
                            if (text.equals("상시채용") || text.matches("D-\\d+") ||
                                    text.matches("\\d{4}-\\d{1,2}-\\d{1,2}") || text.contains("까지")) {
                                deadline = text;
                                break;
                            }
                        }
                        if (deadline.isEmpty()) {
                            deadline = "마감일 정보 없음";
                        }
                    } catch (Exception e2) {
                        deadline = "마감일 정보 없음";
                    }
                }

                System.out.println("제목 : " + title);
                System.out.println("회사명 : " + company);
                System.out.println("근무 경력 : " + experienceLevel);
//                System.out.println("근무 형태 : " + employmentType);
                System.out.println("마감일 : " + deadline);
                System.out.println("==========");
            } catch (Exception e) {
                System.err.println(" " + e.getMessage());
            }
        }
    }
}
