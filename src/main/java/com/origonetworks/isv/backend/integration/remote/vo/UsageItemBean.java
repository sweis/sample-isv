package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.origonetworks.isv.backend.integration.remote.type.PricingUnit;

@XmlRootElement(name = "usageItem")
public class UsageItemBean implements Serializable {
	private static final long serialVersionUID = 7935998093136955903L;

	private PricingUnit unit;
	private int quantity;

	public PricingUnit getUnit() {
		return unit;
	}

	public void setUnit(PricingUnit unit) {
		this.unit = unit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
