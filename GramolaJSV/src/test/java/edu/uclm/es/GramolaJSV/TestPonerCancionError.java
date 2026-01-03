package edu.uclm.es.GramolaJSV;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestPonerCancionError {

        public static void main(String[] args) throws InterruptedException {

                ChromeOptions options = ponerGeolocalizacion();

                WebDriver driver = new ChromeDriver(options);
                driver = ponerCookies(driver);

                WebElement soyCliente = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[1]/app-login/div/button"));
                soyCliente.click();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                "//*[@id=\"cancion\"]")));

                WebElement buscador = driver.findElement(By.xpath(
                                "//*[@id=\"cancion\"]"));
                buscador.sendKeys("Melendi");

                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                "/html/body/app-root/app-music-user/div/div[2]/div[2]/div/p[2]")));

                WebElement ponerCancion = driver.findElement(By.xpath(
                                "/html/body/app-root/app-music-user/div/div[2]/div[2]/button"));
                ponerCancion.click();

                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                "/html/body/app-root/app-music-user/div[2]/div/div[2]/button[2]")));

                WebElement irAlPago = driver.findElement(By.xpath(
                                "/html/body/app-root/app-music-user/div[2]/div/div[2]/button[2]"));
                irAlPago.click();

                wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                // Stripe suele envolver sus inputs en iframes que contienen la palabra
                // 'privateStripeFrame' o similar
                // Lo más seguro es buscar el iframe que sea padre de este elemento
                wait.until(ExpectedConditions
                                .frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe[title*='tarjeta']")));

                WebElement campoTarjeta = wait
                                .until(ExpectedConditions.visibilityOfElementLocated(By.name("cardnumber")));

                campoTarjeta.sendKeys("4244242424242 2356 567");

                driver.switchTo().defaultContent();// Salir del iframe

                WebElement confirmarPago = driver.findElement(By.xpath(
                                "//*[@id=\"submit\"]"));
                confirmarPago.click();

                wait = new WebDriverWait(driver, Duration.ofSeconds(8));
                By selectorMensaje = By.xpath("//*[@id='card-error']//p");

                wait.until(ExpectedConditions.visibilityOfElementLocated(selectorMensaje));
                String mensajeDeError = driver.findElement(selectorMensaje).getText();

                System.out.println("\n--- RESULTADO DEL TEST ---");
                System.out.println("El mensaje de error ha sido: " + mensajeDeError);

                // 3. Tu Assert
                assertNotEquals("", mensajeDeError.trim(), "No ha habido error (el mensaje está vacío)");

        }

        public static ChromeOptions ponerGeolocalizacion() {
                ChromeOptions options = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("profile.default_content_setting_values.geolocation", 1);// Para que permita la localización
                options.setExperimentalOption("prefs", prefs);

                return options;

        }

        public static WebDriver ponerCookies(WebDriver driver) {
                driver.get("https://open.spotify.com");

                Cookie dc = new Cookie.Builder("sp_dc",
                                "AQDsoJuLXyZDm2Qhw1GgiM-2u8kiC991Xnslwu38DYFqzDPnVnzHGHJ8TlxP1uPBKHCVgp3_ppZRAdC6jLrIsgqd1y8BAopLeya-T_emVXFsZW5w7A4F7IAdYgVmEu4kZ00K7bmVRxU_jw2sj9Nxp2MGItS7__ewNfH2MYrmXQzc7RUv3SeoFl2mnwdcRXHXlP7hhwK8yVyM0zE4Xqo")
                                .domain(".spotify.com")
                                .path("/")
                                .build();

                Cookie key = new Cookie.Builder("sp_key", "831b334b-00e9-407b-b955-06afbad31881")
                                .domain(".spotify.com")
                                .path("/")
                                .build();

                driver.manage().addCookie(dc);
                driver.manage().addCookie(key);

                driver.get("http://127.0.0.1:4200");
                driver.manage().window().maximize();
                return driver;
        }
}