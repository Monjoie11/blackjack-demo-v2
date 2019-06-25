package edu.cnm.deepdive.blackjackdemo.controller;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.cnm.deepdive.blackjackdemo.R;
import edu.cnm.deepdive.blackjackdemo.model.Card;
import edu.cnm.deepdive.blackjackdemo.model.Hand;
import edu.cnm.deepdive.blackjackdemo.view.HandAdapter;
import edu.cnm.deepdive.blackjackdemo.viewmodel.MainViewModel;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final int DECKS_IN_SHOE = 6;


  private RecyclerView handView;
  private HandAdapter handAdapter;
  private int adapterSize;
  private MainViewModel model;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupToolbar();
    setupFloatingActionButton();
    setupRecyclerView();
    setupViewModel();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.shuffle_deck:
        model.shuffle();
        break;
      case R.id.deal_hand:
        model.initHand();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void setupFloatingActionButton() {
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener((view) -> model.draw(1));
  }

  private void setupRecyclerView() {
    Resources res = getResources();
    handView = findViewById(R.id.hand_view);
    handView.addItemDecoration(new HandAdapter.OverlapDecoration(
        (int) res.getDimension(R.dimen.card_horizontal_spacing),
        (int) res.getDimension(R.dimen.card_vertical_spacing)));
    handView.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupViewModel() {
    model = ViewModelProviders.of(this).get(MainViewModel.class);
    model.getDeck().observe(this, (deck) -> model.initHand());
    model.getHand().observe(this, this::setupHand);
    model.getCards().observe(this, this::updateCards);
    Hand hand = model.getHand().getValue();
    if (model.getHand().getValue() == null) {
      model.initDeck(DECKS_IN_SHOE); //TODO Can we avoid doing this?
    } else {
      setupAdapter(hand);
      adapterSize = hand.getCards().size();
    }
  }

  private void setupHand(Hand hand) {
    setupAdapter(hand);
    model.draw(2);
  }

  private void setupAdapter(Hand hand) {
    handAdapter = new HandAdapter(this, hand.getCards());
    handView.setAdapter((handAdapter));
    adapterSize = 0;
  }

  private void updateCards(List<Card> cards) {
    handAdapter.notifyItemRangeInserted(adapterSize, cards.size() - adapterSize);
    adapterSize = cards.size();
  }


}
