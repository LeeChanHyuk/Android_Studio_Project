package kr.co.softcampus.stone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Youngteeth extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.youngteeth);

        Button button1 = (Button) findViewById(R.id.why);
        Button button2 = (Button) findViewById(R.id.timing);
        Button button3 = (Button) findViewById(R.id.endyoungteeth);

        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Youngteeth.this, Youngteeth_y1.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Youngteeth.this, Youngteeth_y2.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                }
        });
    }
}
