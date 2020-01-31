package kr.co.softcampus.stone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button3=(Button)findViewById(R.id.waytouse);
        Button button5=(Button)findViewById(R.id.posneg);
        Button button6=(Button)findViewById(R.id.childteeth);
        Button button7=(Button)findViewById(R.id.checkteeth);

        button3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Waytouse.class);
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Posneg.class);
                startActivity(intent);
            }
        });
        button6.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Youngteeth.class);
                startActivity(intent);
            }
        });

        button7.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, Findcarries.class);
                startActivity(intent);
            }
        });
    }
}
