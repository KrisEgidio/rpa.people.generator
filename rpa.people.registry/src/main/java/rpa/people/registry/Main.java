package rpa.people.registry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import rpa.people.registry.browser.DriverFactory;
import rpa.people.registry.model.Person;

public class Main {

	private static List<Person> personList;
	private static String pathInput = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\3.completo\\people.xlsx";
	private static String pathOutput = "C:\\Users\\Kris\\Desktop\\rpa.people.generator.files\\4.processados\\people.xlsx";

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Kris\\Desktop\\Deploy\\chromedriver\\chromedriver.exe");
		personList = new ArrayList<Person>();
		
		readExcel();
		inputPeople();
		writeOutputFile();
	}

	private static void readExcel() {

		try (FileInputStream fileInputStream = new FileInputStream(pathInput);
				Workbook workbook = new XSSFWorkbook(fileInputStream);) {

			workbook.getSheetAt(0).rowIterator().forEachRemaining(row -> {

				if (row.getCell(0) != null) {
					String fullName = row.getCell(0).getStringCellValue();
					String city = row.getCell(1).getStringCellValue();
					String country = row.getCell(2).getStringCellValue();
					String email = row.getCell(3).getStringCellValue();
					String address = row.getCell(4).getStringCellValue();
					String gender = row.getCell(5).getStringCellValue();
					String uf = row.getCell(6).getStringCellValue();
					String zipCode = row.getCell(7).getStringCellValue();

					if (!fullName.contains("fullName")) {
						personList
								.add(new Person(fullName, email, gender, zipCode, address, country, uf, city, "", ""));
					}
				}
			});
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		moveFile(pathInput, pathOutput);

	}

	private static void inputPeople() {
		try (DriverFactory driverFactory = new DriverFactory()) {
			WebDriver driver = driverFactory.getChromeDriver();

			driver.get("https://rpa-insiders-tools.vercel.app/exercicios/exercicio1");
			personList.forEach(person -> {
				inputData(driver, person);
			});

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static void inputData(WebDriver driver, Person person) {

		if (validadeIfNull(person)) {

			try {
				driver.findElement(By.xpath("//label[contains(text(), 'Email')]//following-sibling::input"))
						.sendKeys(person.getEmail());
				driver.findElement(By.xpath("//label[contains(text(), 'Nome')]//following-sibling::input"))
						.sendKeys(person.getFullName());
				driver.findElement(By.xpath("//label[contains(text(), 'Endereço')]//following-sibling::input"))
						.sendKeys(person.getAddress());
				driver.findElement(By.xpath("//label[contains(text(), 'País')]//following-sibling::input"))
						.sendKeys(person.getCountry());
				driver.findElement(By.xpath("//label[contains(text(), 'Cidade')]//following-sibling::input"))
						.sendKeys(person.getCity());
				driver.findElement(By.xpath("//label[contains(text(), 'CEP')]//following-sibling::input"))
						.sendKeys(person.getZipCode());

				new Select(
						driver.findElement(By.xpath("//label[contains(text(), 'Estado')]//following-sibling::select")))
						.selectByValue(person.getUf());

				driver.findElement(By.xpath("//input[@value='" + person.getGender() + "']")).click();

				driver.findElement(By.xpath("//input[@id='terms']")).click();
				driver.findElement(By.xpath("//button[@id='registerEmployerBtn']")).click();

				WebDriverWait wait = new WebDriverWait(driver, 40);
				wait.until(ExpectedConditions.textToBePresentInElement(
						driver.findElement(By.xpath("(//tbody//tr)[1]//td[1]")), person.getFullName()));

				person.setStatus("OK");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println(person.getFullName() + " - Falha ao cadastrar");

				driver.get("https://rpa-insiders-tools.vercel.app/exercicios/exercicio1");

				person.setStatus("NOK");
				person.setStatusMessage(e.getClass().getName());

			}
		} else {
			person.setStatus("NOK");
			person.setStatusMessage("Não pode haver campos nulos ou vazios");
		}

	}

	private static boolean validadeIfNull(Person person) {
		if (person.getFullName() == null || person.getFullName().isEmpty())
			return false;
		if (person.getEmail() == null || person.getEmail().isEmpty())
			return false;
		if (person.getCity() == null || person.getCity().isEmpty())
			return false;
		if (person.getCountry() == null || person.getCountry().isEmpty())
			return false;
		if (person.getAddress() == null || person.getAddress().isEmpty())
			return false;
		if (person.getGender() == null || person.getGender().isEmpty())
			return false;
		if (person.getUf() == null || person.getUf().isEmpty())
			return false;
		if (person.getZipCode() == null || person.getZipCode().isEmpty())
			return false;
		return true;
	}
	
	private static void moveFile(String input, String output) {
		try {
			Files.move(Paths.get(input), Paths.get(output), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("Falha ao mover o arquivo: " + e.getMessage());
		}
	}
	
	private static void writeOutputFile() {
		try (Workbook workbook = new XSSFWorkbook();
				FileOutputStream outputStream = new FileOutputStream(pathOutput)) {
				
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
				headerRow.createCell(8).setCellValue("Status");
				headerRow.createCell(9).setCellValue("Mensagem");
				
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
					row.createCell(8).setCellValue(person.getStatus());
					row.createCell(9).setCellValue(person.getStatusMessage());
					
					line++;
				}
				
				workbook.write(outputStream);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
	}

}
