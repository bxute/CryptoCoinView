package xute.cryptocoinview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class CoinView extends FrameLayout implements NetworkUtils.NetworkResponseCallback {
		private ImageView coinImage;
		private TextView coinName;
		private TextView usdRate;
		private TextView percentageChange;
		private TextView rank;
		private TextView marketCap;
		private TextView volume;
		private ImageView refreshBtn;
		private ProgressBar priceProgress;
		private ProgressBar rankProgress;
		private ProgressBar mcProgress;
		private ProgressBar volumeProgess;
		private NetworkUtils networkUtils;
		private Context mContext;
		private int coinId;
		private Handler mHandler;

		public CoinView(@NonNull Context context) {
				super(context);
				init(context);
		}

		public CoinView(@NonNull Context context, @Nullable AttributeSet attrs) {
				super(context, attrs);
				init(context);
		}

		public CoinView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
				super(context, attrs, defStyleAttr);
				init(context);
		}

		private void init(Context context) {
				this.mContext = context;
				networkUtils = new NetworkUtils();
				mHandler = new Handler();
				networkUtils.setNetworkResponseCallback(this);
				View view = LayoutInflater.from(mContext).inflate(R.layout.coin_view, this);
				coinImage = view.findViewById(R.id.coin_image);
				coinName = view.findViewById(R.id.coin_name);
				usdRate = view.findViewById(R.id.usd_rate);
				percentageChange = view.findViewById(R.id.percent_change);
				rank = view.findViewById(R.id.rank);
				marketCap = view.findViewById(R.id.market_cap);
				volume = view.findViewById(R.id.volume);
				refreshBtn = view.findViewById(R.id.refreshBtn);
				priceProgress = view.findViewById(R.id.price_progressBar);
				rankProgress = view.findViewById(R.id.rank_progressBar);
				mcProgress = view.findViewById(R.id.mc_progressBar);
				volumeProgess = view.findViewById(R.id.volume_progressBar);
				refreshBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
								fetchData();
						}
				});
		}

		public void setCoinId(int id) {
				this.coinId = id;
				fetchData();
		}

		private void fetchData() {
				priceProgress.setVisibility(VISIBLE);
				rankProgress.setVisibility(VISIBLE);
				mcProgress.setVisibility(VISIBLE);
				volumeProgess.setVisibility(VISIBLE);
				usdRate.setVisibility(GONE);
				rank.setVisibility(GONE);
				marketCap.setVisibility(GONE);
				volume.setVisibility(GONE);
				percentageChange.setVisibility(GONE);
				String url = String.format(Locale.US, "%s%d", Constants.BASE_DATA_URL, coinId);
				networkUtils.request(url, "GET", "");
		}

		private void bindData(DataModel dataModel) {
				try {
						priceProgress.setVisibility(GONE);
						rankProgress.setVisibility(GONE);
						mcProgress.setVisibility(GONE);
						volumeProgess.setVisibility(GONE);

						usdRate.setVisibility(VISIBLE);
						rank.setVisibility(VISIBLE);
						marketCap.setVisibility(VISIBLE);
						volume.setVisibility(VISIBLE);
						percentageChange.setVisibility(VISIBLE);

						String imageUrl = String.format(Locale.US, Constants.COIN_IMAGE_SKELETON, coinId);
						Glide.with(mContext)
								.load(imageUrl)
								.into(coinImage);
						coinName.setText(String.format("%s (%s)", dataModel.getName(), dataModel.getSymbol()));
						rank.setText(String.valueOf(dataModel.getRank()));
						setTextColorToRate(dataModel.getPercentageChange());
						usdRate.setText(String.format(Locale.US, "%.2f USD", dataModel.getPrice()));
						volume.setText(String.format("$ %s", getFormattedPrice(dataModel.getVolume())));
						marketCap.setText(String.format("$ %s", getFormattedPrice(dataModel.getMarketcap())));
						if (rateCallback != null) {
								rateCallback.onRate(dataModel.getPrice());
						}
				}
				catch (Exception e) {

				}
		}

		@Override
		public void onResponse(String response) {
				parseJson(response);
		}

		@Override
		public void onError(String e) {
		}

		private void parseJson(String response) {
				final DataModel dataModel = new DataModel();
				try {
						JSONObject rootObj = new JSONObject(response);
						JSONObject dataObj = rootObj.getJSONObject("data");
						dataModel.setName(dataObj.getString("name"));
						dataModel.setSymbol(dataObj.getString("symbol"));
						dataModel.setRank(dataObj.getInt("rank"));

						JSONObject usdBlock = dataObj
								.getJSONObject("quotes")
								.getJSONObject("USD");
						dataModel.setPrice(usdBlock.getDouble("price"));
						dataModel.setMarketcap(usdBlock.getLong("market_cap"));
						dataModel.setPercentageChange(usdBlock.getDouble("percent_change_24h"));
						dataModel.setVolume(usdBlock.getLong("volume_24h"));
				}
				catch (JSONException e) {
						Log.d("Response", "JSON Exc" + e.toString());
				}
				mHandler.post(new Runnable() {
						@Override
						public void run() {
								bindData(dataModel);
						}
				});
		}

		private int getDecimals(double mc) {
				int len = 0;
				while ((int) mc != 0) {
						mc /= 10;
						len++;
				}
				return len;
		}

		private String getFormattedPrice(double num) {
				int len = getDecimals(num);
				if (len < 4) {
						return num + "";
				} else if ((len >= 4) && (len <= 6)) {
						return String.format(Locale.US, "%.2f K", num / 1000);
				} else if ((len >= 7) && (len <= 9)) {
						return String.format(Locale.US, "%.2f M", num / 1000000);
				} else if ((len >= 10) && (len <= 12)) {
						return String.format(Locale.US, "%.2f B", num / 1000000000);
				}
				return "";
		}

		private void setTextColorToRate(double rate) {
				percentageChange.setText(String.format("%s %%)", String.format(Locale.US, "(%.2f", rate)));
				if (rate < 0) {
						percentageChange.setTextColor(Color.parseColor("#d94040"));
				} else {
						percentageChange.setTextColor(Color.parseColor("#5cb85c"));
				}
		}

		private RateCallback rateCallback;

		public void setRateCallback(RateCallback rateCallback) {
				this.rateCallback = rateCallback;
		}

		public interface RateCallback {
				void onRate(double rate);
		}
}
