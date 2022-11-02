package com.example.musicapp.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.musicapp.R;
import com.example.musicapp.data_local.DataLocalManager;
import com.example.musicapp.databinding.ActivityMainBinding;
import com.example.musicapp.ui.home.tab_home.song.Song;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        DataLocalManager.setListSong(getListSong());
    }


    public List<Song> getListSong() {
        List<Song> list = new ArrayList<>();
        list.add(new Song("Ánh sao và bầu trời", "T.R.I", R.drawable.anh_sao_va_bau_troi, R.raw.anh_sao_va_bau_troi));
        list.add(new Song("Bao tiền một mớ bình yên", "14 Casper, Bon", R.drawable.bao_tien_mot_mo_binh_yen, R.raw.bao_tien_mot_mo_binh_yen));
        list.add(new Song("Chuyện đôi ta", "Emcee L (Da LAB)", R.drawable.chuyen_doi_ta, R.raw.chuyen_doi_ta));
        list.add(new Song("Có ai thương em như anh", "Tóc Tiên", R.drawable.co_ai_thuong_em, R.raw.co_ai_thuong_em_nhu_anh));
        list.add(new Song("Mơ", "Vũ Cát Tường", R.drawable.mo, R.raw.mo));
        list.add(new Song("Nắng đã làm má em thêm hồng", "Charles", R.drawable.nang_da_lam_ma_em_ung_hong, R.raw.nang_da_lam_ma_em_them_hong));
        list.add(new Song("Thắc mắc", "Thịnh Suy", R.drawable.thac_mac, R.raw.thac_mac));
        list.add(new Song("Thanh Xuân", "Da LAB", R.drawable.thanh_xuan, R.raw.thanh_xuan));
        list.add(new Song("Vài câu nói có khiến người thay đổi", "GREY-D (Đoàn Thế Lân)", R.drawable.vai_cau_noi_co_khien_nguoi_thay_doi, R.raw.vai_cau_noi_co_khien_nguoi_thay_doi));
        list.add(new Song("Vì anh đâu có biết", "Madihu, Vũ", R.drawable.vi_anh_dau_co_biet, R.raw.vi_anh_dau_co_biet));

        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}