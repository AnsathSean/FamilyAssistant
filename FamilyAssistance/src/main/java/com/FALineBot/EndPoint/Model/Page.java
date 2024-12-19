package com.FALineBot.EndPoint.Model;

import javax.persistence.Entity;

@Entity
public class Page {

	
	public int currentPage;
	public int totalPages;
	public boolean hasNext;
	public boolean hasBeofre;
	
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}
	public boolean isHasBeofre() {
		return hasBeofre;
	}
	public void setHasBeofre(boolean hasBeofre) {
		this.hasBeofre = hasBeofre;
	}
	

}
