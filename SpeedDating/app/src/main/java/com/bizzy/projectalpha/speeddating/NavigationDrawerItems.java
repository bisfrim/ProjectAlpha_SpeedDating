package com.bizzy.projectalpha.speeddating;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.bizzy.projectalpha.speeddating.activities.ConnectionsActivity;
import com.bizzy.projectalpha.speeddating.activities.FindMatchActivity;
import com.bizzy.projectalpha.speeddating.activities.MainActivity;
import com.bizzy.projectalpha.speeddating.activities.MessageActivity;
import com.bizzy.projectalpha.speeddating.activities.PeopleNearMeActivity;
import com.bizzy.projectalpha.speeddating.activities.SettingsActivity;
import com.bizzy.projectalpha.speeddating.activities.UserDispatchActivity;
import com.bizzy.projectalpha.speeddating.models.User;
import com.bumptech.glide.Glide;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.view.BezelImageView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by bismark.frimpong on 12/9/2015.
 */
public class NavigationDrawerItems {

    //Drawer item code
    public final static int DRAWER_ID_PROFILE = 0;
    public final static int DRAWER_ID_START_MATCH = 1;
    public final static int DRAWER_ID_SETTINGS = 2;
    public final static int DRAWER_ID_MESSAGES = 3;
    public final static int DRAWER_ID_PEOPLE_NEAR_ME = 4;
    public final static int DRAWER_ID_USERS_PROFILE = 5;
    public final static int DRAWER_ID_CONNECTIONS = 6;
    public final static int DRAWER_ID_LOGOUT = 7;


    private static Drawer mNavigationDrawer = null;

    public static Drawer createDrawerWithBack(ActivityWithToolbar activity){

        User currentUser = (User)User.getCurrentUser();

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withEmail(currentUser.getEmail()).withName(currentUser.getNickname());

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity.getActivity())
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                }).build();

        BezelImageView profilePhotoThumb = (BezelImageView) accountHeader.getView().findViewById(R.id.material_drawer_account_header_current);
        Glide.with(activity.getActivity()).load(currentUser.getPhotoUrl()).centerCrop().into(profilePhotoThumb);

        Drawer drawer = new DrawerBuilder()
                .withActivity(activity.getActivity())
                .withAccountHeader(accountHeader)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(DRAWER_ID_PROFILE),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_people_near_me).withIcon(FontAwesome.Icon.faw_users).withIdentifier(DRAWER_ID_PEOPLE_NEAR_ME),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_start_match).withIcon(FontAwesome.Icon.faw_heart_o).withIdentifier(DRAWER_ID_START_MATCH),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_messages).withIcon(FontAwesome.Icon.faw_envelope_o).withIdentifier(DRAWER_ID_MESSAGES),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_connections).withIcon(FontAwesome.Icon.faw_users).withIdentifier(DRAWER_ID_CONNECTIONS),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_users_near_me).withIcon(FontAwesome.Icon.faw_users).withIdentifier(DRAWER_ID_SETTINGS),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(DRAWER_ID_LOGOUT)
                )
                .build();
        drawer.setSelection(activity.getDriwerId());
        drawer.setOnDrawerItemClickListener(new NavigationDrawerItemClickListener(activity.getActivity()));
        return drawer;
    }

    public static Drawer createDrawer(ActivityWithToolbar activity){

        User currentUser = (User)User.getCurrentUser();

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withEmail(currentUser.getEmail()).withName(currentUser.getNickname());

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(activity.getActivity())
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                }).build();

        BezelImageView profilePhotoThumb = (BezelImageView) accountHeader.getView().findViewById(R.id.material_drawer_account_header_current);
        Glide.with(activity.getActivity()).load(currentUser.getPhotoUrl()).centerCrop().into(profilePhotoThumb);

        Drawer drawer = new DrawerBuilder()
                .withActivity(activity.getActivity())
                .withToolbar(activity.getToolbar())
                .withAccountHeader(accountHeader)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user).withIdentifier(DRAWER_ID_PROFILE),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_people_near_me).withIcon(FontAwesome.Icon.faw_users).withIdentifier(DRAWER_ID_PEOPLE_NEAR_ME),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_start_match).withIcon(FontAwesome.Icon.faw_heart_o).withIdentifier(DRAWER_ID_START_MATCH),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_messages).withIcon(FontAwesome.Icon.faw_envelope_o).withIdentifier(DRAWER_ID_MESSAGES),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_connections).withIcon(FontAwesome.Icon.faw_users).withIdentifier(DRAWER_ID_CONNECTIONS),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_users_near_me).withIcon(FontAwesome.Icon.faw_wrench).withIdentifier(DRAWER_ID_SETTINGS),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(DRAWER_ID_LOGOUT)
                )
                .build();
        drawer.setSelection(activity.getDriwerId());
        drawer.setOnDrawerItemClickListener(new NavigationDrawerItemClickListener(activity.getActivity()));
        return drawer;
    }


    private static class NavigationDrawerItemClickListener implements Drawer.OnDrawerItemClickListener{
        private Activity mActivity;

        public NavigationDrawerItemClickListener(Activity activity){
            mActivity = activity;
        }

        @Override
        public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
            switch ((int) iDrawerItem.getIdentifier()){
                case DRAWER_ID_LOGOUT:
                    User currentUser = User.getUser();
                    currentUser.setOnline(false);
                    currentUser.saveInBackground();
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Intent loginIntent = new Intent(mActivity, UserDispatchActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mActivity.startActivity(loginIntent);
                                mActivity.finish();
                            } else {
                                Config.log(e.getMessage());
                            }
                        }
                    });
                    break;
                case DRAWER_ID_PROFILE:
                    Intent mainIntent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(mainIntent);
                    break;
                case DRAWER_ID_SETTINGS:
                    Intent userNearMeIntent = new Intent(mActivity, SettingsActivity.class);
                    mActivity.startActivity(userNearMeIntent);
                    break;
                case DRAWER_ID_START_MATCH:
                    Intent startMatchIntent = new Intent(mActivity, FindMatchActivity.class);
                    mActivity.startActivity(startMatchIntent);
                    break;
                case DRAWER_ID_MESSAGES:
                    Intent messageListIntent = new Intent(mActivity, MessageActivity.class);
                    mActivity.startActivity(messageListIntent);
                    break;
                case DRAWER_ID_CONNECTIONS:
                    Intent connectionsIntent = new Intent(mActivity, ConnectionsActivity.class);
                    mActivity.startActivity(connectionsIntent);
                    break;
                case DRAWER_ID_PEOPLE_NEAR_ME:
                    Intent people_near_meIntent = new Intent(mActivity, PeopleNearMeActivity.class);
                    mActivity.startActivity(people_near_meIntent);
                    break;
            }
            return false;
        }
    }

}
