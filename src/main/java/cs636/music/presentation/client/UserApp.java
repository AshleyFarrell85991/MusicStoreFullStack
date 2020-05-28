package cs636.music.presentation.client;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import cs636.music.domain.Cart;
import cs636.music.domain.Product;
import cs636.music.domain.Track;
import cs636.music.service.CatalogService;
import cs636.music.service.SalesService;
import cs636.music.service.ServiceException;
import cs636.music.service.data.CartItemData;
import cs636.music.service.data.InvoiceData;
import cs636.music.service.data.UserData;


/* @author AshleyFarrell */

public class UserApp {



	private CatalogService catalogService;
	private SalesService salesService;

	static private Scanner in; // the user input source

	private UserData user = null; // the user once registered (immutable part of user data)
	private Cart cart = null; // the shopping cart for the user
	public UserApp(CatalogService catalogService, SalesService salesService) {
		this.catalogService = catalogService;
		this.salesService = salesService;		
	}
	
	public void handleCatalogPage() throws IOException, ServiceException {
		Set<Product> prodlist;
		Product prod = null;
		while (true) {
			System.out.println("---Catalog Page---");
			prodlist = this.catalogService.getProductList();
			System.out.println("Displaying list of CDs: product codes and descriptions");
			PresentationUtils.displayCDCatlog(prodlist, System.out);
			String productCode = PresentationUtils.readEntry(in,"Enter the product code of product to see info, or S to show cart or Q to quit");
			if (productCode.equalsIgnoreCase("S")) {
				if (cart == null)
					cart = catalogService.createCart();
				handleCartPage(0); 
				continue;
			} else if (productCode.equalsIgnoreCase("Q"))
				return;
		
			prod = this.catalogService.getProductByCode(productCode);
			if (prod != null) {
				handleProduct(prod.getId());
			}
			else {
				System.out.println(" Can't find the product: " + productCode + " !!!");
			}
				
		}
	}


	public void handleProduct(long productId) throws IOException, ServiceException{
		
		boolean listening = false;
		

		while (true) {
			Product product = catalogService.getProduct(productId); 
			if (listening) {
				System.out.println("---Sound Page---");
				System.out.println(" Displaying list of tracks: track# and title");
				PresentationUtils.displayTracks(product, System.out);			
			} else {
				System.out.println("---Product Page--");
				System.out.println("displaying product info for this product");
				PresentationUtils.displayProductInfo(product, System.out);
			}

			String command = null;
			
			if (cart == null)
				cart = this.catalogService.createCart();
			System.out.println("Possible Commands");
			if (listening){
				System.out.println("T: Track# to play");
			}
			System.out.println("A: Add CD to Cart");
			System.out.println("S: Show Cart - has other options like CheckOut");
			if (!listening) {
				System.out.println("L: Listen to Sample ");
			}
			System.out.println("B: Browse Catalog");

			command = PresentationUtils.readEntry(in,"Please Enter the Command");

			if (command.equalsIgnoreCase("A")) {
				// add this CD to cart
				handleCartPage(productId);
				return; // redisplay whole catalog after add-to-cart
			} else if (command.equalsIgnoreCase("S")) {
				// show cart
				handleCartPage(0);
				return; // redisplay whole catalog after display of cart

			} else if (command.equalsIgnoreCase("L") && (!listening)) {
				// creating User if necessary (required for download)
				if (user == null)
				   user = userUI(); // do simple registration if needed
				listening = true; // switch this method to the Sound page

			} else if (command.equalsIgnoreCase("T") && listening) {
				String trackNumberString = PresentationUtils.readEntry(in,
						"Please enter track# to download & play");
				int trackNumber = Integer.parseInt(trackNumberString);
				System.out.println("getting Track " + trackNumber	
				 	+ ", recording download and fake-playing track");
				// user stays on Sound page (listening == true)
				// Get fresh product object (really not needed if products are immutable)
				product = catalogService.getProduct(productId); 
				Track track = product.findTrackByNumber(trackNumber);
				if (track != null) {
					PresentationUtils.playTrack(track, System.out);
					catalogService.addDownload(user.getEmailAddress(), track);
				} else{
					System.out.println("Can't find track #" + trackNumber);
				}
			} else if (command.equalsIgnoreCase("B")) {  // browse command
				return;  // back to catalog page
			} else
				System.out.println("Invalid Command: " + command + "! Try again...");
		}
	}



