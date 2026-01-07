package edu.uclm.es.GramolaJSV;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestRegistro {

        private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/gramola?serverTimezone=UTC&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&maxAllowedPacket=134217728";
        private static final String DB_USER = "tysweb2025";
        private static final String DB_PASS = "12345678";

        public static void main(String[] args) {

                int users = contarUsers();
                WebDriver driver = new ChromeDriver();
                JavascriptExecutor js = (JavascriptExecutor) driver;

                driver.get("http://127.0.0.1:4200");
                driver.manage().window().maximize();

                WebElement QuieroRegistrar = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[1]/app-login/div/div/p/a"));
                QuieroRegistrar.click();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[1]/div[1]/input")));

                WebElement NombreBar = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[1]/div[1]/input"));
                NombreBar.sendKeys("BarJaime");

                WebElement email = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[1]/div[2]/input"));
                email.sendKeys("j.sesmero.v@gmail.com");

                WebElement password = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[2]/div[1]/input"));
                password.sendKeys("Jaime1234567");

                WebElement password2 = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[2]/div[2]/input"));
                password2.sendKeys("Jaime1234567");

                WebElement codigoPostal = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[3]/div[2]/input"));
                codigoPostal.sendKeys(
                                "Calle del Río Alberche, 32, Santa María de Benquerencia, Toledo, Castilla-La Mancha, 45007, España");

                WebElement ubiReal = driver.findElement(By.xpath(
                                "//*[@id=\"Ubireal\"]"));
                ubiReal.click();

                WebElement clientid = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[4]/div[1]/input"));
                clientid.sendKeys("8a897e4af6e040e4a0f858095bde1973");

                WebElement clientsecret = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[4]/div[2]/input"));
                clientsecret.sendKeys("7e477b4a2b8e46b9ac709828005261d0");

                WebElement Firma = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[5]/button[1]"));
                Firma.click();

                WebElement canvas = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div[2]/div/canvas"));
                Actions action = new Actions(driver);
                action.moveToElement(canvas, 10, 10) // Mueve a una posición inicial (10, 10)
                                .clickAndHold()
                                .moveByOffset(50, 50) // Dibuja una línea de 50px horizontal y 50px vertical
                                .moveByOffset(-20, 30) // Continúa dibujando
                                .release()
                                .perform();

                WebElement Guardar = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div[2]/div/div/button[2]"));
                Guardar.click();

                WebElement Registrarme = driver.findElement(By.xpath(
                                "/html/body/app-root/app-auth/div/div/div/div[2]/app-register/div/form/div[5]/button[2]"));
                Registrarme.click();

                wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                                "/html/body/app-root/app-mandarcorreo/div/div")));

                int usersAhora = contarUsers();

                System.out.println("\n\n\n\n--- RESULTADO DEL TEST ---");
                System.out.println("Usuarios al inicio: " + users);
                System.out.println("Usuarios al final: " + usersAhora);

                assertEquals(users + 1, usersAhora, "La base de datos no aumentó en 1");

                System.out.println("Se ha incluido el usuario correctamente!");

        }

        public static int contarUsers() {
                int total = 0;
                String query = "SELECT COUNT(*) AS total FROM user";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery(query)) {

                        if (rs.next()) {
                                total = rs.getInt("total");
                        }
                } catch (Exception e) {
                        System.err.println("Error conectando a la BD: " + e.getMessage());
                }
                return total;
        }

}
