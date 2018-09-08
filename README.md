# CryptoCoinView [![](https://jitpack.io/v/bxute/CryptoCoinView.svg)](https://jitpack.io/#bxute/CryptoCoinView)

An Android View which show data of cryptocurrency.

A Quick look:

<p align="center"><img src="https://user-images.githubusercontent.com/10809719/42724532-ba035c38-8791-11e8-9d81-0a675a813f18.png" width="360px" height="156px"/></p>

You can opt for 1636 coins available in the market.

It`s very easy to take into use.

**Step 1. Add it in your root build.gradle at the end of repositories**

```
allprojects {
  repositories {
	maven { url 'https://jitpack.io' }
  }
}
```	
**Step 2. Add the dependency**
```   
dependencies {
 implementation 'com.github.bxute:CryptoCoinView:v0.6'
}
```

**Step 3. Add XML to your view**
```xml
 <xute.cryptocoinview.CoinView
  android:id="@+id/coinViewSBD"
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```
We recommend you to keep the `width` as `match_parent` for a better view.

**Step 4. Instantiate in Activity**

```java
CoinView sbd = findViewById(R.id.coinViewSBD);
sbd.setCoinId(Coins.SBD);
```

That`s It 😀.

As a result you will get
<p align="center">
<img src="https://user-images.githubusercontent.com/10809719/42724595-d8477228-8792-11e8-867b-5d536afa6da1.png" width="320px" height="640px"/></p>
