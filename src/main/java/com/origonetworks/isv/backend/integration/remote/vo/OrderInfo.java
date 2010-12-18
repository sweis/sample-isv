package com.origonetworks.isv.backend.integration.remote.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "order")
public class OrderInfo implements Serializable{
	private static final long serialVersionUID = -6727466858109325508L;

	private String editionCode;
	private List<OrderItemInfo> items = new ArrayList<OrderItemInfo>();

	public String getEditionCode() {
		return editionCode;
	}

	public void setEditionCode(String editionCode) {
		this.editionCode = editionCode;
	}

	@XmlElement(name = "item")
	public List<OrderItemInfo> getItems() {
		return items;
	}

	public void setItems(List<OrderItemInfo> items) {
		this.items = items;
	}
}
