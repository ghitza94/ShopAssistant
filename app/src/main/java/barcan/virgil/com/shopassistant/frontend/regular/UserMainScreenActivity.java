package barcan.virgil.com.shopassistant.frontend.regular;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import barcan.virgil.com.shopassistant.R;
import barcan.virgil.com.shopassistant.backend.Controller;
import barcan.virgil.com.shopassistant.backend.service.LocationReceiver;
import barcan.virgil.com.shopassistant.frontend.SettingsActivity;
import barcan.virgil.com.shopassistant.model.Constants;
import barcan.virgil.com.shopassistant.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserMainScreenActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private Intent intentLocationService;

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        controller = Controller.getInstance();

        //Start the location service?
        if (controller.startService()) {
            startLocationService();
        }
        else {
            stopLocationService();
        }

        //Choose the right fragment to show:
        // if the activity was opened by a notification, show the shopping list fragment
        // else show the home fragment
        String fragmentToShowString = "UserHomeFragment";
        String shopProductsToShow = "ALL";
        if (getIntent() != null && getIntent().getStringExtra(Constants.FRAGMENT_TO_START) != null) {
            fragmentToShowString = getIntent().getStringExtra(Constants.FRAGMENT_TO_START);
            shopProductsToShow = getIntent().getStringExtra(Constants.SHOP_PRODUCTS_TO_SHOW);
        }
        //controller.setShopToShow(shopProductsToShow);

        if (fragmentToShowString.equals("UserHomeFragment")) {
            UserHomeFragment fragmentHome = new UserHomeFragment();
            FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
            fragmentTransactionHome.replace(R.id.frame, fragmentHome);
            fragmentTransactionHome.commit();
        }
        else {
            UserShoppingListFragment fragmentShoppingList = new UserShoppingListFragment();
            fragmentShoppingList.setShopToShow(shopProductsToShow);
            FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
            fragmentTransactionHome.replace(R.id.frame, fragmentShoppingList);
            fragmentTransactionHome.commit();
        }

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        addDataToNavigationViewHeader(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                }
                else {
                    menuItem.setChecked(true);
                }

                drawerLayout.closeDrawers();

                System.out.println("UserMainScreenActivity.onNavigationItemSelected: menuItem.getItemId()=" + menuItem.getItemId());

                controller.setShopToShow("ALL");

                switch (menuItem.getItemId()) {
                    case R.id.goToHome:
                        Toast.makeText(getApplicationContext(), "Home selected", Toast.LENGTH_SHORT).show();

                        startHomeFragment();

                        return true;

                    case R.id.goToProducts:
                        Toast.makeText(getApplicationContext(), "Search products selected", Toast.LENGTH_SHORT).show();

                        startProductsFragment();

                        return true;

                    case R.id.shoppingList:
                        Toast.makeText(getApplicationContext(), "Shopping list selected", Toast.LENGTH_SHORT).show();

                        startShoppingListFragment();

                        return true;

                    case R.id.goToAccount:
                        Toast.makeText(getApplicationContext(), "Account selected", Toast.LENGTH_SHORT).show();

                        startAccountFragment();

                        return true;

                    case R.id.goToSettings:
                        Toast.makeText(getApplicationContext(), "Notifications selected", Toast.LENGTH_SHORT).show();

                        openPreferenceActivity();

                    default:
                        System.out.println("UserMainScreenActivity.onNavigationItemSelected: ERROR! Id not supported!");
                }
                return false;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes.
                // As we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer opens.
                // As we don't want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        System.out.println("UserMainScreenActivity.onCreateOptionsMenu: menu=" + menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        System.out.println("UserMainScreenActivity.onCreateOptionsMenu: getComponentName=" + getComponentName());
        System.out.println("UserMainScreenActivity.onCreateOptionsMenu: searchableInfo=" + searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                System.out.println("MainActivity.onOptionsItemSelected: Search pressed!");
                //TODO: React to search
                //TODO: Create fragments and classes
                startProductsFragment();
                break;

            case R.id.action_shopping_list:
                System.out.println("MainActivity.onOptionsItemSelected: Shopping List pressed!");
                //TODO: React to shopping list
                //TODO: Create fragments and classes
                startShoppingListFragment();
                break;

            case R.id.action_settings:
                System.out.println("MainActivity.onOptionsItemSelected: Settings pressed!");
                //TODO: React to settings

                //Just a small test
                openPreferenceActivity();

                break;

            default:
                System.out.println("MainActivity.onOptionsItemSelected: ERROR! MenuItem id not recognized!");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method starts the LocationService
     * The LocationService gets GPS position and checks if shops are close to the user
     * If a shop that sells something the user wants is close, the Service notifies
     */
    private void startLocationService() {
        System.out.println("UserMainScreenActivity.startLocationService");
        if (!isMyServiceRunning(LocationReceiver.class)) {
            intentLocationService = createExplicitIntentFromImplicitIntent(getApplicationContext(), new Intent("barcan.virgil.com.shopassistant.backend.service"));
            startService(intentLocationService);

            //Intent intent = new Intent(this, LocationService.class);
            //PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //alarmManager.cancel(pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
        }
    }

    /**
     * This method is used to stop the LocationService
     */
    private void stopLocationService() {
        System.out.println("UserMainScreenActivity.stopLocationService");
        if (isMyServiceRunning(LocationReceiver.class)) {
            stopService(intentLocationService);
        }
    }

    /**
     * This function converts an implicit intent (given as a string) to an explicit one
     * @param context the context
     * @param implicitIntent the implicit intent
     * @return the explicit Intent
     */
    private Intent createExplicitIntentFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(implicitIntent, 0);

        //Make sure only one match was found
        if (resolveInfos == null || resolveInfos.size() != 1) {
            return null;
        }

        //Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfos.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName componentName = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(componentName);

        return explicitIntent;
    }

    /**
     * This function is used to check if the service is running already, to not start it again
     * @param serviceClass the class of the service
     * @return true if the service is running, false otherwise
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("UserMainScreenActivity.isMyServiceRunning: yes, the " + service.service.getClassName() + " is running");
                return true;
            }
        }
        return false;
    }

    /**
     * This method adds user related info to the navigation header
     * @param navigationView the NavigationView whose header we want to process
     */
    private void addDataToNavigationViewHeader(NavigationView navigationView) {
        User connectedUser = controller.getConnectedUser();

        View headerView = LayoutInflater.from(this).inflate(R.layout.navigation_drawer_header, null);

        System.out.println("UserMainScreenActivity.addDataToNavigationViewHeader: connectedUser=" + connectedUser);

        if (connectedUser != null) {
            TextView textViewFullName = (TextView) headerView.findViewById(R.id.fullname);
            textViewFullName.setText(connectedUser.getFullName());

            TextView textViewUsername = (TextView) headerView.findViewById(R.id.username);
            textViewUsername.setText(connectedUser.getUsername());

            //TODO: Add user image to the header
            CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.circleView);
            circleImageView.setImageResource(R.drawable.ic_account_circle);

            navigationView.addHeaderView(headerView);
        }
        else {
            System.out.println("UserMainScreenActivity.addDataToNavigationViewHeader: ERROR! No user is connected! This should not be possible!");
        }
    }

    /**
     * This method starts the Home fragment when the user clicks on the Home button
     * in the NavigationView (Drawer)
     */
    private void startHomeFragment() {
        UserHomeFragment fragmentHome = new UserHomeFragment();
        FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
        fragmentTransactionHome.replace(R.id.frame, fragmentHome);
        fragmentTransactionHome.addToBackStack("Home");
        fragmentTransactionHome.commit();
    }

    /**
     * This method starts the Products fragment when the user clicks on the Products button
     * in the NavigationView (Drawer)
     */
    private void startProductsFragment() {
        UserProductsFragment fragmentProducts = new UserProductsFragment();
        FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
        fragmentTransactionHome.replace(R.id.frame, fragmentProducts);
        fragmentTransactionHome.addToBackStack("Products");
        fragmentTransactionHome.commit();
    }

    /**
     * This method starts the ShoppingList fragment when the user clicks on the ShoppingList button
     * in the NavigationView (Drawer)
     */
    private void startShoppingListFragment() {
        UserShoppingListFragment fragmentShoppingList = new UserShoppingListFragment();
        fragmentShoppingList.setShopToShow("ALL");
        FragmentTransaction fragmentTransactionShoppingList = getSupportFragmentManager().beginTransaction();
        fragmentTransactionShoppingList.replace(R.id.frame, fragmentShoppingList);
        fragmentTransactionShoppingList.addToBackStack("Shopping List");
        fragmentTransactionShoppingList.commit();
    }

    /**
     * This method starts the Account fragment when the user clicks on the Account button
     * in the NavigationView (Drawer)
     */
    private void startAccountFragment() {
        UserAccountFragment fragmentAccount = new UserAccountFragment();
        FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
        fragmentTransactionHome.replace(R.id.frame, fragmentAccount);
        fragmentTransactionHome.addToBackStack("Account");
        fragmentTransactionHome.commit();
    }

    /**
     * This method starts the Settings fragment when the user clicks on the Settings button
     * in the NavigationView (Drawer)
     */
    private void startSettingsFragment() {
        /*
        UserNotificationsFragment fragmentSettings = new UserNotificationsFragment();

        FragmentTransaction fragmentTransactionHome = getSupportFragmentManager().beginTransaction();
        fragmentTransactionHome.replace(R.id.frame, fragmentSettings);
        fragmentTransactionHome.addToBackStack("Settings");
        fragmentTransactionHome.commit();
        */
    }

    /**
     * This method starts the Settings activity when the user clicks on the Settings button
     * in the NavigationView (Drawer)
     */
    private void openPreferenceActivity() {
        Intent intentSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(intentSettingsActivity);
    }
}