package com.hjf.uhf_r2000.model;

public class PojoCard {
	private String content;
	private int count;

	@Override
	public String toString() {
		return "PojoCard [content=" + content + ", count=" + count + "]";
	}

	public PojoCard() {
		super();
	}

	public PojoCard(String content, int count) {
		super();
		this.content = content;
		this.count = count;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
