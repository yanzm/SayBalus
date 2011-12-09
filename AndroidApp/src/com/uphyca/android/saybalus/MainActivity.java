/*
 * Copyright (C) 2011 yanzm, uPhyca Inc.,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uphyca.android.saybalus;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AccessoryActivity {

    private static final int REQUEST_CODE = 0;

    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "唱える");
                    startActivityForResult(intent, REQUEST_CODE);

                } catch (ActivityNotFoundException e) {
                    // このインテントに応答できるアクティビティがインストールされていない場合
                    Toast.makeText(MainActivity.this, "ActivityNotFoundException", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        enableControls(false);
    }

    private boolean sendCommandFlag = false;
    
    @Override
    public void onResume() {
        super.onResume();
        
        if(sendCommandFlag) {
            sendCommandFlag = false;
            byte command = 0x1;
            byte value = 0x1;
            sendCommand(command, value);
            Toast.makeText(this, "バルス成功！", Toast.LENGTH_LONG).show();            
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results.size() > 0) {
                if (results.get(0).equals("バルス")) {
                    sendCommandFlag = true;
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void enableControls(boolean enable) {
        if (enable) {
            Log.d(TAG, "connected");
        } else {
            Log.d(TAG, "not connected");
        }
        mButton.setEnabled(enable);
    }

    protected void handleLedMessage(LedMsg l) {
        if (l.isOn()) {
            // トーストを使って結果を表示
            Toast.makeText(this, "バルス成功！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "バルス失敗！", Toast.LENGTH_LONG).show();
        }
    }

}