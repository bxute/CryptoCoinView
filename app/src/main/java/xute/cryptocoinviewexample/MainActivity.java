package xute.cryptocoinviewexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xute.cryptocoinview.CoinView;
import xute.cryptocoinview.Coins;
import xute.cryptocoinview.NetworkUtils;

public class MainActivity extends AppCompatActivity{

		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				CoinView sbd = findViewById(R.id.coinViewSBD);
				sbd.setCoinId(Coins.SBD);
		}
}
