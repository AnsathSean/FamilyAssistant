package com.FALineBot.EndPoint.Model;

import java.time.LocalDate;

import javax.persistence.Entity;

@Entity
public class Vocabulary {

    private Integer id;

    private String lineId;

    private String word;

    private String definition;

    private String exampleSentence;

    private Integer repetitions;

    private Float easeFactor;

    private LocalDate nextReviewDate;

    private LocalDate lastReviewDate;

    private String status;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private String partOfSpeech;

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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getExampleSentence() {
		return exampleSentence;
	}

	public void setExampleSentence(String exampleSentence) {
		this.exampleSentence = exampleSentence;
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

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
    
    

}
