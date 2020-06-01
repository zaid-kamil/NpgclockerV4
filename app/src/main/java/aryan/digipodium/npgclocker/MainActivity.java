package aryan.digipodium.npgclocker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int userType = Helper.getUserType(this);
        initFirebase();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (userType == 0) {
            int[] top_levels = {R.id.fc_home, R.id.fc_history, R.id.fc_profile, R.id.fc_users};
            initNavigation(navigationView, navController, R.navigation.faculty_navigation, R.menu.activity_main_fc_drawer, top_levels);
        } else {
            int[] top_levels = {R.id.st_home, R.id.st_profile, R.id.st_folders, R.id.st_history};
            initNavigation(navigationView, navController, R.navigation.student_navigation, R.menu.activity_main_st_drawer, top_levels);
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initNavigation(NavigationView navigationView, final NavController navController, int navgraph, int drawer_menu, int[] top_levels) {
        navController.setGraph(navgraph);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(drawer_menu);

        mAppBarConfiguration = new AppBarConfiguration.Builder(top_levels).setDrawerLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    private void exec_logout() {
        Helper.clearUserType(this);
        mAuth.signOut();
        Intent i = new Intent(this, ChoiceActivity.class);
        finish();
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            exec_logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
