package shn.hello.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Pair<String, String>> filesList = new ArrayList<Pair<String, String>>();

        File path = new File(Environment.getExternalStorageDirectory()+File.separator+"Audiorecorder");
        File list[] = path.listFiles();
        for( int i=0; i< list.length; i++)
        {
            String[] infos = list[i].getName().split("_");
            if (infos.length==2)
            filesList.add(Pair.create(infos[0],infos[1]));
        }

        final RecyclerView rv = (RecyclerView) findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MyAdapter(filesList));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this,MainActivity.class);
        startActivityForResult(i,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }





}