	public void handleCartPage(long productId) throws IOException, ServiceException {
		System.out.println("---Cart Page---");
		// if newProduct is non-null, adding it to Cart
		if (productId > 0){
			this.catalogService.addItemtoCart(productId, cart, 1);
		}

		while (true) {
			System.out.println("displaying Cart");
			Set<CartItemData> cartInfo = catalogService.getCartInfo(cart);
			PresentationUtils.displayCart(cartInfo, System.out);
			System.out.println("Possible Commands");
			System.out.println("C: Change to Cart");
			System.out.println("R: Remove from Cart ");
			System.out.println("O: Check Out ");
			System.out.println("B: Browse Catalog");
			String command = PresentationUtils.readEntry(in,
					"Please Enter the Command");

			if (command.equalsIgnoreCase("C")) {
				String productCode = PresentationUtils.readEntry(in,
						"Enter the product code of product to change");

				int quantity = Integer.parseInt(PresentationUtils.readEntry(in,
						"Enter the new quantity of the product"));
				System.out.println("calling service layer to update Cart for product"
								+ productCode + ", quantity " + quantity);
				Product prd = catalogService.getProductByCode(productCode);
				if (prd == null) {
					System.out.println(" Can't find the product: " + productCode + " !!!");
				} else {
					catalogService.changeCart(productId, cart, quantity);
				}
			} else if (command.equalsIgnoreCase("R")) {
				String productCode = PresentationUtils.readEntry(in,
						"Enter the product code for removal");
				System.out.println("calling service layer to remove product "
						+ productCode + " from cart");
				Product prd = catalogService.getProductByCode(productCode);
				if (prd == null) {
					System.out.println(" Can't find the product: " + productCode + " !!!");
				} else {
					catalogService.removeCartItem(productId, cart);
				}
			} else if (command.equalsIgnoreCase("O")) {
				handleCheckOut();
				return;
			} else if (command.equalsIgnoreCase("B")) {
				return;
			} else
				System.out.println("Invalid Command!");
		}
	}

	public void handleCheckOut() throws IOException, ServiceException {
		if (cart.getItems().size()>0)
		{		
			System.out.println("---CheckOut Page---");			
			if (user == null)
				user = userUI();
			String address = PresentationUtils.readEntry(in, "Address for order");
			salesService.addUserAddress(user.getId(), address);
			System.out.println("\nCreating order for cart items:");
			Set<CartItemData> cartInfo = catalogService.getCartInfo(cart);
			PresentationUtils.displayCart(cartInfo, System.out);
			System.out.println("calling service layer to checkout");
	
			InvoiceData newInvoice = salesService.checkout(cart, user.getId());
			System.out.println("\nThank you for your order, your cart is now empty.\n");
			PresentationUtils.displayInvoice(newInvoice, System.out);			
		} else {
			System.out.println("The cart is empty!!");
		}
		
	}

	public UserData userUI() throws IOException, ServiceException{
		System.out.println("---User Registration Page---");
		String fName = PresentationUtils.readEntry(in,
				"\n Give us a few registration details:\n\t\t FirstName");
		String lName = PresentationUtils.readEntry(in, "\n\t\t LastName");
		String eMail = PresentationUtils.readEntry(in, "\n\t\t Email");
		System.out.println("calling service layer to register user: " + fName
				+ " " + lName + " " + eMail);
		// Note: creating User object in service layer, not here!
		salesService.registerUser(fName,lName,eMail);
		UserData user = salesService.getUserInfoByEmail(eMail);
		PresentationUtils.displayUserInfo(user, System.out);
		return user;
	}

}
