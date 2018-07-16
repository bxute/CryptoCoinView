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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/*
 * Custom View which is responsible for fetching data from api and showing it.
 * */
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
  private RateCallback rateCallback;

  public CoinView(@NonNull Context context) {
    super(context);
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

  public CoinView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public CoinView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public void setCoinId(int id) {
    this.coinId = id;
    fetchData();
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

  /**
   * Binds all the data to view.
   *
   * @param dataModel data to be bound
   */
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

  /**
   * Helper method to change the color of rate green/red according to % change.
   * -0.12% will be red.
   * 1.23 % will be green.
   *
   * @param rate Exchange rate
   */
  private void setTextColorToRate(double rate) {
    percentageChange.setText(String.format("%s %%)", String.format(Locale.US, "(%.2f", rate)));
    if (rate < 0) {
      percentageChange.setTextColor(Color.parseColor("#d94040"));
    } else {
      percentageChange.setTextColor(Color.parseColor("#5cb85c"));
    }
  }

  /**
   * Formats large digits currency to small form. like [1234 -> 1.23K] [12345678 -> 1.2 M]
   *
   * @param num number to be formatted into short currency form.
   * @return Formatted String from large digit
   */
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

  /**
   * Helper method for counting digits len.
   *
   * @param mc number whose digits to be calculated.
   * @return length of digit
   */
  private int getDecimals(double mc) {
    int len = 0;
    while ((int) mc != 0) {
      mc /= 10;
      len++;
    }
    return len;
  }

  public void setRateCallback(RateCallback rateCallback) {
    this.rateCallback = rateCallback;
  }

  public interface RateCallback {
    void onRate(double rate);
  }
}
