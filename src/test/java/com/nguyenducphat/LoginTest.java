package com.nguyenducphat;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class LoginTest {
    private WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080"); // Giả lập màn hình thực tế để tránh lỗi UI
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        driver.get("https://sinhvien1.tlu.edu.vn/");

        System.out.println("Print Title : " + driver.getTitle());
        System.out.println("Print URL: " + driver.getCurrentUrl());
        System.out.println("\nBắt đầu đăng nhập vào TLU...");
        
        // Nhập thông tin
        WebElement usernameInput = driver.findElement(By.id("username"));
        usernameInput.clear();
        usernameInput.sendKeys("2351067119");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("079205011830");

        // Click đăng nhập
        WebElement loginBtn = driver.findElement(By.xpath("//button[contains(text(), 'Đăng nhập')]"));
        loginBtn.click();

        // Chờ xử lý
        Thread.sleep(5000);

        boolean isLoginFailed = false;
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            isLoginFailed = driver.findElements(By.xpath("//button[contains(text(), 'Đăng nhập')]")).size() > 0;
        } catch (Exception e) {
            isLoginFailed = false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }

        // Nếu thất bại thì chụp lại màn hình trên máy chủ GitHub để xem lỗi do đâu
        if (isLoginFailed) {
            System.out.println("⚠️ Đăng nhập thất bại. Đang chụp màn hình lỗi...");
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            Files.createDirectories(Paths.get("target/surefire-reports"));
            Files.copy(screenshot.toPath(), Paths.get("target/surefire-reports/failure_screenshot.png"), StandardCopyOption.REPLACE_EXISTING);
        }

        // Chạy chuẩn (Đánh Fail thực sự nếu lỗi)
        Assert.assertFalse(isLoginFailed, "Login failed! Mật khẩu không đúng hoặc TLU chặn IP Github. Hãy tải file test-reports về để xem ảnh màn hình lỗi.");
        System.out.println("✅ Login success!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
