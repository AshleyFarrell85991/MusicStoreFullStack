package cs636.music.domain;


public class CartItem {

	private long productId;
	private int quantity;
	public CartItem() {}
	
	public CartItem (long productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}
	
	public long getProductId() {
		return productId;
	}

	public void setProductId(long id) {
		this.productId = id;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
