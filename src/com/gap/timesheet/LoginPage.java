package com.gap.timesheet;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

	public static void login(WebDriver driver, Properties prop) {
		WebElement ele_Username = driver.findElement(By.id("username"));
		WebElement ele_Password = driver.findElement(By.id("password"));
		WebElement ele_Login = driver.findElement(By.id("gw-login-button"));

		String strUsername = prop.getProperty("Username");
		String strPassword = prop.getProperty("Password");
		ele_Username.sendKeys(strUsername);
		ele_Password.sendKeys(strPassword);
		ele_Login.click();
	}
}
