package shn.hello.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Pair<String, String>> characters;

    public MyAdapter(List<Pair<String, String>> c){
        this.characters=c;
    }


    @Override
    public int getItemCount() {
        return characters.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_cell, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Pair<String, String> pair = characters.get(position);
        holder.display(pair);
    }

    public void removeItem(int position){
        new File(Environment.getExternalStorageDirectory()+File.separator+"Audiorecorder"+File.separator+characters.get(position).first+"_"+characters.get(position).second).delete();
        characters.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView description;
        private RelativeLayout viewForeground,viewBackground;

        private MediaPlayer mPlayer;

        private Pair<String, String> currentPair;

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = ((TextView) itemView.findViewById(R.id.name));
            description = ((TextView) itemView.findViewById(R.id.description));
            viewForeground = (RelativeLayout) itemView.findViewById(R.id.view_foreground);
            viewBackground = (RelativeLayout) itemView.findViewById(R.id.view_background);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPlayer != null){
                        mPlayer.release();
                        mPlayer = null;
                    }
                    mPlayer = new MediaPlayer();
                    try {
                        try {
                            mPlayer.setDataSource(Environment.getExternalStorageDirectory()+File.separator+"Audiorecorder"+File.separator+name.getText().toString()+"_"+new SimpleDateFormat("yyyyMMddHHmmss").format(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(description.getText().toString())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        Log.e("MediaPlayerErr", "prepare() failed");
                    }
                }
            });
        }

        public RelativeLayout getForeView(){
            return viewForeground;
        }

        public void display(Pair<String, String> pair) {
            currentPair = pair;
            name.setText(pair.first);
            try {
                description.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(pair.second)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
