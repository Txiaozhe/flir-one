package com.flir.flirone;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class NfcHandlerActivity extends AppCompatActivity {

    //nfc
    private TextView nfcTView;
    private NfcAdapter nfcAdapter;
    private String nfcResult;

    //返回主程序按钮
    private Button bt_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_handler);

        //nfc
        nfcTView=(TextView)findViewById(R.id.show_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            nfcTView.setText("设备不支持NFC！");
            return;
        } else if (nfcAdapter!=null&&!nfcAdapter.isEnabled()) {
            nfcTView.setText("请在系统设置中先启用NFC功能！");
            return;
        } else {
            nfcTView.setText("NFC可用");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                    NfcInfo nfcInfo = readFromTag(getIntent());
                    nfcResult = nfcInfo.readResult;
                    nfcTView.setText("NFC扫描结果：" + nfcResult + "\n当前车厢号：硬卧1234");
                    Log.i("action0", getIntent().getAction());
                }
                Log.i("action", getIntent().getAction());
            }
        }).start();
    }

    class NfcInfo {
        boolean record;
        String readResult;
    }

    private NfcInfo readFromTag(Intent intent){
        NfcInfo nfcInfo = new NfcInfo();
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage mNdefMsg = (NdefMessage)rawArray[0];
        NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
        try {
            if(mNdefRecord != null){
                nfcInfo.readResult = new String(mNdefRecord.getPayload(),"UTF-8");
                nfcInfo.record = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        };
        return nfcInfo;
    }

    public void returnToPreview(View v) {
        Intent intent = new Intent(NfcHandlerActivity.this, PreviewActivity.class);
        intent.putExtra("nfcresult", nfcResult);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(NfcHandlerActivity.this, PreviewActivity.class);
            intent.putExtra("nfcresult", nfcTView.getText().toString());
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
