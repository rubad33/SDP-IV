package com.bongoacademy.masterjuba_new;

import static android.view.View.GONE;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.BUFFERING;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.CUED;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.ENDED;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.PAUSED;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.PLAYING;
import static com.bongoacademy.masterjuba_new.MasterJubaYoutube.UNSTARTED;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

        /*
    >>Source Code provided by
    >>Jubayer Hossain [https://www.facebook.com/JubayerHossainbd]
    >>Please don't forget to put a review on my site [www.bongoacademy.com]
    >>Share my course with your friends on Facebook
    >>Your positive words help me doing even better
     */

    MasterJubaYoutube webYouTube;
    LinearLayout layoutContainer;
    LottieAnimationView myLottie;
    TextView durationTextView;
    SeekBar seekBar;
    ScrollView mainScroll;
    RelativeLayout _rootLay, layPlayerRoot, seek_lay;
    InterstitialAd mInterstitialAd;
    LinearLayout layPlayer;
    ImageView imngClosePlayer, imgPlayPause, imgPrevious, imgNext, mainCover, repeat_one, imgFullscreen;

    public static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    HashMap<String, String> hashMap;
    int PLAYING_NOW = 0;
    boolean playingVideo = false;
    public static int SCREEN_WIDTH = 0;
    boolean YOUTUBE_PLAYER = false;
    public static boolean REPEAT_ONE = false;
    String CURRENT_VIDEO_ID = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Home.CategoryClicked = -10;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


       //tvDate = findViewById(R.id.tvDate);
        layoutContainer = findViewById(R.id.layoutContainer);
        _rootLay = findViewById(R.id._rootLay);
        layPlayer = findViewById(R.id.layPlayer);
        imngClosePlayer = findViewById(R.id.imngClosePlayer);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        imgNext = findViewById(R.id.imgNext);
        imgPrevious = findViewById(R.id.imgPrevious);
        mainCover = findViewById(R.id.mainCover);
        layPlayerRoot = findViewById(R.id.layPlayerRoot);
        myLottie = findViewById(R.id.myLottie);
        mainScroll = findViewById(R.id.mainScroll);
        repeat_one = findViewById(R.id.repeat_one);
        seek_lay = findViewById(R.id.seek_lay);
        seekBar = findViewById(R.id.seekBar);
        durationTextView = findViewById(R.id.durationTextView);
        mainPlayerLay143 = findViewById(R.id.mainPlayerLay143);
        layBottomBar = findViewById(R.id.layBottomBar);
        imgFullscreen = findViewById(R.id.imgFullscreen);


        //init multiple players with this method
        initMultiPlayers();

        //Calling addVideos method by which we will make a video list
        makeListView();


        //Adding Cover Dynamically ----------------------------------------------
        Random r = new Random();
        int low = 0;
        int high = arrayList.size()-1;
        int randomNum = r.nextInt(high-low) + low;

        HashMap<String, String> hashMap = arrayList.get(randomNum);
        String vdo_id = hashMap.get("vdo_id");

        // Youtube thumnail link is like
        //https://i.ytimg.com/vi/<VIDEO ID>/0.jpg
        String thumb_link = "https://i.ytimg.com/vi/"+vdo_id+"/0.jpg";
        Picasso.get().
                load(""+thumb_link)
                .placeholder(R.mipmap.ic_launcher)
                .into(mainCover);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




        imngClosePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();
                playingVideo = false;
            }
        });






        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v!=null && v.getTag()!=null){
                    String tag = v.getTag().toString();
                    if (tag.contains("PLAYING")){

                         if (webYouTube!=null) webYouTube.pause();
                        else Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_LONG).show();

                    }else if (tag.contains("PAUSED")){

                        if (webYouTube!=null) webYouTube.play();
                        else Toast.makeText(MainActivity.this, "Please wait...", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextVideo();
            }
        });

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousVideo();
            }
        });



        repeat_one.setBackgroundColor(Color.TRANSPARENT);
        REPEAT_ONE = false;
        repeat_one.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!REPEAT_ONE){
                    REPEAT_ONE = true;
                    //repeat_one.setBackgroundColor(Color.parseColor("#44ffffff"));
                    repeat_one.setColorFilter( Color.parseColor("#ffcdd2"));
                    Toast.makeText(MainActivity.this, "Done!\nRepeat one enabled", Toast.LENGTH_SHORT).show();
                }else{
                    REPEAT_ONE = false;
                    //repeat_one.setBackgroundColor(Color.TRANSPARENT);
                    repeat_one.setColorFilter( Color.parseColor("#ffffff"));
                }

            }
        });




        myLottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });





        //------------------------------------------
        imgFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!FULLSCREEN) FULLSCREEN = true;
               else FULLSCREEN = false;
               setPlayerScreenOrientation();

            }
        });

        layBottomBar.setVisibility(View.GONE);


        // onTouch player items visible
        if (webYouTube!=null) {
            webYouTube.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Toast.makeText(getApplicationContext(), "Touch", Toast.LENGTH_SHORT).show();
                    if ( !TOUCH_EXECUTING && layBottomBar.getVisibility()==GONE){
                        TOUCH_EXECUTING = true;
                        layBottomBar.setVisibility(View.VISIBLE);
                        layBottomBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.up_from_bottom));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TOUCH_EXECUTING = false;
                            }
                        }, 4000);

                    }


                    return false;
                }
            });
        }









    } // End of onCreate Bundle




    private void makeListView(){



        ExpandableHeightGridView mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setExpanded(true);
        //------------
        MyAdapter myAdapter = new MyAdapter();
        mainGrid.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLottie.setVisibility(View.GONE);
                mainScroll.setVisibility(View.VISIBLE);
            }
        }, 5000);

    }

    ///==============================================




    //==============================================
    private void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null ) {
            mInterstitialAd.show(this);
        }
    }
    //============================================




    ///==============================================
    ///==============================================

    private void initMultiPlayers(){

        //Adding Master Juba YouTube Player
        webYouTube = new MasterJubaYoutube(MainActivity.this);
        layPlayerRoot.addView(webYouTube);
        webYouTube.setVisibility(View.GONE);
        webYouTube.initialize();
        setupMasterJubaYoutube();

        //Getting screen height and width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;


        // Master Juba Player listener handler
        setMasterJubaPlayerListener();







    } //end of initMultiplayer ------------------------------------>

    ///==============================================
    ///==============================================






    private int currentSeconds = 0, videoDuration = 1;
    private float videoBuffered = 0;
    private boolean isUserChangingTouch = false;
    private Handler secondsHandler = new Handler();


    private void setMasterJubaPlayerListener(){
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++
        webYouTube.setOnPlaybackStateChange(new Runnable() {
            @Override
            public synchronized void run() {
                switch (webYouTube.getPlaybackState()) {

                    case BUFFERING:
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.buffer);
                            imgPlayPause.setTag("BUFFERING");
                        }
                        break;

                    case CUED:
                        //Toast.makeText(getApplicationContext(), "Cued : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        break;

                    case ENDED:
                        // Toast.makeText(getApplicationContext(), "Ended : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        if (REPEAT_ONE) webYouTube.play();
                        else playNextVideo();


                        break;

                    case PAUSED:
                        // Toast.makeText(getApplicationContext(), "Paused : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.icon_play);
                            imgPlayPause.setTag("PAUSED");
                        }

                        break;


                    case PLAYING:
                        if (!YOUTUBE_PLAYER && imgPlayPause != null ){
                            imgPlayPause.setImageResource(R.drawable.icon_pause);
                            imgPlayPause.setTag("PLAYING");
                            if (seek_lay!=null) seek_lay.setVisibility(View.VISIBLE);
                        }
                        break;

                    case UNSTARTED:
                        //Toast.makeText(getApplicationContext(), "Unstarted : "+Service_Overlay.playerView.getPlaybackState(), Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoDuration != 0) {
                    currentSeconds = (int) ((progress / 100f) * videoDuration);
                    if (isUserChangingTouch) {
                        webYouTube.seekTo(currentSeconds, false);
                    } else {
                        webYouTube.seekTo(currentSeconds, true);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserChangingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserChangingTouch = false;
                currentSeconds = (int) ((seekBar.getProgress() / 100f) * videoDuration);
                webYouTube.seekTo(currentSeconds, true);
                webYouTube.play();
            }
        });


        webYouTube.setCurrentTimeListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                currentSeconds = (int) n;
            }
        }); //TODO: Put this in player view itself
        webYouTube.setDurationListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                videoDuration = (int) n;
            }
        }); //TODO: Put this in player view itself
        webYouTube.setVideoLoadedFractionListener(new MasterJubaYoutube.NumberReceivedListener() {
            @Override
            public void onReceive(float n) {
                videoBuffered = n;
            }
        }); //TODO: Put this in player view itself



        //Update seekbar every second with handler // -- juba@
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (seekBar!=null){
                    if (seekBar.getVisibility()==View.VISIBLE){

                        if (!isUserChangingTouch) {
                            //=======================================================
                            updateSeekBarUI();
                            updatePlayerComponents();
                        }
                        //=======================================================

                    }
                }
                secondsHandler.postDelayed(this, 1000);
            }
        };
        secondsHandler.postDelayed(r, 200);





    }

    ///==============================================
    ///========================================================================================


    private void updateSeekBarUI() {
        //Log.d("Service", "Updating SeekUI, " + videoDuration);
        if (isUserChangingTouch || videoDuration == 0) {
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
            currentSeconds = 0;
            updateTextUI();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(100 * currentSeconds / videoDuration, true);
        } else {
            seekBar.setProgress(100 * currentSeconds / videoDuration);
        }
        seekBar.setSecondaryProgress((int) (100 * videoBuffered));
        updateTextUI();
    }

    private void updateTextUI() {
        String text = currentSeconds / 60 + ":" + currentSeconds % 60;
        if (durationTextView!=null) durationTextView.setText(text);

    }


    /**
     * Sets up the player view
     */
    public void setupMasterJubaYoutube() {
        webYouTube.setOnPlayerReadyRunnable(new Runnable() {
            @Override
            public void run() {
                webYouTube.pause();
                webYouTube.showOverlay();
            }
        });

    }


    //=================================================================






    ///==============================================
    ///==============================================


    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(){
            this.inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.video_item, parent, false);

            TextView tvTitle = convertView.findViewById(R.id.tvTitle);
            TextView tvDescription = convertView.findViewById(R.id.tvDescription);
            ImageView imgThumb = convertView.findViewById(R.id.imgThumb);
            RelativeLayout layItem = convertView.findViewById(R.id.layItem);

            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_grid);
            animation.setStartOffset(position*200);
            convertView.startAnimation(animation);

            HashMap<String, String> hashMap = arrayList.get(position);
            String vdo_id = hashMap.get("vdo_id");
            String vdo_title = hashMap.get("vdo_title");
            String vdo_desciption = hashMap.get("vdo_desciption");

            tvTitle.setText(vdo_title);
            tvDescription.setText(vdo_desciption);

            // Youtube thumnail link is like
            //https://i.ytimg.com/vi/<VIDEO ID>/0.jpg
            String thumb_link = "https://i.ytimg.com/vi/"+vdo_id+"/0.jpg";
            Picasso.get().
                    load(""+thumb_link)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imgThumb);

            layItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Play Video
                    PLAYING_NOW = position;
                    playVideo(vdo_id);
                    showInterstitial();

                }
            });




            return convertView;
        }
    }



    //================================================
    private void playVideo(String video_id){
        seek_lay.setVisibility(View.GONE);
        CURRENT_VIDEO_ID = video_id;

        try{
            playWithMasterJubaPlayer(video_id);
            setPlayerScreenOrientation();

        } catch(Exception e){
            Toast.makeText(MainActivity.this, "Please wait..", Toast.LENGTH_LONG).show();

        }



    }



    //===================================================================================

    private void playWithMasterJubaPlayer(String video_id){


        YOUTUBE_PLAYER = false;
        //=====================================================================
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(150, 50);
        layoutParams1.width = SCREEN_WIDTH;
        layoutParams1.height = (int) (SCREEN_WIDTH * 9/16);
        webYouTube.setLayoutParams(layoutParams1);
        //webYouTube.setVisibility(View.GONE);

        layPlayer.setVisibility(View.VISIBLE);
        layPlayer.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in));
        webYouTube.setVisibility(View.VISIBLE);
        try{
            webYouTube.loadVideoById(video_id, 0, "default");
            webYouTube.play();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Please try again!", Toast.LENGTH_LONG).show();
        }
        playingVideo = true;
        webYouTube.bringToFront();
    }



    //===================================================================================
    //===================================================================================
    //===================================================================================
    //===================================================================================
    //================================================

    private void closePlayer(){

        if(webYouTube!=null) webYouTube.pause();
        layPlayer.setVisibility(View.GONE);
        layPlayer.clearAnimation();
    }



    //===================================================================================
    //================================================



    //===================================================================================
    //================================================
    public  void exitMasterJuba(){
        final RelativeLayout.LayoutParams layoutParams22 = (RelativeLayout.LayoutParams) webYouTube.getLayoutParams();
        layoutParams22.width =0;
        layoutParams22.height =0;
        webYouTube.setLayoutParams(layoutParams22);
    }

    //==============================================



    //=================================================================





