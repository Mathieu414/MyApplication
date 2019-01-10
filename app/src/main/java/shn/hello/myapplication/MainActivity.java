package shn.hello.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton startbtn, playbtn, savebtn;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    private String currentDateandTime=null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;


    private boolean isRecording = false;

    public Color baseColor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setResult(0);
        //pour lier le java et le xml :
        startbtn = (ImageButton)findViewById(R.id.btnRecord);
        playbtn = (ImageButton)findViewById(R.id.btnPlay);
        savebtn = (ImageButton)findViewById(R.id.btnSave);
        File yourAppDir = new File(Environment.getExternalStorageDirectory()+File.separator+"Audiorecorder");
        if(!yourAppDir.exists()) {
            // create an empty directory
            yourAppDir.mkdirs();
        }

        startbtn.setOnClickListener(this);
        playbtn.setOnClickListener(this);
        savebtn.setOnClickListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRecord:
                if (!isRecording) {
                    if (CheckPermissions()) {
                        if (mFileName != null)
                            new File(mFileName).delete();
                        Date d = Calendar.getInstance().getTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        currentDateandTime = sdf.format(d);
                        mFileName = Environment.getExternalStorageDirectory() + File.separator + "Audiorecorder";
                        mFileName += File.separator + "fileTest_" + currentDateandTime;

                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setOutputFile(mFileName);
                        try {
                            mRecorder.prepare();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "prepare() failed");
                        }
                        mRecorder.start();
                        startbtn.setBackgroundResource(R.drawable.redbutton);
                        startbtn.setImageResource(R.drawable.stop);
                        isRecording = true;
                        playbtn.setVisibility(View.INVISIBLE);
                        savebtn.setVisibility(View.INVISIBLE);
                    } else {
                        RequestPermissions();
                    }
                }else{
                    startbtn.setBackgroundResource(R.drawable.graybutton);
                    startbtn.setImageResource(R.drawable.microphone);
                    isRecording = false;
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    playbtn.setVisibility(View.VISIBLE);
                    savebtn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btnPlay:
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                break;

            case R.id.btnSave:
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                final Activity act = this;

                new AlertDialog.Builder(this).setTitle("Nom du fichier").setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();
                        File newFile = new File(Environment.getExternalStorageDirectory()+File.separator+"Audiorecorder"+File.separator+newName+"_"+currentDateandTime);
                        File oldFile = new File(mFileName);
                        oldFile.renameTo(newFile);
                        stopAll();
                        act.setResult(1);
                        act.finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void stopAll(){
        if (mRecorder !=null){
            mRecorder.release();
            mRecorder=null;
        }
        if (mPlayer!=null){
            mPlayer.release();
            mPlayer=null;
        }
    }

    @Override
    protected void onDestroy() {
        stopAll();
        if (mFileName!=null)
            new File(mFileName).delete();
        super.onDestroy();
    }

    @Override
    //Parce qu'il y avait plein de fuites de mémoire de partout
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        stopAll();
        if (mFileName!=null)
            new File(mFileName).delete();
        setResult(2);
        super.onBackPressed();

    }
}