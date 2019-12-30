package be.pxl.unionapp.activities;

import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import be.pxl.unionapp.R;

// Hier worden de Fragments (Home, Read, Insert, Logout) in vervat
public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    AppBarConfiguration mAppBarConfiguration;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar); // Staat in app_bar_main.xml
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout); // id van activity_main
        navigationView = findViewById(R.id.nav_view); // Staat in activity_main.xml

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_show_members, R.id.nav_add_member, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment); // Staat in content_main
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    // Settings Menu (nog implementeren)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
