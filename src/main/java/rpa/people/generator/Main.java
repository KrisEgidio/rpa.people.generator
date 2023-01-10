package rpa.people.generator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import rpa.people.generator.browser.DriverFactory;
import rpa.people.generator.model.Person;

public class Main {

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "resources\\chromedriver.exe");
		generateNames();
		
	}


	private static void generateNames() {
		try (DriverFactory driverFactory = new DriverFactory()){
			
			WebDriver driver = driverFactory.getChromeDriver();
			
			driver.get("https://pt.fakenamegenerator.com");
			new Select(driver.findElement(By.id("gen"))).selectByValue("random");
			new Select(driver.findElement(By.id("n"))).selectByValue("br");
			new Select(driver.findElement(By.id("c"))).selectByValue("br");
			
			List<Person> people = new ArrayList<>();
			
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
				people.add(person);
				System.out.println("name: " + person.getFullName() + " email: " + person.getEmail() );
			}
			
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}


}
