package com.gap.timesheet;


import java.util.concurrent.TimeUnit;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.gap.timesheet.Load_Properties;
import com.gap.timesheet.LoginPage;
import com.gap.timesheet.StatusValidation;

public class Timesheet {
	static WebDriver driver;
	static Logger log = Logger.getLogger(Timesheet.class.getName());

	public static void filltimesheet() throws Exception {

		Desktop desktop = Desktop.getDesktop();
		desktop.open(new File(".\\Properties\\TimeSheet.properties"));
		Thread.sleep(20000);
		Runtime.getRuntime().exec(".\\exe\\closenotepad.exe");
		
		//To load the log4j and user entry details property files
		Load_Properties.readpropertiesfile(".\\Properties\\log4j.properties");
		Properties objprop = Load_Properties.readpropertiesfile(".\\Properties\\TimeSheet.properties");

		// To load the Chrome driver
		System.setProperty("webdriver.chrome.driver", objprop.getProperty("ChromeEXE"));
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.get(objprop.getProperty("URL"));
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

		//if Login page exists
		if(driver.getTitle().equalsIgnoreCase("Gapweb Login Page"))
			LoginPage.login(driver, objprop);

		String expected_Name = objprop.getProperty("Name");
		String actual_Name = driver.findElement(By.id("ppm_header_user")).getText();
		log.info("Actual Name : "+actual_Name);
		if(actual_Name.equalsIgnoreCase(expected_Name)) {
			driver.findElement(By.xpath("//button[@title='Current Timesheet']")).click();
			WebElement timeperiod = driver.findElement(By.name("timeperiod"));
			if(timeperiod.isDisplayed()) {
				Select sel_timesheet = new Select(timeperiod);
				String strTimeperiod =	sel_timesheet.getFirstSelectedOption().getText();
				log.info(strTimeperiod);
				String strTimesheetTable = "//div[@class='ppm_gridcontent']/table[@class='ppm_list_layout']";
				String expected_Description = objprop.getProperty("DescriptionName");
				WebElement timesheetTable = driver.findElement(By.xpath(strTimesheetTable));

				if(timesheetTable.isDisplayed()) {
					int table_rows = driver.findElements(By.xpath(strTimesheetTable+"/tbody/tr")).size();
					log.info("No of Rows in Table : "+table_rows);
					int i = 0;
					int a = 0,b = 0;
					boolean bolInvestment = false;
					for(i=1; i<=table_rows; i++) {
						String strRowtext = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+i+"]")).getText();
						if(strRowtext.contains(expected_Description)) {
							a = i;
							bolInvestment = true;
						}
						else if(strRowtext.contains("Out of Office"))
							b = i;
					}
					if(bolInvestment) {
						log.info("Investment Name found");
						filldetails(strTimesheetTable, a, b, objprop);
					}
					else
						log.error("Investment Name mismatch or not found");

					Thread.sleep(2000);

					SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
					Date d = new Date();
					String strToday = sdf.format(d).toString();

					if(strToday.equalsIgnoreCase("Friday") || 
					   objprop.getProperty("Submit").equalsIgnoreCase("Yes") ||
					   objprop.getProperty("Submit").equalsIgnoreCase("Y")) {
						driver.findElement(By.xpath("(//button[contains(text(),'Save')])[2]")).click(); 
						driver.findElement(By.xpath("(//button[contains(text(),'Submit for Approval')])[2]")).click(); 
						log.info("As Today is Friday we are Submitting for Approval");
						StatusValidation.verifyStatus(driver,strTimeperiod, log);
					} 
					else {
						driver.findElement(By.xpath("(//button[contains(text(),'Save')])[2]")).click();
						log.info("As Today is not Friday we are Saving the Timesheet");
					}
				}
				else
					log.error("Timesheet Table is not displayed in the page.");
			}
			else 
				log.error("***********Page Not loaded Properly***************");
		}
		else
			log.error("***********Invalid UserName***************");
		Thread.sleep(3000);
		driver.findElement(By.id("ppm_header_logout")).click();
		driver.close();
		driver.quit();
	}
	private static void filldetails(String strTimesheetTable, int a, int b, Properties objpProp) {
		int table_colomns = driver.findElements(By.xpath(strTimesheetTable+"/tbody/tr["+a+"]/td")).size();
		int c=a+1;
		String[] strHours = objpProp.get("RegularHours").toString().split(",");
		log.info("pass "+table_colomns);
		for(int j=8; j<=table_colomns; j++) {
			WebElement web_Baseline_1 = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+a+"]/td["+j+"]/input"));
			WebElement web_Baseline_2 = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+c+"]/td["+j+"]/input"));
			WebElement web_OutOfOffice = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+b+"]/td["+j+"]/input"));
			String inputtags = web_Baseline_1.getAttribute("title").trim();
			log.info(inputtags);
			web_Baseline_1.clear();
			web_Baseline_2.clear();
			web_OutOfOffice.clear();
			if(inputtags.length()>0) {
				switch (inputtags.substring(0, 3)) {
				case "Sun" :
					System.out.println("Not a Working Day");
					break;

				case "Mon" :
					if(objpProp.getProperty("Mon").length()>0)
						web_OutOfOffice.sendKeys(objpProp.getProperty("Mon"));
					else {
						web_Baseline_1.sendKeys(strHours[0]);
						web_Baseline_2.sendKeys(strHours[1]);
					}
					break;

				case "Tue" :
					if(objpProp.getProperty("Tue").length()>0)
						web_OutOfOffice.sendKeys(objpProp.getProperty("Tue"));
					else {
						web_Baseline_1.sendKeys(strHours[0]);
						web_Baseline_2.sendKeys(strHours[1]);
					}
					break;

				case "Wed" :
					if(objpProp.getProperty("Wed").length()>0)
						web_OutOfOffice.sendKeys(objpProp.getProperty("Wed"));
					else {
						web_Baseline_1.sendKeys(strHours[0]);
						web_Baseline_2.sendKeys(strHours[1]);
					}
					break;

				case "Thu" :
					if(objpProp.getProperty("Thu").length()>0)
						web_OutOfOffice.sendKeys(objpProp.getProperty("Thu"));
					else {
						web_Baseline_1.sendKeys(strHours[0]);
						web_Baseline_2.sendKeys(strHours[1]);
					}
					break;

				case "Fri" :
					if(objpProp.getProperty("Fri").length()>0)
						web_OutOfOffice.sendKeys(objpProp.getProperty("Fri"));
					else {
						web_Baseline_1.sendKeys(strHours[0]);
						web_Baseline_2.sendKeys(strHours[1]);
					}
					break;

				case "Sat" :
					System.out.println("Not a Working Day");
					break;

				default :
					System.out.println("Input Tag is not present "+inputtags);
					break;
				}
			}
		}
	}
}