package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import deckOfCards.*;

/*
 * This class will create the deck and deal cards to the player and dealer.
 * This class is responsible for the basic function of the Blackjack game.
 * The class will evaluate a hand, assess the hand, assess the game, and assess whether the dealer should take a card.
 * This class is used by the GUI for the logic and structure of the game. 
 */

public class BlackjackModel {
		
	private ArrayList<Card> dealerCards; 
	private ArrayList<Card> playerCards; 
	private Deck deck; 
	
	//Get the dealer's cards
	public ArrayList<Card> getDealerCards() {
		return new ArrayList<Card>(dealerCards);
	}
	
	//Get the player's cards
	public ArrayList<Card> getPlayerCards() {
		return new ArrayList<Card>(playerCards);
	}
	
	//Set the dealer cards
	public void setDealerCards(ArrayList<Card> cards) {
		dealerCards = new ArrayList<Card>(cards); 
	}
	
	//Set the player's cards
	public void setPlayerCards(ArrayList<Card> cards) {
		playerCards = new ArrayList<Card>(cards);
	}
	
	//Create and shuffle the deck
	public void createAndShuffleDeck(Random random) {
		deck = new Deck();
		deck.shuffle(random);
	}
	
	//Deal two cards from the deck to the the dealer
	public void initialDealerCards() {
		dealerCards = new ArrayList<Card>();
		dealerCards.add(deck.dealOneCard());
		dealerCards.add(deck.dealOneCard());
	}
	
	//Deal two cards from the deck to the the player
	public void initialPlayerCards() {
		playerCards = new ArrayList<Card>();
		playerCards.add(deck.dealOneCard());
		playerCards.add(deck.dealOneCard()); 
	}
	
	//Deal one card from the deck to the dealer
	public void dealerTakeCard() {
		dealerCards.add(deck.dealOneCard()); 
	}
	
	//Deal one card from the deck to the player
	public void playerTakeCard() {
		playerCards.add(deck.dealOneCard()); 
	}
	
	//Evaluates the hand and returns an ArrayList that represents the 1 or 2 values that could be assigned to the hand
	public static ArrayList<Integer> possibleHandValues(ArrayList<Card> hand) {
		ArrayList<Integer> handValues = new ArrayList<Integer>(); //Holds the 1 or 2 possible hand values
		
		for (int i = 0; i < hand.size(); i++) { //Loops through the hand
			if (i == 0) { //If it's the first card, it's values should be added
				handValues.add(hand.get(i).getRank().getValue()); 
				if (hand.get(i).getRank().getValue() == 1) { //If the first card is an Ace, 11 should also be added
					handValues.add(11); 
				}
			} else { //If it is not the first card, values are either set or added to the list
				
				if (hand.get(i).getRank().getValue() == 1) {
					if (handValues.size() == 1) { //If Ace, either add 1 or 11 based on its value being more than 21
						int temp = handValues.get(0); 
						if (handValues.get(0) + 1 <= 21 && !handValues.contains(handValues.get(0) + 1)) {
							handValues.set(0, handValues.get(0) + 1);
						}
						if (temp + 11 <= 21 && !handValues.contains(temp + 11)) {
							handValues.add(temp + 11);
						}
					} else { //If Ace, either add/set 1 or 11 based on its value being more than 21
						for(int j = 0; j < handValues.size(); j++) { 
							if (handValues.get(j) + 1 <= 21 && !handValues.contains(handValues.get(j) + 1)) {
								handValues.set(j, handValues.get(j) + 1);
							} else if (handValues.get(0) + 11 <= 21 && !handValues.contains(handValues.get(j) + 11)) {
								handValues.set(j, handValues.get(j) + 11);
							}
						}
					} 
				} else { //If not an Ace, add the card's value to the 1 or 2 possible values
					handValues.set(0, hand.get(i).getRank().getValue() + handValues.get(0)); 
					if (handValues.size() != 1) {
						handValues.set(1, hand.get(i).getRank().getValue() + handValues.get(1)); 
					}
				}
				
					
			}
			
		}
		
		Collections.sort(handValues);
		
		//Check if the hand contains an ace
		boolean ace = false;
		for(Suit s : Suit.values()) {
			if(hand.contains(new Card(Rank.ACE, s))) {
				ace = true; 
			}
		}
		
		if(handValues.size() == 2 && ace) { //If the hand contains an ace and has two possible hand values
			if(handValues.get(0) > 21 && handValues.get(1) > 21) { 
				//If both the hand values are greater than 21, only keep the "lesser evil" value
				handValues.remove(1); 
			} else if (handValues.get(1) > 21) { 
				//If only one of the hand values is over 21, just remove it 
				handValues.remove(1); 
			}
		}
		
		return handValues; 
	}
	
