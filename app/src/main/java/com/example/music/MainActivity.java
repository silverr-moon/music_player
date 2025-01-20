package com.example.music;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Toolbar musichome;

    public static final int REQUEST_CODE=1;
    static ArrayList <MusicFiles> musicFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //setToolbar
        musichome=findViewById(R.id.mainToolbar);
        setSupportActionBar(musichome);

        // Disable the default title to use the custom TextView REMOVES PROJECT NAME FROM TOOLBAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //request runtime permissions
        runtimeperimissions();


    }
    private void runtimeperimissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
            } else {
                // Permission already granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPager();
            }
        }else{
            //check for read_external_storage on android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                // Permission already granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    musicFiles=getAllAudio(this);
                }
                else {
                    Toast.makeText(this, "Permission Denied.The app cannot function without this permission", Toast.LENGTH_SHORT).show();
                    showSettingDialog();
                }
        }
    }
    //to redirect to setting in case they deny permissions
    private void showSettingDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Storage permission is required to access your music files.Please grant it in app settings.")
                .setPositiveButton("Go to Settings",((dialogInterface, i) ->{
                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("Package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }))
                .setNegativeButton("Cancel",((dialogInterface, i) -> dialogInterface.dismiss()))
                .create()
                .show();
    }

    //for fragments layout songs and albums in main page
    private void initViewPager() {
        ViewPager viewPager=findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(),"Songs") ;
        viewPagerAdapter.addFragments(new AlbumsFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList <MusicFiles> tempAudioList=new ArrayList<>();
        Uri collection;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
            //for ANDROID 13+ scoped storage
            collection=MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        //used READ_EXTERNAL_STORAGE with scoped storage for versions android 11 and above
        else if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q)
            collection=MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            collection=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


        String[] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,//for path
                MediaStore.Audio.Media.ARTIST
        };

        String selection=null;
        Cursor cursor=context.getContentResolver().query(collection,projection,selection,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                String album=cursor.getString(0);
                String title=cursor.getString(1);
                String duration=cursor.getString(2);
                String path=cursor.getString(3);
                String artist= cursor.getString(4);

                MusicFiles musicFiles=new MusicFiles(path,title,album,artist,duration); //note the order of it to be same as in the musicfiles.java constructor
                //log e to check
                Log.e("Path:"+path,"Album:"+album);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }
}