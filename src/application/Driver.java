package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import entities.Acordo;
import entities.Pessoa;

public class Driver {

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "C:\\VENDAS\\SYSTEM\\chromedriver.exe");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--disable-gpu"); 
		options.addArguments("--disable-background-timer-throttling"); 
		options.addArguments("--disable-backgrounding-occluded-windows"); 
		options.addArguments("--disable-renderer-backgrounding"); 
		options.addArguments("--no-sandbox"); 
		options.addArguments("--disable-extensions"); 
		options.addArguments("--disk-cache-dir=/path/to/cache");
		
		WebDriver driver = new ChromeDriver(options);

		driver.get("LINK FORNCEDOR");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); 
		
		Pessoa pessoa = null;
		List<Pessoa> listPessoa = new ArrayList<Pessoa>();
		
		String file = ("C:\\VENDAS\\CONTRATOS.txt");
		FileReader fr = null;
		BufferedReader br = null;
		
		LocalDate date = LocalDate.now();
		File save = new File("C:\\VENDAS\\" + date + ".csv");
		

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(save, true))) {

			String nameLine = "TIPO_PESSOA" + ";" + "NOME" + ";" + "ID_PRODUTO" + ";" + "VALOR" + ";" + "STATUS";
			bw.write(nameLine);
			bw.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//// ----- LOGIN
		login(driver);

		try {

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = br.readLine();

			while (line != null) {

				String[] split = line.split(";");

				Long CpfCnpjLong = Long.parseLong(split[0]);
				Long ContratoLong = Long.parseLong(split[1]);

				pessoa = new Pessoa(CpfCnpjLong, ContratoLong);
				listPessoa.add(pessoa);

				line = br.readLine();

				String CpfCnpj = pessoa.getCpfCnpj().toString();

				if (CpfCnpj.length() <= 11) {
					CpfCnpj = String.format("%011d", Long.parseLong(CpfCnpj));
				} else if (CpfCnpj.length() <= 14) {
					CpfCnpj = String.format("%014d", Long.parseLong(CpfCnpj));
				}

				String contrato = pessoa.getContrato().toString();
				
				Instant Started = Instant.now();

				// PESQUISA POR CPF/CNPJ
				search(wait, CpfCnpj, driver, contrato);

				// RESULTADO DO PRODUTO
				product(wait, driver, contrato, CpfCnpj);

				// CONSULTA POR ACORDO
				dealQuery(wait, driver, contrato, CpfCnpj, save, Started);
			}

		} catch (IOException e) {
			System.out.println("Error IOException 1" + e.getMessage());
			
		} catch (ElementClickInterceptedException e) { 
			String[] args2 = null;	
			driver.navigate().refresh();
			main(args2);
		} finally {
			
			try {
				
				String fileNew = ("C:\\VENDAS\\CONTRATOS.txt");
				fr = new FileReader(fileNew);
				br = new BufferedReader(fr);

				String lineFinal = br.readLine();
				
				if(lineFinal == null) {
					
					File saveFinal = new File("C:\\VENDAS\\FINALIZADO.txt");

					try (BufferedWriter brFina = new BufferedWriter(new FileWriter(saveFinal, true))) {
						String lines = "FINALIZADO!";
						String linesTwo = "Quantidade localizada: " + (listPessoa.size() - 1) ;

						brFina.write(lines);
						brFina.newLine();
						brFina.write(linesTwo);
						brFina.newLine();

					} catch (NoSuchElementException	 e) {
						String[] args2 = null;	
						driver.navigate().refresh();
						main(args2);
					} catch (IOException e) {
						e.printStackTrace();
					}  finally {
						login(driver);
					}
									
				} else {
					String[] args2 = null;	
					driver.navigate().refresh();
					main(args2);
				}
			
			} catch(Exception f) {
				System.out.println("Login encerrado " + f);
			}				
		}
	}
	
	public static void deleteLine() {
		String file = ("C:\\VENDAS\\CONTRATOS.txt");
		
		List<String> lines = new ArrayList<>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			boolean firstLine = true;
			
			while((line = br.readLine()) != null) {
				if(firstLine) {
					firstLine = false;
					continue;
				}
				
				lines.add(line);
			}
		} catch (IOException e) {
            e.printStackTrace();
        }
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
			for(String line : lines) {
				bw.write(line);
				bw.newLine();
			}
			
		}catch (IOException e) {
            e.printStackTrace();
        }
	
    }
		
	
	public static void login(WebDriver driver) {
		try {

			// LOGIN
			WebElement userName = driver.findElement(By.id("userLogin__input"));
			userName.sendKeys("LOGIN");

			// SENHA
			WebElement userPassword = driver.findElement(By.id("userPassword__input"));
			userPassword.sendKeys("SENHA");

			driver.findElement(By.className("primary")).click();

		} catch (Exception e) {
				
			try {
				String file = ("C:\\VENDAS\\CONTRATOS.txt");

				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				
				if(line.equals("")) {
					driver.close();	
				} else {
					String[] args2 = null;	
					driver.navigate().refresh();
					main(args2);
				}
			
			} catch(Exception f) {
				System.out.println("Login encerrado");
			} 
					
		} 
	}

	public static void search(WebDriverWait wait, String CpfCnpj, WebDriver driver, String contrato)
			throws ElementNotInteractableException {
		// LOCALIZAR O CLIENTE

		try {
			WebElement inputField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("inputSearch")));

			inputField.sendKeys(CpfCnpj);

			driver.findElement(By.name("inputSearch")).click(); 
			driver.findElement(By.className("search-svg-container")).click();
		} catch (Exception e) {
			System.out.println("Exception 1 " + CpfCnpj.toString());
			finallyError(CpfCnpj, contrato, driver);
		}

	}

	public static void product(WebDriverWait wait, WebDriver driver, String contrato, String CpfCnpj) {

		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading-full")));

			WebElement menuDiv = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//div[contains(@class, 'show-menu-client')]")));

			wait.until(ExpectedConditions.visibilityOf(menuDiv));
			wait.until(ExpectedConditions.elementToBeClickable(menuDiv));
			menuDiv.click();

			WebElement menuRenegotiation = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.cssSelector("div.session-menu.col-md-2.ng-star-inserted ul li.ng-star-inserted a")));

			menuRenegotiation.click();
		} catch (TimeoutException e) {
			driver.navigate().refresh();
			login(driver);
		}

	}

	public static void dealQuery(WebDriverWait wait, WebDriver driver, String contrato, String CpfCnpj, File save, Instant Started) {

		try {
				
			List<WebElement> dealAll = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("em.icon-seta-baixo")));
			
			WebElement  dealGet2 = dealAll.get(2);	 

			
			for (WebElement deal : dealAll) {
				try {
					while(deal.equals(dealGet2)) {
						((JavascriptExecutor) driver).executeScript("arguments[0].click();", deal);
						break;
					}	
				} catch (Exception e) {
					String[] args2 = null;	
					driver.navigate().refresh();
					main(args2);
					finallyError(CpfCnpj, contrato, driver);
				} 
			}
		} catch (Exception e) {
			finallyError(CpfCnpj, contrato, driver);
		}

		try {
			List<WebElement> tr = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(@class, 'spaui-radiobutton-icon')]")));

			int n = 1;

			while (n > 0) {

				for (WebElement searchTr : tr) {

					((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchTr);

					WebElement idOf = wait
							.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordioAcordosPendentes")));

					List<WebElement> TagName = idOf.findElements(By.tagName("td"));

					List<WebElement> tr2 = idOf
							.findElements(By.xpath("//span[contains(@class, 'spaui-radiobutton-icon')]"));

					try {
						WebElement onlyContract = wait.until(ExpectedConditions.presenceOfElementLocated(
								By.xpath("//tr[td/div[contains(text(), '" + contrato + "')]]")));

						String onlyContractText = onlyContract.getText();

						if (onlyContractText.contains("Liquidado")) {
							n = 0;
							notFoundLiquidated(CpfCnpj, contrato, save, driver, wait);
							break;
						} else if (onlyContractText.contains("Cancelado")) {
							n = 0;
							notFoundCanceled(CpfCnpj, contrato, save, driver, wait);
							break;
						} else {

							if (onlyContractText.contains(contrato)) {

								int play = 2;
								int finalPlay = 0;

								String TagNameGet = TagName.get(3).getText();

								for (int i = 2; play > finalPlay; i++) {
									JavascriptExecutor js = (JavascriptExecutor) driver;

									for (WebElement element : tr2) {
										js.executeScript("arguments[0].click();", element);

										if (TagNameGet.contains(contrato)) {

											n = 0;

											try {
												WebElement buttonContract = wait
														.until(ExpectedConditions.presenceOfElementLocated(
																By.xpath("//button[text()='CONSULTAR PRODUTO']")));

												wait.until(ExpectedConditions.visibilityOf(buttonContract));
												wait.until(ExpectedConditions.elementToBeClickable(buttonContract));

												((JavascriptExecutor) driver).executeScript("arguments[0].click();",
														buttonContract);

												agreement(wait, driver, save, CpfCnpj, contrato, Started);

											} catch (Exception e) {
												System.out.println("Exception 4 " + CpfCnpj.toString());
											}  
											play = 0;
											break;
										} else {
											TagNameGet = TagName.get(i + 8).getText();
										}

									}

								}

							} else {
								finallyError(CpfCnpj, contrato, driver);
								n = 0;
								break;
							}
						}

						break;

					} catch (Exception e) {
						notFoundError(CpfCnpj, contrato, save, driver);
					}
					break;
				}
				break;
			}

		} catch (TimeoutException e) {
			finallyError(CpfCnpj, contrato, driver);

		} catch (Exception e) { 
			String[] args2 = null;	
			driver.navigate().refresh();
			finallyError(CpfCnpj, contrato, driver);
			main(args2);

		} finally {
			WebElement back = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("logotipo")));
			back.click();
		}

	}

	public static String convertStringValue(String text) {
		text = text.replaceAll("R\\$\\s*", "");
		text = text.replace(".", "").replace(",", ".");

		return text;
	}

	public static void notFoundLiquidated(String CpfCnpj, String contrato, File save, WebDriver driver,
			WebDriverWait wait) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(save, true))) {

			String nameLine = CpfCnpj + ";" + ";" + ";" + ";" + "SIM";
			
			bw.write(nameLine);
			bw.newLine();
			deleteLine();


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			WebElement back = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("logotipo")));
			back.click();

		}

	}

	public static void notFoundCanceled(String CpfCnpj, String contrato, File save, WebDriver driver,
			WebDriverWait wait) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(save, true))) {

			String nameLine = CpfCnpj + ";" + ";" + ";" + ";" + "SIM";

			bw.write(nameLine);
			bw.newLine();
			deleteLine();


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			WebElement back = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("logotipo")));
			back.click();
		}
	}

	public static void notFoundError(String CpfCnpj, String contrato, File save, WebDriver driver) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(save, true))) {

			String nameLine = CpfCnpj + ";" + ";" + ";" + ";" + "SIM";

			bw.write(nameLine);
			bw.newLine();
			deleteLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			WebElement back = driver.findElement(By.className("logotipo"));
			back.click();

		}

	}

	public static void finallyError(String CpfCnpj, String contrato, WebDriver driver) {

		File saveFinal = new File("C:\\VENDAS\\ERRO.txt");

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(saveFinal, true))) {
			String line = CpfCnpj + ";" + contrato;
			bw.write(line);
			bw.newLine();
			
			deleteLine();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			WebElement back = driver.findElement(By.className("logotipo"));
			back.click();
		}

	}
	
	public static void agreement(WebDriverWait wait, WebDriver driver, File save, String CpfCnpj, String contrato, Instant Started) {

		try {

			List<WebElement> dealText = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='row ng-star-inserted']//span")));

			WebElement webTipoPessoa = dealText.get(3);
			WebElement webNome = dealText.get(1);
			WebElement webIDProduto = dealText.get(6);
			WebElement webValorProduto = dealText.get(7);
			WebElement webStatus = dealText.get(8);

			String tipoPessoa = webTipoPessoa.getText();
			String nome = webNome.getText();
			Integer idProduto = Integer.parseInt(webIDProduto.getText());
			Double valorProduto = Double.parseDouble(webValorProduto.getText());
			String status = webStatus.getText();
			
			//------------
			
			WebElement allInstallments = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordionDetalhamentoParcelaAcordo")));
			wait.until(ExpectedConditions.visibilityOf(allInstallments));                          
			wait.until(ExpectedConditions.elementToBeClickable(allInstallments));
				
			
			List<WebElement> allbutton = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("em.icon-seta-baixo")));
			
			WebElement twobutton = allbutton.get(2);
			
			for(WebElement all : allbutton) {
				
				while(all.equals(twobutton)) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", all);
					break;
				} 
					
			}
	
			List<WebElement> TagTr = allInstallments.findElements(By.tagName("tr"));
							
			while(TagTr.size() == 1) {
				Thread.sleep(500);
				allInstallments = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordionDetalhamentoParcelaAcordo")));
				wait.until(ExpectedConditions.visibilityOf(allInstallments));                          
				wait.until(ExpectedConditions.elementToBeClickable(allInstallments));
				Thread.sleep(2000);
				TagTr = allInstallments.findElements(By.tagName("tr"));					
			}
					
		
		
			//------------

			
			Acordo deal = new Acordo(tipoPessoa, nome, idProduto, valorProduto, status);

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(save, true))) {

				String nameLine = deal.getTipoPessoa() + ";" + deal.getNome() + ";" + deal.getIdProduto() + ";"
						+ deal.getValorProduto() + ";" + deal.getStatus();

			    
				bw.write(nameLine);
				bw.newLine();
				
				deleteLine();

			} catch (IOException e) {
				String cpfcontrato = CpfCnpj.toString();
				finallyError(cpfcontrato, contrato, driver);

			} finally {
				WebElement back = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("logotipo")));
				wait.until(ExpectedConditions.visibilityOf(back));
				wait.until(ExpectedConditions.elementToBeClickable(back));
				back.click();
			}

		} catch (TimeoutException e) {
			finallyError(CpfCnpj, contrato, driver);
		} catch (NullPointerException e) {
			finallyError(CpfCnpj, contrato, driver);
		} catch (NumberFormatException e) {
			finallyError(CpfCnpj, contrato, driver);
		} catch (Exception e) { 
			String[] args2 = null;	
			driver.navigate().refresh();
			finallyError(CpfCnpj, contrato, driver);
			main(args2);
		} finally {
			WebElement back = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("logotipo")));
			wait.until(ExpectedConditions.visibilityOf(back));
			wait.until(ExpectedConditions.elementToBeClickable(back));
			back.click();
		}

	}
	
	
}
