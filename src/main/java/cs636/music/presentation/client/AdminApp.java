package cs636.music.presentation.client;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import cs636.music.service.CatalogService;
import cs636.music.service.SalesService;
import cs636.music.service.ServiceException;
import cs636.music.service.data.InvoiceData;

/**
 * 
 * Line-oriented client code for Music administrator user. To be replaced by JSP pages
 * later. 
 * 
 */
/**
 * @author AshleyFarrell
 * 
 */
public class AdminApp {

	private SalesService salesService;
	private CatalogService catalogService;
	
	private static Scanner in;
	
	public AdminApp(CatalogService catalogService, SalesService salesService) {
		this.catalogService = catalogService;
		this.salesService = salesService;	
		in = new Scanner(System.in);
	}
	
	public void loginPage() throws IOException, ServiceException {
		System.out.println("---Login Page---");
		String username = PresentationUtils.readEntry(in,"User Name _ ");
		String password = PresentationUtils.readEntry(in,"Password _ ");
		if (salesService.checkAdminLogin(username, password)){
			handleMainPage();
		} else {
			System.out.println(" User name and password did not match !!");
		}
	}
	
	public void handleMainPage() throws IOException, ServiceException {
		String command = null;
		while (true) {
			System.out.println("---Main Page---");
			System.out.println(" P: Process Invoice ");
			System.out.println(" R: Display Report ");
			System.out.println(" I: Initialize Database ");
			System.out.println(" Q: Quit ");
			command = PresentationUtils.readEntry(in,"Please Enter the Command");
			if (command.equalsIgnoreCase("P")) {
				this.processInvoice();
			} else if (command.equalsIgnoreCase("R")) {
				this.displayReports();
			} else if (command.equalsIgnoreCase("I")) {
				this.salesService.initializeDB();
			} else if (command.equalsIgnoreCase("Q")) {
				return;
			} else {
				System.out.println("Invalid Command! Try again...");
			}
		}
	}
	
	public void processInvoice() throws IOException, ServiceException {
		String command = null;
		while (true) {
			System.out.println("---Process Invoice Page---");
			Set<InvoiceData> invoices = salesService.getListofUnprocessedInvoices();
			if (invoices.isEmpty()) {
				System.out.println("No unprocessed invoices exist, returning to main page");
				return;
			}
			PresentationUtils.displayInvoices(this.salesService.getListofUnprocessedInvoices(), System.out);
			System.out.println(" I: Choose Invoice to Process");
			System.out.println(" B: Back to Main Page ");
			command = PresentationUtils.readEntry(in,"Please Enter the Command");
			if (command.equalsIgnoreCase("I")) {
				int invoice_id = Integer.parseInt(PresentationUtils.readEntry(in, "invoice id"));
				System.out.println(" Processing invoice " + invoice_id + " .....");
				this.salesService.processInvoice(invoice_id);
			} else if (command.equalsIgnoreCase("B")) {
				return;
			} else {
				System.out.println("Invalid Command! Try again...");
			}
		}
	}
	
	public void displayReports() throws IOException, ServiceException {
		String command = null;
		while (true) {
			System.out.println("---Display Reports Page---");
			System.out.println(" I: List of All Invoices To Process");
			System.out.println(" D: List of All Downloads");
			System.out.println(" B: Back to Main Page ");
			command = PresentationUtils.readEntry(in,"Please Enter the Command");
			if (command.equalsIgnoreCase("I")) {
				PresentationUtils.displayInvoices(this.salesService.getListofInvoices(),System.out);
			} else if (command.equalsIgnoreCase("D")) {	
				PresentationUtils.downloadReport(catalogService.getListofDownloads(), System.out);
			} else if (command.equalsIgnoreCase("B")) {
				return;
			} else {
				System.out.println("Invalid Command! Try again...");
			}
		}
	}
}
