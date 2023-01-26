package rpa.people.gender;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import rpa.people.gender.browser.DriverFactory;
import rpa.people.gender.model.Person;

public class Main {

	private static String pathInput = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\1.entrada\\people.xlsx";
	private static String pathGender = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\2.gender\\people.xlsx";
	private static String pathOutput = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\3.completo\\people.xlsx";

	private static List<Person> personList;
	
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Kris\\Desktop\\Deploy\\chromedriver\\chromedriver.exe");
		personList = new ArrayList<>();

		readExcel();
		search();
		writeOutputFile();
	}
	
	private static void readExcel() {
		
		moveFile(pathInput, pathGender);

		try (FileInputStream fileInputStream = new FileInputStream(pathGender);
				Workbook workbook = new XSSFWorkbook(fileInputStream);) {

			workbook.getSheetAt(0).rowIterator().forEachRemaining(row -> {

				String cellName = row.getCell(0).getStringCellValue();

				if (!cellName.contains("fullName")) {
					personList.add(new Person(cellName, ""));
				}
			});

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private static void search() {

		try (DriverFactory driverFactory = new DriverFactory()) {

			WebDriver driver = driverFactory.getChromeDriver();
			driver.get("https://rpa-insiders-tools.vercel.app/utils/name-probability");
			personList.forEach(person -> {
				String gender = searchName(driver, person.getName().split(" ")[0]);
				personList.get(personList.indexOf(person)).setGender(gender);
			});

		} catch (

		Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println(personList.toString());
	}

	private static void writeOutputFile() {
		try (FileInputStream fileInputStream = new FileInputStream(pathGender);
				XSSFWorkbook fileWorkbook = new XSSFWorkbook(fileInputStream);
				FileOutputStream outputStream = new FileOutputStream(pathGender)) {

			fileWorkbook.getSheetAt(0).rowIterator().forEachRemaining(row -> {
				if (!row.getCell(0).getStringCellValue().contains("fullName")) {
					row.createCell(5).setCellValue(personList.get(row.getRowNum() - 1).getGender());
				}
			});

			fileWorkbook.write(outputStream);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		moveFile(pathGender, pathOutput);
	}
	
	private static void moveFile(String input, String output) {
		try {
			Files.move(Paths.get(input), Paths.get(output), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("Falha ao mover o arquivo: " + e.getMessage());
		}
	}

	private static String searchName(WebDriver driver, String name) {
		driver.findElement(By.id("search")).sendKeys(name);
		driver.findElement(By.xpath("//button")).click();

		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.xpath("(//h1)[2]")), name));

		String gender = driver.findElement(By.xpath("//p//strong[contains(text(), 'Gênero')]/parent::p")).getText()
				.replace("Gênero: ", "").trim();
		driver.findElement(By.id("search")).clear();

		return gender;

	}

}
