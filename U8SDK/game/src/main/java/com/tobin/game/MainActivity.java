package com.tobin.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.game_login:

                break;
            case R.id.game_pay:
                // 支付


                break;
            case R.id.game_visible_float:


                break;
            case R.id.game_gone_float:

                break;
            default:
                break;

        }
    }


    private void initView(){
        findViewById(R.id.game_login).setOnClickListener(this);
        findViewById(R.id.game_pay).setOnClickListener(this);
        findViewById(R.id.game_visible_float).setOnClickListener(this);
        findViewById(R.id.game_gone_float).setOnClickListener(this);
    }

}
