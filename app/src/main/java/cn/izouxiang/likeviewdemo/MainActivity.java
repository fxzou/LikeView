package cn.izouxiang.likeviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.izouxiang.likeview.LikeView;
import cn.izouxiang.likeview.R;

public class MainActivity extends AppCompatActivity {
    private LikeView likeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        likeView = (LikeView) findViewById(R.id.likeview);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_add:
                likeView.add(1);
                likeView.activate();
                break;
            case R.id.btn_subtract:
                likeView.add(-1);
                likeView.deactivate();
                break;
        }
    }
}
