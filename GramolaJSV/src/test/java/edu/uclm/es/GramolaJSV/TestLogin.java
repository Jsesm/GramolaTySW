package edu.uclm.es.GramolaJSV;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestLogin {
        public static void main(String[] args) {
                WebDriver driver = new ChromeDriver();
                JavascriptExecutor js = (JavascriptExecutor) driver;

                driver.get("http://127.0.0.1:4200");
                driver.manage().window().maximize();

                WebElement email = driver.findElement(By.xpath(
                                "//*[@id=\"email\"]"));
                email.sendKeys("j.sesmero.v@gmail.com");

                WebElement password = driver.findElement(By.xpath(
                                "//*[@id=\"password\"]"));
                password.sendKeys("Jaime1234567");

                WebElement inicioSesion = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[1]/app-login/div/form/button"));
                inicioSesion.click();

        }
}