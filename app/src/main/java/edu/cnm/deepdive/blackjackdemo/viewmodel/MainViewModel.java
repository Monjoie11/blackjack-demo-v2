package edu.cnm.deepdive.blackjackdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.blackjackdemo.model.Card;
import edu.cnm.deepdive.blackjackdemo.model.Deck;
import edu.cnm.deepdive.blackjackdemo.model.Draw;
import edu.cnm.deepdive.blackjackdemo.model.Hand;
import edu.cnm.deepdive.blackjackdemo.service.DeckOfCardsService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MainViewModel extends ViewModel {

  private MutableLiveData<Deck> deck;
  private MutableLiveData<Hand> hand;
  private MutableLiveData<List<Card>> cards;

  public LiveData<Deck> getDeck() {
    if (deck == null) {
      deck = new MutableLiveData<>();
    }
    return deck;
  }

  public LiveData<Hand> getHand() {
    if (hand == null) {
      hand = new MutableLiveData<>();
    }
    return hand;
  }

  public LiveData<List<Card>> getCards() {
    if (cards == null) {
      cards = new MutableLiveData<>();
    }
    return cards;
  }

  public void initDeck(int numDecks) {
    DeckOfCardsService.getInstance().newDeck(numDecks)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(deck::postValue); // Add this to a disposable container
  }

  public void shuffle(){
    DeckOfCardsService.getInstance().shuffle(deck.getValue().getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(d -> initHand());//FIXME Add this to a disposable
  }

  public void draw(int numCards){
    DeckOfCardsService.getInstance().draw(deck.getValue().getId(), numCards)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addToHand);
  }

  public void initHand(){
    hand.postValue(new Hand());
  }

  private void addToHand(Draw draw){
    Hand hand = getHand().getValue();
    for(Card card: draw.getCards()) {
      hand.addCard(card);
    }
    cards.postValue(hand.getCards());
  }

}
