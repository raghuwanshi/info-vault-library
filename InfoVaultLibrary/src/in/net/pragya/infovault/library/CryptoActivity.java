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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CryptoActivity extends Activity {
	private TextView  textView1 = null;
	private TextView  textView2 = null;
	private SeekBar seekBar1 = null;
	private EditText InputText = null;
	private EditText OutputText = null;
	private EditText PassText = null;
	private SharedPreferences shPreferences = null;
	private Button button1 = null;
	private Button button2 = null;
	
	private String password = "";
	private String qrPass = ""; 

	private CryptoParams cryptParams = new CryptoParams();
	
	class HelloListener implements android.view.View.OnClickListener, SeekBar.OnSeekBarChangeListener 
	{
		public void onClick(View v)
		{
			int vId = v.getId();
			password = PassText.getText().toString();
			String inText = InputText.getText().toString();
			try
			{
				if(vId==button1.getId())
				{
					byte[] txt = CryptoEngine.encrypt(password+qrPass, cryptParams, inText.getBytes());
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos);   
					out.writeObject(cryptParams);
					byte[] rawParams = bos.toByteArray(); 
					out.close();
					bos.close();
					
					OutputText.setText(Base64.encode(rawParams) + "$" + Base64.encode(txt));
					textView2.setText(password+qrPass);
				}
				else if(vId==button2.getId())
				{
					int devider = inText.indexOf("$");
					String rawParams = inText.substring(0, devider);
					String rawData = inText.substring(devider+1);
					ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(rawParams));
					ObjectInput in = new ObjectInputStream(bis);
					cryptParams = (CryptoParams) in.readObject();
					bis.close();
					in.close();
					
					byte[] txt = CryptoEngine.decrypt(password+qrPass, cryptParams, Base64.decode(rawData));
					OutputText.setText(new String(txt, "UTF8"));
					textView2.setText(password+qrPass);
				}
				else if(vId==findViewById(R.id.button3).getId())
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
				OutputText.setText("ERROR: " + e.toString());
			}
		}

		public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
		{
			int value = seekBar1.getProgress();
			cryptParams.iterations = (int)Math.pow(2.0, (value + 8));
			String strComplexity[] = {
					"Neglegible/Fastest",
					"Low/Fast",
					"Enough",
					"Acceptable/Slow",
					"Good/Slower",
					"Standard/Very Slow",
					"Outstanding/Slowest" };
			textView1.setText("Key Complexity: " + strComplexity[value]
					+ " (" + (new Integer(cryptParams.iterations)).toString() + ")");
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
		textView1 = (TextView) findViewById(R.id.textView1);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		InputText = (EditText) findViewById(R.id.editText2);
		InputText.setHint("Input");
		OutputText = (EditText) findViewById(R.id.editText3);
		OutputText.setHint("Output");
		PassText = (EditText) findViewById(R.id.editText4);
		PassText.setHint("Password");
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);
		Button button4 = (Button) findViewById(R.id.button4);
		
		TextView textView = new TextView(CryptoActivity.this);
		textView.setText(getPackageName());
		android.view.View.OnClickListener helloListener = new HelloListener();
		button1.setOnClickListener((android.view.View.OnClickListener) helloListener);
		button2.setOnClickListener((android.view.View.OnClickListener) helloListener);
		button3.setOnClickListener((android.view.View.OnClickListener) helloListener);
		button4.setOnClickListener((android.view.View.OnClickListener) helloListener);
		seekBar1.setProgress(shPreferences.getInt("Slider", seekBar1.getProgress()));
		seekBar1.setOnSeekBarChangeListener((OnSeekBarChangeListener) helloListener);
		((OnSeekBarChangeListener) helloListener).onProgressChanged(seekBar1, 10, false);
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
			OutputText.append(providers[idx]);
			
*/
		//checkBox1.setChecked(shPreferences.getBoolean("CheckSave", false));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null)
		{
			qrPass = intent.getStringExtra("SCAN_RESULT");
			textView2.setText(qrPass);
		}
		else
		{
			OutputText.setText("CANCELLED");
		}
	}

	/** Called to save state */
	@Override
	public void onPause()
	{
		super.onPause();
		SharedPreferences.Editor ed = shPreferences.edit();
		ed.putInt("Slider", seekBar1.getProgress());
		ed.commit();
	}

}

