package in.net.pragya.infovault.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
/*
import java.security.Provider;
import java.security.Security;
import java.util.Map;
*/
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CryptoActivity extends Activity
{
	private TextView tvKeyComplexity = null;
	private SeekBar seekKeyComplexity = null;
	private EditText edtInputText = null;
	private EditText edtOutputText = null;
	private EditText edtPassText = null;
	private SharedPreferences shPreferences = null;
	private Button btnEncrypt = null;
	private Button btnDecrypt = null;
	private Button btnScanCode = null;	
	private Button btnGenCode = null;	

	private String password = "";
	private String qrPass = ""; 

	private CryptoParams cryptoParams = new CryptoParams();
	
	class CryptoEventListener implements android.view.View.OnClickListener, SeekBar.OnSeekBarChangeListener 
	{
		public void onClick(View v)
		{
			int vId = v.getId();
			password = edtPassText.getText().toString();
			String inText = edtInputText.getText().toString();
			try
			{
				if(vId==btnEncrypt.getId())
				{
					if(password+qrPass == "")
						return;
					byte[] txt = CryptoEngine.encrypt(password+qrPass, cryptoParams, inText.getBytes());
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(cryptoParams);
					byte[] rawParams = bos.toByteArray(); 
					out.close();
					bos.close();
					
					String strEnc = Base64.encode(rawParams) + "$" + Base64.encode(txt);
					Log.v("CypherText", strEnc);
					edtOutputText.setText(strEnc);
				}
				else if(vId==btnDecrypt.getId())
				{
					int devider = inText.indexOf("$");
					String rawData = null;
					if(devider > 1)
					{
						String rawParams = inText.substring(0, devider);
						rawData = inText.substring(devider+1);
						ByteArrayInputStream bis = null;
						ObjectInput in = null;
						try
						{
							bis = new ByteArrayInputStream(Base64.decode(rawParams));
							in = new ObjectInputStream(bis);
							cryptoParams = (CryptoParams) in.readObject();
						}
						catch(Exception e)
						{
							Log.e("Exception", e.toString());							
						}
						finally
						{
							cryptoParams = new CryptoParams();
							if(bis != null)
								bis.close();
							if(in != null)
								in.close();
						}
					}
					else
					{
						cryptoParams = new CryptoParams();
						rawData = inText;
					}
					
					byte[] txt = CryptoEngine.decrypt(password+qrPass, cryptoParams, Base64.decode(rawData));
					edtOutputText.setText(new String(txt, "UTF8"));
				}
				else if(vId==btnScanCode.getId())
				{
					if (android.os.Build.VERSION.SDK_INT >= 7)
					{
						IntentIntegrator integrator = new IntentIntegrator(CryptoActivity.this);
						integrator.initiateScan();
					}
					else
					{
					    // Do something different to support older versions
					}
				}
				else
				{
					if (android.os.Build.VERSION.SDK_INT >= 7)
					{
						IntentIntegrator integrator = new IntentIntegrator(CryptoActivity.this);
						integrator.shareText("test");
					}
					else
					{
					    // Do something different to support older versions
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Exception", e.toString());
			}
		}

		public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
		{
			int value = seekKeyComplexity.getProgress();
			cryptoParams.iterations = (int)Math.pow(2.0, (value + 8));
			String strComplexity[] = {
					"Neglegible/Fastest",
					"Low/Fast",
					"Enough",
					"Acceptable/Slow",
					"Good/Slower",
					"Standard/Very Slow",
					"Outstanding/Slowest" };
			tvKeyComplexity.setText("Key Complexity: " + strComplexity[value]
					+ " (" + (new Integer(cryptoParams.iterations)).toString() + ")");
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crypto_activity);
		shPreferences = getPreferences(MODE_PRIVATE);
		tvKeyComplexity = (TextView) findViewById(R.id.tvKeyComplexity);
		seekKeyComplexity = (SeekBar) findViewById(R.id.seekKeyComplexity);
		edtInputText = (EditText) findViewById(R.id.editInput);
		edtInputText.setHint("Input");
		edtOutputText = (EditText) findViewById(R.id.editOutput);
		edtOutputText.setHint("Output");
		edtPassText = (EditText) findViewById(R.id.editPassword);
		edtPassText.setHint("Password");
		btnEncrypt = (Button) findViewById(R.id.btnEncrypt);
		btnDecrypt = (Button) findViewById(R.id.btnDecrypt);
		btnScanCode = (Button) findViewById(R.id.btnScanCode);
		btnGenCode = (Button) findViewById(R.id.btnGenCode);
		
		TextView textView = new TextView(CryptoActivity.this);
		textView.setText(getPackageName());
		android.view.View.OnClickListener helloListener = new CryptoEventListener();
		btnEncrypt.setOnClickListener((android.view.View.OnClickListener) helloListener);
		btnDecrypt.setOnClickListener((android.view.View.OnClickListener) helloListener);
		btnScanCode.setOnClickListener((android.view.View.OnClickListener) helloListener);
		btnGenCode.setOnClickListener((android.view.View.OnClickListener) helloListener);
		seekKeyComplexity.setProgress(shPreferences.getInt("Slider", seekKeyComplexity.getProgress()));
		seekKeyComplexity.setOnSeekBarChangeListener((OnSeekBarChangeListener) helloListener);
		((OnSeekBarChangeListener) helloListener).onProgressChanged(seekKeyComplexity, 10, false);
/*
		String[] providers = new String[10];
		for(int idx=0;idx<10;idx++)
			providers[idx]=new String();
		int i=0;
		for (Provider p : Security.getProviders()) {
			for (Map.Entry<Object, Object> e : p.entrySet()) {
				String s = e.getKey().toString()
						+ "\t" + e.getValue().toString() + "\n";
				providers[i/100] += s;
				i++;
			}
		}
		for(int idx=0;idx<10;idx++)
			edtOutputText.append(providers[idx]);
			
*/
		//checkBox1.setChecked(shPreferences.getBoolean("CheckSave", false));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null)
		{
			qrPass = intent.getStringExtra("SCAN_RESULT");
		}
		else
		{
			edtOutputText.setText("CANCELLED");
		}
	}

	/** Called to save state */
	@Override
	public void onPause()
	{
		super.onPause();
		SharedPreferences.Editor ed = shPreferences.edit();
		ed.putInt("Slider", seekKeyComplexity.getProgress());
		ed.commit();
	}

}

