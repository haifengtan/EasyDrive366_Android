package cn.count.easydrive366.barcode;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ShowBarcodeActivity extends BaseHttpActivity {
	private Button button;
	private EditText editText;
	private ImageView imageView;
	private BitMatrix matrix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modules_barcode_showbarcode);
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		this.setRightButtonInVisible();
		
		initView();
		setListener();
	}

	private void initView() {
		button = (Button) findViewById(R.id.button);
		imageView = (ImageView) findViewById(R.id.code_img);
		editText = (EditText) findViewById(R.id.code_edit);
		// TODO Auto-generated method stub

	}

	private String str;

	private void setListener() {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				str = editText.getText().toString();
				Bitmap bitmap = null;
				try {
					if (str != null && !"".equals(str)) {
						bitmap = CreateCode(str);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		});

	}

	private Bitmap CreateCode(String content) throws WriterException,
			UnsupportedEncodingException {

		QRCodeWriter writer = new QRCodeWriter();

		//
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

		matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,
				300, 300, hints);

		int width = matrix.getWidth();
		int hight = matrix.getHeight();

		int[] pixels = new int[width * hight];
		for (int y = 0; y < hight; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap map = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
		map.setPixels(pixels, 0, width, 0, 0, width, hight);

		return map;

	}
}
