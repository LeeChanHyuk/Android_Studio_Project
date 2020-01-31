package kr.co.softcampus.stone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Findcarries extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        Button b1 = (Button)findViewById(R.id.takepic);
        Button b2 = (Button)findViewById(R.id.selectpic);
        Button b3 = (Button)findViewById(R.id.back);

        b1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비중입니다..!", Toast.LENGTH_SHORT).show();

            }
        });

        b2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Findcarries.this, Check2.class);
                startActivity(intent);
            }
        });

        b3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

    }
}