//=================================================
    private void playNextVideo(){
        if(PLAYING_NOW >= (arrayList.size()-1))
            PLAYING_NOW = 0;
        else PLAYING_NOW++;

        HashMap<String, String> hashMap = arrayList.get(PLAYING_NOW);
        String vdo_id = hashMap.get("vdo_id");
        playVideo(vdo_id);
    }


    private void playPreviousVideo(){
        if(PLAYING_NOW > 0){
            PLAYING_NOW--;
            HashMap<String, String> hashMap = arrayList.get(PLAYING_NOW);
            String vdo_id = hashMap.get("vdo_id");
            playVideo(vdo_id);
        }else{
            Toast.makeText(MainActivity.this, "Playing the first video", Toast.LENGTH_LONG).show();
        }

    }






    ///==============================================
    ///==============================================
    //===================================================

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //==========================================================================
//==========================================================================




    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

    //=======================================================
    //method to show a dialog in android
    private void showYoutubeInsallDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Install Youtube App");
        alertDialog.setMessage(getString(R.string.app_name)+" will not work if you don't have youtube official app installed on your device");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Install NOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        openStoreIntent("com.google.android.youtube");
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Exit App",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Exit App
                        if(Build.VERSION.SDK_INT>=16 && Build.VERSION.SDK_INT<21){
                            finishAffinity();
                        } else if(Build.VERSION.SDK_INT>=21){
                            finishAndRemoveTask();
                        }

                    }
                });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //===============================================================



    //=================================================
    ///==============================================
    ///==============================================
    ///==============================================
    //try to download youtube app from app stores
    private void openStoreIntent(String app_package){
        String url="";
        Intent storeintent=null;
        try {
            url = "market://details?id="+app_package;
            storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(storeintent);
        } catch ( final Exception e ) {
            url = "https://play.google.com/store/apps/details?id="+app_package;
            storeintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            storeintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(storeintent);
        }

    }
    ///==============================================
    ///==============================================
    ///==============================================
    ///==============================================




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (secondsHandler != null) secondsHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    //--------------------------------------------------------------







    RelativeLayout mainPlayerLay143;
    LinearLayout layBottomBar;
    boolean FULLSCREEN = false;

    private void setPlayerScreenOrientation() {

        if (FULLSCREEN) {
            //make fullscreen now
            //=====================================================================
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mainPlayerLay143.setLayoutParams(layoutParams2);
            //=====================================================================
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            webYouTube.setLayoutParams(layoutParams1);

            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layBottomBar.setLayoutParams(layoutParams3);


            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            //Toast.makeText(MainActivity.this, "FullScreen", Toast.LENGTH_SHORT).show();
        } else {
            //exit fullscreen -- normal mode
            //=====================================================================
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) mainPlayerLay143.getLayoutParams();
            layoutParams2.width = SCREEN_WIDTH;
            //layoutParams2.height = (int) (SCREEN_WIDTH * 9 / 16);
            layoutParams2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mainPlayerLay143.setLayoutParams(layoutParams2);
            //=====================================================================
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(150, 50);
            layoutParams1.width = SCREEN_WIDTH;
            layoutParams1.height = (int) (SCREEN_WIDTH * 9 / 16);
            webYouTube.setLayoutParams(layoutParams1);

            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams3.addRule(RelativeLayout.BELOW, R.id.layPlayerRoot);
            layBottomBar.setLayoutParams(layoutParams3);

            //=====================================================================
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //Toast.makeText(MainActivity.this, "Exit FullScreen", Toast.LENGTH_SHORT).show();
        }


    }


    //--------------------------------------------------------------------------------------------------
    //===================================================================================================

    boolean UI_UPDATE_RUNNING = false;
    boolean TOUCH_EXECUTING = false;
    private void updatePlayerComponents(){
        if (webYouTube!=null && !UI_UPDATE_RUNNING && !TOUCH_EXECUTING){

            if(layPlayer.getVisibility()==View.VISIBLE && !FULLSCREEN && layBottomBar.getVisibility()==View.GONE){
                UI_UPDATE_RUNNING = true;
                Handler handler = new Handler(Looper.myLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layBottomBar.clearAnimation();
                        layBottomBar.setVisibility(View.VISIBLE);
                        UI_UPDATE_RUNNING = false;
                    }
                }, 50);

            }


           else if(layPlayer.getVisibility()==View.VISIBLE && FULLSCREEN && layBottomBar.getVisibility()==View.VISIBLE){
                UI_UPDATE_RUNNING = true;
                Handler handler = new Handler(Looper.myLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layBottomBar.clearAnimation();
                        //layBottomBar.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out));
                        layBottomBar.setVisibility(View.GONE);
                        UI_UPDATE_RUNNING = false;
                    }
                }, 5000);

            }



        }

    }

    //--------------------------------------------------------------------------------------------------
    //===================================================================================================








}