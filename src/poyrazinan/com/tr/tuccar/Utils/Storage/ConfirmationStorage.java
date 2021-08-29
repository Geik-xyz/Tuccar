package poyrazinan.com.tr.tuccar.Utils.Storage;

public class ConfirmationStorage {
	
	ProductStorage productInfos;
	int buyAmount;
	boolean selfItem;
	
	public ConfirmationStorage(ProductStorage productInfos, int buyAmount, boolean selfItem) {
		this.productInfos = productInfos;
		this.buyAmount = buyAmount;
		this.selfItem = selfItem;
	}
	
	public ProductStorage getProduct() {
		return productInfos;
	}
	public int getBuyAmount() {
		return buyAmount;
	}
	public boolean isSelfItem() {
		return selfItem;
	}

}
