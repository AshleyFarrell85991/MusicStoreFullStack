package cs636.music.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Cart implements Serializable {

	private static final long serialVersionUID = 1L;
	private Set<CartItem> items;

	public Cart() {
		items = new HashSet<CartItem>();
	}
	

	public Set<CartItem> getItems() {
		return items;
	}
	

	public CartItem findItem(long productId) {
		for (CartItem i : items) {
			if (i.getProductId() == productId) {
				return i;
			}
		}
		return null;
	}

	public void addItem(CartItem item) {
		if (items == null) {
			items = new HashSet<CartItem>();
		}
		long prodId = item.getProductId();
		int quantity = item.getQuantity();

		for (CartItem ci : items) {

			if (ci.getProductId() == prodId) {
				ci.setQuantity(quantity);
				return;
			}
		}
	
		items.add(item);
	}

	public void removeItem(long productId) {

		for (CartItem ci : items) {
			if (ci.getProductId() == productId) {
				items.remove(ci);
				return;
			}
		}
	}

	public void clear() {
		items.clear();
	}
}