package xute.cryptocoinviewexample;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
	* Example local unit test, which will execute on the development machine (host).
	*
	* @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
	*/
public class ExampleUnitTest {
		@Test
		public void addition_isCorrect() {
				double mc = 1234567890;
				//168 - same
				//1234 - 1.23K
				//12345 - 12.34K
				//123456 - 123.46K
				//172331
				//1234567 - 1.23 M
				//12345678 - 12.34 M
				//123456789 - 123.45 M

				//1234567890 - 1.23 B
				int len = getDecimals(mc);
				System.out.println("String " + getFormattedString(mc, len));
		}

		private int getDecimals(double mc) {
				int len = 0;
				while ((int) mc != 0) {
						mc /= 10;
						len++;
				}
				return len;
		}

		public String getFormattedString(double num, int len) {
				if (len < 4) {
						return num + "";
				} else if ((len >= 4) && (len <= 6)) {
						return String.format(Locale.US, "%.2f K", num / 1000);
				} else if ((len >= 7) && (len <= 9)) {
						return String.format(Locale.US, "%.2f M", num / 1000000);
				} else if((len >= 10) && (len <= 12)){
						return String.format(Locale.US, "%.2f B", num / 1000000000);
				}
				return "";
		}
}