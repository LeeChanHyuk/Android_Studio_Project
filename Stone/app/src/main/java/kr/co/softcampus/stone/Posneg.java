package kr.co.softcampus.stone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Posneg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.posneg);
        Button button1=(Button)findViewById(R.id.direct);
        Button button2=(Button)findViewById(R.id.bon);
        Button button3=(Button)findViewById(R.id.cover);
        Button button4=(Button)findViewById(R.id.newteeth);
        Button button5=(Button)findViewById(R.id.endposneg);


        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Posneg.this, Posneg_g1.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Posneg.this, Posneg_g2.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Posneg.this, Posneg_g3.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Posneg.this, Posneg_g4.class);
                startActivity(intent);
            }
        });
        button5.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });


    }
            }



