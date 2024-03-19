package deckOfCards;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/* This class represents a standard deck of 52 cards.
 * This class can be used to shuffle the cards and deal cards.
 * */
public class Deck {

	private ArrayList<Card> cards;
	
	//This constructor instantiates the list of Cards and populates it with the 52 cards in a deck
	public Deck() {
		cards = new ArrayList<Card>();
		
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				cards.add(new Card(rank, suit)); 
			}
		}
	}
	
	//This method shuffles the ArrayList of cards
	public void shuffle(Random randomNumberGenerator) {
		Collections.shuffle(cards, randomNumberGenerator);
	}
	
	//This method removes a card/deals a cards from the beginning of the deck of cards
	public Card dealOneCard() {
		return cards.remove(0); 
	}
	
}
