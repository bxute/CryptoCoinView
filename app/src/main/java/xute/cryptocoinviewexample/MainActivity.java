package xute.cryptocoinviewexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import xute.cryptocoinview.CoinView;
import xute.cryptocoinview.Coins;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    CoinView sbd = findViewById(R.id.coinViewSBD);
    sbd.setCoinId(Coins.STEEM);
  }
}