	//This method will assess the hand and return a HandAssessment constant
	public static HandAssessment assessHand(ArrayList<Card> hand) {
		//If the hand is null or contains fewer than two cards--Insufficient Cards
		if(hand == null || hand.size() < 2) {
			return HandAssessment.INSUFFICIENT_CARDS; 
		}
		
		//Check if the hand is a bust if any value is greater than 21
		boolean bust = false;
		for (int i = 0; i < possibleHandValues(hand).size(); i++) {
			if(possibleHandValues(hand).get(i) > 21) {
				return HandAssessment.BUST; 
			}
		}
		
		//Check if there is an ace and card that's value is 10---Natural Blackjack
		boolean ace = false; //If the hand contains an Ace
		boolean ten = false; //If the hand contains a card that has a value of 10
		for(int i = 0; i < hand.size(); i++) {			
			if(hand.get(i).getRank().getValue() == 1) {
				ace = true;
			}
			if(hand.get(i).getRank().getValue() == 10) {
				ten = true;
			}
		}
		
		if (hand.size() == 2 && ace && ten) {
			return HandAssessment.NATURAL_BLACKJACK;
		} else {
			return HandAssessment.NORMAL;
		}
	}
	
	//This method will determine the outcome of the game and return a GameResult constant
	public GameResult gameAssessment() {
		HandAssessment pAssess = assessHand(playerCards); //Assess the player's hand
		HandAssessment dAssess = assessHand(dealerCards); //Assess the dealer's hand
		
		ArrayList<Integer> playerHandVals = possibleHandValues(playerCards); //Player's possible hand values
		ArrayList<Integer> dealerHandVals = possibleHandValues(dealerCards); //Dealer's possible hand values
		
		if (pAssess == HandAssessment.NATURAL_BLACKJACK && dAssess != HandAssessment.NATURAL_BLACKJACK) {
			return GameResult.NATURAL_BLACKJACK;
		} else if (pAssess == HandAssessment.NATURAL_BLACKJACK && dAssess == HandAssessment.NATURAL_BLACKJACK) {
			return GameResult.PUSH; 
		} else if (pAssess != HandAssessment.NATURAL_BLACKJACK) {
			if(pAssess == HandAssessment.BUST) {
				return GameResult.PLAYER_LOST;
			} else if (pAssess != HandAssessment.BUST && dAssess == HandAssessment.BUST) {
				return GameResult.PLAYER_WON; 
			} else if (pAssess != HandAssessment.BUST && dAssess != HandAssessment.BUST) {
				
				if (Collections.max(playerHandVals) > Collections.max(dealerHandVals)) {
					return GameResult.PLAYER_WON; 
				} else if (Collections.max(playerHandVals) < Collections.max(dealerHandVals)) {
					return GameResult.PLAYER_LOST; 
				} else if (Collections.max(playerHandVals) == Collections.max(dealerHandVals)) {
					return GameResult.PUSH;
				}
				
			}
		}
		return GameResult.PUSH;
	} 
	
	//This method is used once the payer has decided to "stay" (or has busted)
	//This method returns a boolean associated with whether or not the dealer should take more cards or not
	public boolean dealerShouldTakeCard() {
		
		//Check if the dealer's cards have an ace
		boolean ace = false; 
		for(int i = 0; i < dealerCards.size(); i++) {			
			if(dealerCards.get(i).getRank().getValue() == 1) {
				ace = true;
			}
		}
		
		ArrayList<Integer> handValues = possibleHandValues(dealerCards);
		if (handValues.get(handValues.size() - 1) <= 16) { 
			//If the dealer's hand is 16 or less, they must continue taking cards
			return true; 
		} else if (handValues.get(handValues.size() - 1) >= 18) { 
			//If the dealer's hand reaches 18 or more, they must stop taking cards
			return false; 
		} else if (ace && handValues.contains(7) && handValues.contains(17)) { 
			//If dealer's hand includes Ace & the hand value's could either be 7 or 17, they must take another card
			return true; 
		} else { 
			//If the dealer's hand is valued as 17 and could not be valued as 7, they must stop taking cards
			return false; 
		}
	}

}

