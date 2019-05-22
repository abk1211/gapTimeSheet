package com.gap.timesheet;

import java.util.concurrent.TimeUnit;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class Timesheet {
	static WebDriver driver;
	public static Properties readpropertiesfile() throws Exception {
		String filepath = ".\\Properties\\TimeSheet.properties";
		File file = new File(filepath);
		FileInputStream fis = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fis);
		return prop;
	}
	public static void filltimesheet() throws Exception {
		Properties objprop = readpropertiesfile();
		System.setProperty("webdriver.chrome.driver", objprop.getProperty("ChromeEXE"));
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.get(objprop.getProperty("URL"));
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		String expected_Name = objprop.getProperty("UserName");
		String actual_Name = driver.findElement(By.id("ppm_header_user")).getText();
		System.out.println("Actual Name : "+actual_Name);
		if(actual_Name.equalsIgnoreCase(expected_Name)) {
			driver.findElement(By.xpath("//button[@title='Current Timesheet']")).click();
			WebElement timeperiod = driver.findElement(By.name("timeperiod"));
			if(timeperiod.isDisplayed()) {
				String strdays = Date_to_Day.datecreation();
				Select sel_timesheet = new Select(timeperiod);
				sel_timesheet.selectByVisibleText(strdays);
				String strTimesheetTable = "//div[@class='ppm_gridcontent']/table[@class='ppm_list_layout']";
				String expected_Investment = objprop.getProperty("InvestmentName");
				WebElement timesheetTable = driver.findElement(By.xpath(strTimesheetTable));

				if(timesheetTable.isDisplayed()) {
					int table_rows = driver.findElements(By.xpath(strTimesheetTable+"/tbody/tr")).size();
					System.out.println("No of Rows in Table : "+table_rows);
					int i = 0;
					boolean bolInvestment = false;
					for(i=1; i<=table_rows; i++) {
						String strRowtext = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+i+"]")).getText();
						if(strRowtext.contains(expected_Investment)) {
							filldetails(strTimesheetTable,i,objprop);
							bolInvestment = true;
							break;
						}
					}
					if(bolInvestment)
						System.out.println("Investment Name found");
					else
						System.err.println("Investment Name mismatch or not found");
					Thread.sleep(3000);
					SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
					Date d = new Date();
					String strToday = sdf.format(d).toString();

					if(strToday.equalsIgnoreCase("Friday")) {
						driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click(); 
						//						driver.findElement(By.xpath("//button[contains(text(),'Submit for Approval')]")).click(); 
					} 
					else
						driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click();
				}
				else
					System.err.println("Timesheet Table is not displayed in the page.");
			}
			else 
				System.err.println("***********Page Not loaded Properly***************");
		}
		else
			System.err.println("***********Invalid UserName***************");
		driver.findElement(By.id("ppm_header_logout")).click();
		driver.close();
		driver.quit();
	}
	private static void filldetails(String strTimesheetTable, int i, Properties objpProp) {
		int table_colomns = driver.findElements(By.xpath(strTimesheetTable+"/tbody/tr["+i+"]/td")).size();
		int k=i+1;
		String strHours = "8.00";
		System.out.println("pass "+table_colomns);
		for(int j=8; j<=table_colomns; j++) {
			WebElement web_Baseline = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+i+"]/td["+j+"]/input"));
			WebElement web_OutOfOffice = driver.findElement(By.xpath(strTimesheetTable+"/tbody/tr["+k+"]/td["+j+"]/input"));
			String inputtags = web_Baseline.getAttribute("title");
			System.out.println(inputtags);
			web_Baseline.clear();
			web_OutOfOffice.clear();
			if(inputtags.contains("Mon") && !web_Baseline.getAttribute("class").contains("ppm_res_cal_non_working")) {
				if(objpProp.getProperty("Mon").length()>0)
					web_OutOfOffice.sendKeys(objpProp.getProperty("Mon"));
				else
					web_Baseline.sendKeys(strHours);

			}
			if(inputtags.contains("Tue") && !web_Baseline.getAttribute("class").contains("ppm_res_cal_non_working")) {
				if(objpProp.getProperty("Tue").length()>0)
					web_OutOfOffice.sendKeys(objpProp.getProperty("Tue"));
				else
					web_Baseline.sendKeys(strHours);
			}
			if(inputtags.contains("Wed") && !web_Baseline.getAttribute("class").contains("ppm_res_cal_non_working")) {
				if(objpProp.getProperty("Wed").length()>0)
					web_OutOfOffice.sendKeys(objpProp.getProperty("Wed"));
				else
					web_Baseline.sendKeys(strHours);
			}
			if(inputtags.contains("Thu") && !web_Baseline.getAttribute("class").contains("ppm_res_cal_non_working")) {
				if(objpProp.getProperty("Thu").length()>0)
					web_OutOfOffice.sendKeys(objpProp.getProperty("Thu"));
				else
					web_Baseline.sendKeys(strHours);
			}
			if(inputtags.contains("Fri") && !web_Baseline.getAttribute("class").contains("ppm_res_cal_non_working")) {
				if(objpProp.getProperty("Fri").length()>0)
					web_OutOfOffice.sendKeys(objpProp.getProperty("Fri"));
				else
					web_Baseline.sendKeys(strHours);
			}
		}
	}
}