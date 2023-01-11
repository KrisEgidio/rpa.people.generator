package rpa.people.generator;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import rpa.people.generator.browser.DriverFactory;
import rpa.people.generator.model.Person;

public class Main {

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "resources\\chromedriver.exe");
		List<Person> personList = generateNames();
		createOutputExcel(personList);
		
	}


	private static List<Person> generateNames() {
		
		List<Person> personList = new ArrayList<>();
		
		try (DriverFactory driverFactory = new DriverFactory()){
			
			WebDriver driver = driverFactory.getChromeDriver();
			
			driver.get("https://pt.fakenamegenerator.com");
			new Select(driver.findElement(By.id("gen"))).selectByValue("random");
			new Select(driver.findElement(By.id("n"))).selectByValue("br");
			new Select(driver.findElement(By.id("c"))).selectByValue("br");
			
			for(int counter = 0; counter <= 10; counter++) {
				
				driver.findElement(By.id("genbtn")).click();
				
				String fullName = driver.findElement(By.xpath("//div[@class='address']//h3")).getText().trim();
				String[] addressArray = driver.findElement(By.xpath("//div[@class='address']//div")).getText().split("\n");
				String city = addressArray[1].split("-")[0].trim();
				String country = driver.findElement(By.xpath("//div[@id='nameSetApps']//div//p//a")).getText();
				
				String address = addressArray[0].trim();
				String uf = addressArray[1].split("-")[1].trim();
				String zipCode = addressArray[2].trim();
				
				
				String email = driver.findElement(By.xpath("//dl[@class='dl-horizontal']//dt[contains(text(), 'Endereço de e-mail')]/following-sibling::dd")).getText().split("Este é")[0].trim();
		
			
				Person person = new Person(fullName, email, "", zipCode, address, country,uf, city);
				personList.add(person);
				
			}
			
			return personList;
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return personList;
	}

	
	
	private static void createOutputExcel(List<Person> personList) {
		
		String outputFile = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\people.xlsx";
		
		try(Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(outputFile)) {
			
			Sheet sheet = workbook.createSheet("people");
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("fullName");
			headerRow.createCell(1).setCellValue("city");
			headerRow.createCell(2).setCellValue("country");
			headerRow.createCell(3).setCellValue("email");
			headerRow.createCell(4).setCellValue("address");
			headerRow.createCell(5).setCellValue("gender");
			headerRow.createCell(6).setCellValue("uf");
			headerRow.createCell(7).setCellValue("zipCode");
			
			int line = 1;
			for(Person person : personList) {
				Row row = sheet.createRow(line);
				row.createCell(0).setCellValue(person.getFullName());
				row.createCell(1).setCellValue(person.getCity());
				row.createCell(2).setCellValue(person.getCountry());
				row.createCell(3).setCellValue(person.getEmail());
				row.createCell(4).setCellValue(person.getAddress());
				row.createCell(5).setCellValue(person.getGender());
				row.createCell(6).setCellValue(person.getUf());
				row.createCell(7).setCellValue(person.getZipCode());
				
				line++;
			}
			
			workbook.write(outputStream);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


}
