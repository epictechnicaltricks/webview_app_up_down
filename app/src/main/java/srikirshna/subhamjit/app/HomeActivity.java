package srikirshna.subhamjit.app;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class HomeActivity extends  AppCompatActivity  { 
	
	public final int REQ_CD_FP = 101;
	
	private RelativeLayout linear1;
	private SwipeRefreshLayout swiperefreshlayout1;
	private LinearLayout no_internet;
	private LinearLayout progress;
	private WebView webview2;
	private CardView cardview1;
	private LinearLayout linear2;
	private TextView textview3;
	private ImageView imageview1;
	private TextView textview1;
	private Button button1;
	//private LottieAnimationView lottie1;
	
	private RequestNetwork internet;
	private RequestNetwork.RequestListener _internet_request_listener;
	private Intent fp = new Intent(Intent.ACTION_GET_CONTENT);
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.home);
		initialize(_savedInstanceState);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		}
		else {
			initializeLogic();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		linear1 = findViewById(R.id.linear1);
		swiperefreshlayout1 = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout1);
		no_internet = (LinearLayout) findViewById(R.id.no_internet);
		progress = (LinearLayout) findViewById(R.id.progress);
		webview2 = findViewById(R.id.webview2);
		webview2.getSettings().setJavaScriptEnabled(true);
		webview2.getSettings().setSupportZoom(true);
		cardview1 = (CardView) findViewById(R.id.cardview1);
		linear2 = (LinearLayout) findViewById(R.id.linear2);
		textview3 = (TextView) findViewById(R.id.textview3);
		imageview1 = (ImageView) findViewById(R.id.imageview1);
		textview1 = (TextView) findViewById(R.id.textview1);
		button1 = (Button) findViewById(R.id.button1);
	//	lottie1 = (LottieAnimationView) findViewById(R.id.lottie1);
		internet = new RequestNetwork(this);
		fp.setType("image/*");
		fp.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		webview2.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView _param1, String _param2, Bitmap _param3) {
				final String _url = _param2;
				progress.setVisibility(View.VISIBLE);
				no_internet.setVisibility(View.GONE);
				internet.startRequestNetwork(RequestNetworkController.GET, _url, "null", _internet_request_listener);
				super.onPageStarted(_param1, _param2, _param3);
			}
			
			@Override
			public void onPageFinished(WebView _param1, String _param2) {
				final String _url = _param2;
				progress.setVisibility(View.GONE);
				if (Util.isConnected(getApplicationContext())) {
					swiperefreshlayout1.setVisibility(View.VISIBLE);
				}
				super.onPageFinished(_param1, _param2);
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				webview2.loadUrl(webview2.getUrl());
				Util.showMessage(getApplicationContext(), "Loading.");
			}
		});
		
		_internet_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				no_internet.setVisibility(View.GONE);
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				no_internet.setVisibility(View.VISIBLE);
				textview1.setText(_message);
				swiperefreshlayout1.setVisibility(View.GONE);
				Util.showMessage(getApplicationContext(), "No internet !");
			}
		};
	}
	
	private void initializeLogic() {
		
		webview2.setWebChromeClient(new WebChromeClient() {
			// For 3.0+ Devices
			protected void openFileChooser(ValueCallback uploadMsg, String acceptType) { mUploadMessage = uploadMsg; Intent i = new Intent(Intent.ACTION_GET_CONTENT); i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*"); startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
			}
			
			// For Lollipop 5.0+ Devices
			public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
				if (uploadMessage != null) {
					uploadMessage.onReceiveValue(null);
					uploadMessage = null; } uploadMessage = filePathCallback; Intent intent = fileChooserParams.createIntent(); try {
					startActivityForResult(intent, REQUEST_SELECT_FILE);
				} catch (ActivityNotFoundException e) {
					uploadMessage = null; Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show(); return false; }
				return true; }
			
			//For Android 4.1 only
			protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				mUploadMessage = uploadMsg; Intent intent = new Intent(Intent.ACTION_GET_CONTENT); intent.addCategory(Intent.CATEGORY_OPENABLE); intent.setType("image/*"); startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
			}
			
			protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				 Intent i = new Intent(Intent.ACTION_GET_CONTENT); i.addCategory(Intent.CATEGORY_OPENABLE);
				
				i.setType("*/*"); startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
			}
			
			
		});
		webview2.loadUrl("http://srikrishanbhajan.in/");
		webview2.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		no_internet.setVisibility(View.GONE);
		swiperefreshlayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				webview2.loadUrl(webview2.getUrl());
				swiperefreshlayout1.setRefreshing(false);
			}
		});
		_WebView(true, false, true, true, true, webview2);
		/*
_youtube_fullscreen_webview_2(webview2);
*/
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_FP:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
			}
			break;
			
			case REQUEST_SELECT_FILE:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				if (uploadMessage == null) return; uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(_resultCode, _data)); uploadMessage = null; }
			break;
			
			case FILECHOOSER_RESULTCODE:
			if (null == mUploadMessage){
				return; }
			Uri result = _data == null || _resultCode != RESULT_OK ? null : _data.getData(); mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
			
			if (true){
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (webview2.canGoBack()) {
			webview2.goBack();
		}
		else {
			finish();
		}
	}
	public void _WebView (final boolean _js, final boolean _zoom, final boolean _download, final boolean _html, final boolean _cookies, final WebView _view) {
		_view.getSettings().setJavaScriptEnabled(_js); //Made by Subhamjit 2023
		CookieManager.getInstance().setAcceptCookie(_cookies);
		WebSettings webSettings = _view.getSettings(); 
		webSettings.setJavaScriptEnabled(_html); 
		webSettings.setJavaScriptCanOpenWindowsAutomatically(_html);
		webSettings.setAllowFileAccessFromFileURLs(_html);
		webSettings.setAllowUniversalAccessFromFileURLs(_html);
		if(_zoom){
			_view.getSettings().setBuiltInZoomControls(true);_view.getSettings().setDisplayZoomControls(false);
		}
		else if(!_zoom){
			_view.getSettings().setBuiltInZoomControls(false);
			_view.getSettings().setDisplayZoomControls(false);
		}
		if(_download){
			
			_WebView_Download(webview2, FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS));
		}
		else if(!_download){
			showMessage("Downloads disabled!");
		}
	}
	

	public void _WebView_Download (final WebView _wview, final String _path) {

		try {
			if (FileUtil.isExistFile(_path)) {

			}
			else {
				FileUtil.makeDir(_path);
			}
			_wview.setDownloadListener(new DownloadListener() { public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) { DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url)); String cookies = CookieManager.getInstance().getCookie(url); request.addRequestHeader("cookie", cookies); request.addRequestHeader("User-Agent", userAgent); request.setDescription("Downloading file..."); request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype)); request.allowScanningByMediaScanner();



				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

				java.io.File aatv = new java.io.File(Environment.getExternalStorageDirectory().getPath() + "/Download");if(!aatv.exists()){if (!aatv.mkdirs()){ Log.e("TravellerLog ::","Problem creating Image folder");}} request.setDestinationInExternalPublicDir(_path, URLUtil.guessFileName(url, contentDisposition, mimetype));

				DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE); manager.enqueue(request); showMessage("Downloading File...."); BroadcastReceiver onComplete = new BroadcastReceiver() { public void onReceive(Context ctxt, Intent intent) { showMessage("Download Complete!"); unregisterReceiver(this); }}; registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); } });

		}catch (Exception e)
		{
			Toast.makeText(this, "Ple", Toast.LENGTH_SHORT).show();
		}

		}
	
	
	public void _youtube_videos_fullscreen_webview () {
	}
	
	
	public class CustomWebClient extends WebChromeClient {
		private View mCustomView;
		private WebChromeClient.CustomViewCallback mCustomViewCallback;
		protected FrameLayout frame;
		
		// Initially mOriginalOrientation is set to Landscape
		private int mOriginalOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		private int mOriginalSystemUiVisibility;
		
		// Constructor for CustomWebClient
		public CustomWebClient() {}
		
		public Bitmap getDefaultVideoPoster() {
			if (HomeActivity.this == null) {
				return null; }
			return BitmapFactory.decodeResource(HomeActivity.this.getApplicationContext().getResources(), 2130837573); }
		
		public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback viewCallback) {
			if (this.mCustomView != null) {
				onHideCustomView();
				return; }
			this.mCustomView = paramView;
			this.mOriginalSystemUiVisibility = HomeActivity.this.getWindow().getDecorView().getSystemUiVisibility();
			// When CustomView is shown screen orientation changes to mOriginalOrientation (Landscape).
			HomeActivity.this.setRequestedOrientation(this.mOriginalOrientation);
			// After that mOriginalOrientation is set to portrait.
			this.mOriginalOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			this.mCustomViewCallback = viewCallback; ((FrameLayout)HomeActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1)); HomeActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
		}
		
		public void onHideCustomView() {
			((FrameLayout)HomeActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
			this.mCustomView = null;
			HomeActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
			// When CustomView is hidden, screen orientation is set to mOriginalOrientation (portrait).
			HomeActivity.this.setRequestedOrientation(this.mOriginalOrientation);
			// After that mOriginalOrientation is set to landscape.
			this.mOriginalOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; this.mCustomViewCallback.onCustomViewHidden();
			this.mCustomViewCallback = null;
		}
	}
	
	{
	}
	
	
	public void _youtube_fullscreen_webview_2 (final WebView _view) {
		_view.setWebChromeClient(new CustomWebClient());
		
	}
	
	
	public void _transparentStatusAndNavigation () {
		if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
			    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
		}
		if (Build.VERSION.SDK_INT >= 19) {
			    getWindow().getDecorView().setSystemUiVisibility(
			            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
			                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
			    );
		}
		if (Build.VERSION.SDK_INT >= 21) {
			    setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
			    getWindow().setStatusBarColor(Color.TRANSPARENT);
			    getWindow().setNavigationBarColor(Color.TRANSPARENT);
		}
	}
	private void setWindowFlag(final int bits, boolean on) {
		    Window win = getWindow();
		    WindowManager.LayoutParams winParams = win.getAttributes();
		    if (on) {
			        winParams.flags |= bits;
			    } else {
			        winParams.flags &= ~bits;
			    }
		    win.setAttributes(winParams);
	}
	{
	}
	
	
	public void _extra () {
	}
	
	private ValueCallback<Uri> mUploadMessage;
	public ValueCallback<Uri[]> uploadMessage;
	public static final int REQUEST_SELECT_FILE = 100;
	
	private final static int FILECHOOSER_RESULTCODE = 1;
	
	public static final int
	REQUEST_IMAGE_CAPTURE = 1;
	
	
	
	{
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels(){
		return getResources().getDisplayMetrics().heightPixels;
	}
	
}
