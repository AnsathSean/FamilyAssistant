package com.FALineBot.EndPoint.Model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class Vocabulary {


	private Integer id;

    private String lineId;

    private String word;

    private List<String> definition;

    private List<String> exampleSentence;

    private Integer repetitions;

    private Float easeFactor;

    private LocalDate nextReviewDate;

    private LocalDate lastReviewDate;

    private String status;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private List<String> partOfSpeech;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}



	public Integer getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(Integer repetitions) {
		this.repetitions = repetitions;
	}

	public Float getEaseFactor() {
		return easeFactor;
	}

	public void setEaseFactor(Float easeFactor) {
		this.easeFactor = easeFactor;
	}

	public LocalDate getNextReviewDate() {
		return nextReviewDate;
	}

	public void setNextReviewDate(LocalDate nextReviewDate) {
		this.nextReviewDate = nextReviewDate;
	}

	public LocalDate getLastReviewDate() {
		return lastReviewDate;
	}

	public void setLastReviewDate(LocalDate lastReviewDate) {
		this.lastReviewDate = lastReviewDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<String> getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(List<String> partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

    
    public List<String> getDefinition() {
		return definition;
	}

	public void setDefinition(List<String> definition) {
		this.definition = definition;
	}

	public List<String> getExampleSentence() {
		return exampleSentence;
	}

	public void setExampleSentence(List<String> exampleSentence) {
		this.exampleSentence = exampleSentence;
	}

}
