package kr.co.softcampus.stone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Waytouse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.waytouse);
        Button button1=(Button)findViewById(R.id.string);
        Button button2=(Button)findViewById(R.id.brush);
        Button button3=(Button)findViewById(R.id.tongue);
        Button button4=(Button)findViewById(R.id.endwaytouse);

        button1.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Waytouse.this, Waytouse_string.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Waytouse.this, Waytouse_brush.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Waytouse.this, Waytouse_tongue.class);
                startActivity(intent);
            }
        });


        button4.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

     }
}
